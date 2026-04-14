package br.edu.ufc.quixada.sd.t1.stream;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class ComputerStreamTcpServer {
    private ComputerStreamTcpServer() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printUsage();
            return;
        }

        String mode = args[0].toLowerCase();
        int port = Integer.parseInt(args[1]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor de stream aguardando conexão na porta " + port + "...");
            try (Socket socket = serverSocket.accept()) {
                if ("send".equals(mode)) {
                    Computer[] computers = ComputerSamples.defaultSet();
                    ComputerOutputStream output = new ComputerOutputStream(computers, computers.length, socket.getOutputStream());
                    output.writeObjects();
                    output.flush();
                    System.out.println("Dados enviados para o cliente TCP.");
                } else if ("receive".equals(mode)) {
                    ComputerInputStream input = new ComputerInputStream(socket.getInputStream());
                    Computer[] received = input.readObjects();
                    System.out.println("Dados recebidos via TCP:");
                    for (Computer computer : received) {
                        System.out.println(computer);
                    }
                } else {
                    System.out.println("Modo inválido: " + mode);
                    printUsage();
                }
            }
        }
    }

    private static void printUsage() {
        System.out.println("Uso:");
        System.out.println("  send <porta>    -> servidor envia objetos para cliente TCP");
        System.out.println("  receive <porta> -> servidor recebe objetos enviados por cliente TCP");
    }
}