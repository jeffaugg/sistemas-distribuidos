package br.edu.ufc.quixada.sd.t1.stream;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public final class ComputerStreamInputDemo {
    private ComputerStreamInputDemo() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            printUsage();
            return;
        }

        String mode = args[0].toLowerCase();
        switch (mode) {
            case "stdin":
                readFromStream(System.in);
                break;
            case "file":
                if (args.length < 2) {
                    System.out.println("Informe o caminho do arquivo.");
                    printUsage();
                    return;
                }
                try (FileInputStream fileInputStream = new FileInputStream(args[1])) {
                    readFromStream(fileInputStream);
                }
                break;
            case "tcp":
                if (args.length < 3) {
                    System.out.println("Informe host e porta para leitura TCP.");
                    printUsage();
                    return;
                }
                String host = args[1];
                int port = Integer.parseInt(args[2]);
                try (Socket socket = new Socket(host, port)) {
                    readFromStream(socket.getInputStream());
                }
                break;
            default:
                System.out.println("Modo inválido: " + mode);
                printUsage();
                break;
        }
    }

    private static void readFromStream(InputStream inputStream) throws IOException {
        ComputerInputStream customInput = new ComputerInputStream(inputStream);
        Computer[] computers = customInput.readObjects();
        for (Computer computer : computers) {
            System.out.println(computer);
        }
    }

    private static void printUsage() {
        System.out.println("Uso:");
        System.out.println("  stdin");
        System.out.println("  file <arquivo_entrada>");
        System.out.println("  tcp <host> <porta>");
    }
}