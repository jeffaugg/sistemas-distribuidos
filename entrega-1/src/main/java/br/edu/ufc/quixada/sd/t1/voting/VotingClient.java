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
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class VotingClient {
    private final String host;
    private final int port;

    public VotingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ReplyMessage login(String username, UserRole role) throws IOException {
        RequestMessage request = new RequestMessage(RequestType.LOGIN);
        request.setUsername(username);
        request.setRole(role);
        return send(request);
    }

    public ReplyMessage listCandidates(String token) throws IOException {
        RequestMessage request = new RequestMessage(RequestType.LIST_CANDIDATES);
        request.setSessionToken(token);
        return send(request);
    }

    public ReplyMessage vote(String token, String candidateId) throws IOException {
        RequestMessage request = new RequestMessage(RequestType.VOTE);
        request.setSessionToken(token);
        request.getPayload().put("candidateId", candidateId);
        return send(request);
    }

    public ReplyMessage addCandidate(String token, Candidate candidate) throws IOException {
        RequestMessage request = new RequestMessage(RequestType.ADD_CANDIDATE);
        request.setSessionToken(token);
        request.getPayload().put("candidate", candidate);
        return send(request);
    }

    public ReplyMessage removeCandidate(String token, String candidateId) throws IOException {
        RequestMessage request = new RequestMessage(RequestType.REMOVE_CANDIDATE);
        request.setSessionToken(token);
        request.getPayload().put("candidateId", candidateId);
        return send(request);
    }

    public ReplyMessage sendNotice(String token, String notice) throws IOException {
        RequestMessage request = new RequestMessage(RequestType.SEND_NOTICE);
        request.setSessionToken(token);
        request.getPayload().put("notice", notice);
        return send(request);
    }

    public ReplyMessage getResults() throws IOException {
        return send(new RequestMessage(RequestType.GET_RESULTS));
    }

    public ReplyMessage closeVoting(String token) throws IOException {
        RequestMessage request = new RequestMessage(RequestType.CLOSE_VOTING);
        request.setSessionToken(token);
        return send(request);
    }

    private ReplyMessage send(RequestMessage request) throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            writer.write(JsonCodec.toJson(request));
            writer.newLine();
            writer.flush();

            String response = reader.readLine();
            if (response == null) {
                throw new IOException("Servidor não retornou resposta.");
            }
            return JsonCodec.fromJson(response, ReplyMessage.class);
        }
    }

    public static void main(String[] args) throws IOException {
        VotingClient client = new VotingClient(args.length > 0 ? args[0] : "127.0.0.1", args.length > 1 ? Integer.parseInt(args[1]) : 5000);
        ReplyMessage login = client.login("aluno", UserRole.VOTER);
        System.out.println(login.getMessage());
        System.out.println(login.getPayload());
    }
}