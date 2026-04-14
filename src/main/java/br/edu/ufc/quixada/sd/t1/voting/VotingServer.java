package br.edu.ufc.quixada.sd.t1.voting;

import br.edu.ufc.quixada.sd.t1.protocol.JsonCodec;
import br.edu.ufc.quixada.sd.t1.protocol.ReplyMessage;
import br.edu.ufc.quixada.sd.t1.protocol.RequestMessage;
import br.edu.ufc.quixada.sd.t1.protocol.RequestType;
import br.edu.ufc.quixada.sd.t1.protocol.UserRole;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VotingServer {
    private final int port;
    private final String multicastAddress;
    private final int multicastPort;
    private final ElectionService electionService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public VotingServer(int port, Duration votingDuration, String multicastAddress, int multicastPort) {
        this.port = port;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.electionService = new ElectionService(votingDuration);
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> handle(socket));
            }
        }
    }

    private void handle(Socket socket) {
        try (Socket autoClose = socket;
             BufferedReader reader = new BufferedReader(new InputStreamReader(autoClose.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(autoClose.getOutputStream(), StandardCharsets.UTF_8))) {

            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                writeReply(writer, new ReplyMessage(false, "Requisição vazia."));
                return;
            }

            RequestMessage request = JsonCodec.fromJson(requestLine, RequestMessage.class);
            ReplyMessage reply = process(request);
            writeReply(writer, reply);
        } catch (Exception exception) {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                writeReply(writer, new ReplyMessage(false, exception.getMessage()));
            } catch (IOException ignored) {
            }
        }
    }

    private ReplyMessage process(RequestMessage request) throws IOException {
        ReplyMessage reply = new ReplyMessage();
        reply.setSuccess(true);

        switch (request.getType()) {
            case LOGIN: {
                String username = request.getUsername();
                UserRole role = request.getRole() == null ? UserRole.VOTER : request.getRole();
                VotingSession session = electionService.login(username, role);
                reply.setMessage("Login realizado.");
                reply.setPayload(Map.of(
                        "token", session.getToken(),
                        "role", session.getRole().name(),
                        "deadline", electionService.getVotingDeadline().toString(),
                        "multicastAddress", multicastAddress,
                        "multicastPort", multicastPort
                ));
                return reply;
            }
            case LIST_CANDIDATES: {
                VotingSession session = requireSession(request.getSessionToken());
                reply.setMessage("Candidatos retornados.");
                reply.setPayload(Map.of("candidates", electionService.listCandidates(session)));
                return reply;
            }
            case VOTE: {
                VotingSession session = requireSession(request.getSessionToken());
                String candidateId = String.valueOf(request.getPayload().get("candidateId"));
                Vote vote = electionService.vote(session, candidateId);
                reply.setMessage("Voto registrado.");
                reply.setPayload(Map.of("vote", vote));
                return reply;
            }
            case ADD_CANDIDATE: {
                VotingSession session = requireSession(request.getSessionToken());
                Candidate candidate = JsonCodec.fromJson(JsonCodec.toJson(request.getPayload().get("candidate")), Candidate.class);
                electionService.addCandidate(session, candidate);
                reply.setMessage("Candidato adicionado.");
                return reply;
            }
            case REMOVE_CANDIDATE: {
                VotingSession session = requireSession(request.getSessionToken());
                String candidateId = String.valueOf(request.getPayload().get("candidateId"));
                electionService.removeCandidate(session, candidateId);
                reply.setMessage("Candidato removido.");
                return reply;
            }
            case SEND_NOTICE: {
                VotingSession session = requireSession(request.getSessionToken());
                if (session.getRole() != UserRole.ADMIN) {
                    throw new IllegalArgumentException("Apenas administrador pode enviar notas.");
                }
                String notice = String.valueOf(request.getPayload().get("notice"));
                publishNotice(session.getUsername() + ": " + notice);
                reply.setMessage("Nota enviada em multicast.");
                return reply;
            }
            case GET_RESULTS: {
                reply.setMessage("Resultados calculados.");
                reply.setPayload(Map.of("results", electionService.calculateResults()));
                return reply;
            }
            case CLOSE_VOTING: {
                electionService.closeVoting();
                reply.setMessage("Fechamento solicitado.");
                reply.setPayload(new LinkedHashMap<>(electionService.buildStatus()));
                return reply;
            }
            default:
                throw new IllegalArgumentException("Tipo de request não suportado.");
        }
    }

    private VotingSession requireSession(String token) {
        VotingSession session = electionService.findSession(token);
        if (session == null) {
            throw new IllegalArgumentException("Sessão não encontrada.");
        }
        return session;
    }

    private void writeReply(BufferedWriter writer, ReplyMessage reply) throws IOException {
        writer.write(JsonCodec.toJson(reply));
        writer.newLine();
        writer.flush();
    }

    private void publishNotice(String notice) throws IOException {
        byte[] data = notice.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(multicastAddress), multicastPort);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        int votingSeconds = args.length > 1 ? Integer.parseInt(args[1]) : 300;
        String multicastAddress = args.length > 2 ? args[2] : "230.0.0.1";
        int multicastPort = args.length > 3 ? Integer.parseInt(args[3]) : 6000;

        VotingServer server = new VotingServer(port, Duration.ofSeconds(votingSeconds), multicastAddress, multicastPort);
        server.seedDefaultCandidates();
        server.start();
    }

    private void seedDefaultCandidates() {
        VotingSession admin = electionService.login("system", UserRole.ADMIN);
        electionService.addCandidate(admin, new Candidate("1", "Alice", "Candidata 1"));
        electionService.addCandidate(admin, new Candidate("2", "Bruno", "Candidato 2"));
        electionService.addCandidate(admin, new Candidate("3", "Carla", "Candidata 3"));
    }
}