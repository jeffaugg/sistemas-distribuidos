package br.edu.ufc.quixada.sd.t1.voting;

import br.edu.ufc.quixada.sd.t1.protocol.ReplyMessage;
import br.edu.ufc.quixada.sd.t1.protocol.UserRole;

import java.io.IOException;
import java.util.Scanner;

public final class VotingCli {
    private VotingCli() {
    }

    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        VotingClient client = new VotingClient(host, port);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Perfil (VOTER/ADMIN): ");
            UserRole role = UserRole.valueOf(scanner.nextLine().trim().toUpperCase());

            ReplyMessage loginReply = client.login(username, role);
            if (!loginReply.isSuccess()) {
                System.out.println("Falha no login: " + loginReply.getMessage());
                return;
            }

            String token = String.valueOf(loginReply.getPayload().get("token"));
            System.out.println("Login ok. Token: " + token);
            System.out.println("Multicast: " + loginReply.getPayload().get("multicastAddress") + ":" + loginReply.getPayload().get("multicastPort"));

            if (role == UserRole.ADMIN) {
                runAdminLoop(client, scanner, token);
            } else {
                runVoterLoop(client, scanner, token, loginReply);
            }
        }
    }

    private static void runVoterLoop(VotingClient client, Scanner scanner, String token, ReplyMessage loginReply) throws IOException {
        while (true) {
            System.out.println("\nMenu Eleitor");
            System.out.println("1 - Listar candidatos");
            System.out.println("2 - Votar");
            System.out.println("3 - Ver resultados");
            System.out.println("4 - Escutar 1 nota multicast");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    printReply(client.listCandidates(token));
                    break;
                case "2":
                    System.out.print("ID do candidato: ");
                    printReply(client.vote(token, scanner.nextLine().trim()));
                    break;
                case "3":
                    printReply(client.getResults());
                    break;
                case "4":
                    listenOneNotice(loginReply);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }

    private static void runAdminLoop(VotingClient client, Scanner scanner, String token) throws IOException {
        while (true) {
            System.out.println("\nMenu Admin");
            System.out.println("1 - Listar candidatos");
            System.out.println("2 - Adicionar candidato");
            System.out.println("3 - Remover candidato");
            System.out.println("4 - Enviar nota multicast");
            System.out.println("5 - Encerrar votação");
            System.out.println("6 - Ver resultados");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    printReply(client.listCandidates(token));
                    break;
                case "2": {
                    System.out.print("ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Nome: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Descrição: ");
                    String description = scanner.nextLine().trim();
                    printReply(client.addCandidate(token, new Candidate(id, name, description)));
                    break;
                }
                case "3":
                    System.out.print("ID do candidato: ");
                    printReply(client.removeCandidate(token, scanner.nextLine().trim()));
                    break;
                case "4":
                    System.out.print("Mensagem: ");
                    printReply(client.sendNotice(token, scanner.nextLine().trim()));
                    break;
                case "5":
                    printReply(client.closeVoting(token));
                    break;
                case "6":
                    printReply(client.getResults());
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }

    private static void listenOneNotice(ReplyMessage loginReply) {
        String multicastAddress = String.valueOf(loginReply.getPayload().get("multicastAddress"));
        int multicastPort = Integer.parseInt(String.valueOf(loginReply.getPayload().get("multicastPort")));

        System.out.println("Aguardando nota multicast...");
        try (MulticastNoticeListener listener = new MulticastNoticeListener(multicastAddress, multicastPort)) {
            String notice = listener.receiveOnce();
            System.out.println("Nota recebida: " + notice);
        } catch (IOException exception) {
            System.out.println("Falha ao receber nota multicast: " + exception.getMessage());
        }
    }

    private static void printReply(ReplyMessage reply) {
        System.out.println("success=" + reply.isSuccess());
        System.out.println("message=" + reply.getMessage());
        System.out.println("payload=" + reply.getPayload());
    }
}