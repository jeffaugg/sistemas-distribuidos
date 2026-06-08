package br.edu.ufc.quixada.sd.t1.stream;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public final class ComputerStreamOutputDemo {
    private ComputerStreamOutputDemo() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            printUsage();
            return;
        }

        String mode = args[0].toLowerCase();
        Computer[] computers = ComputerSamples.defaultSet();

        switch (mode) {
            case "stdout":
                writeToStream(computers, System.out);
                break;
            case "file":
                if (args.length < 2) {
                    System.out.println("Informe o caminho do arquivo.");
                    printUsage();
                    return;
                }
                try (FileOutputStream fileOutputStream = new FileOutputStream(args[1])) {
                    writeToStream(computers, fileOutputStream);
                }
                System.out.println("Arquivo gerado em: " + args[1]);
                break;
            case "tcp":
                if (args.length < 3) {
                    System.out.println("Informe host e porta para envio TCP.");
                    printUsage();
                    return;
                }
                String host = args[1];
                int port = Integer.parseInt(args[2]);
                try (Socket socket = new Socket(host, port)) {
                    writeToStream(computers, socket.getOutputStream());
                }
                System.out.println("Objetos enviados via TCP para " + host + ":" + port);
                break;
            default:
                System.out.println("Modo inválido: " + mode);
                printUsage();
                break;
        }
    }

    private static void writeToStream(Computer[] computers, OutputStream outputStream) throws IOException {
        ComputerOutputStream customOutput = new ComputerOutputStream(computers, computers.length, outputStream);
        customOutput.writeObjects();
        customOutput.flush();
    }

    private static void printUsage() {
        System.out.println("Uso:");
        System.out.println("  stdout");
        System.out.println("  file <arquivo_saida>");
        System.out.println("  tcp <host> <porta>");
    }
}