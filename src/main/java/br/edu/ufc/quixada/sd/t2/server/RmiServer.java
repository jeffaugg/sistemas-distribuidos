package br.edu.ufc.quixada.sd.t2.server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Ponto de entrada do servidor RMI.
 *
 * O servidor faz três coisas:
 *   1. Cria um Registro RMI onde clientes procuram objetos pelo nome.
 *   2. Cria uma instância do objeto remoto.
 *   3. Registra o objeto com um nome, para que clientes possam encontrá-lo.
 *
 * Uso: java RmiServer [porta]
 *   Porta padrão: 1099
 */
public class RmiServer {

    private static final int PORTA_PADRAO = 1099;

    public static void main(String[] args) throws Exception {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : PORTA_PADRAO;

        String hostAnunciado = descobrirHostAnunciado();
        System.setProperty("java.rmi.server.hostname", hostAnunciado);

        // Cria o registro RMI nesta máquina, na porta especificada
        Registry registry = LocateRegistry.createRegistry(porta);
        System.out.println("Servidor RMI");
        System.out.println("Registro RMI iniciado na porta " + porta);
        System.out.println("Host anunciado pelo RMI: " + hostAnunciado);

        // Cria o objeto remoto — este é o objeto REAL que vive no servidor
        ComputerServiceImpl servico = new ComputerServiceImpl();

        // Registra o objeto com o nome "ComputerService"
        // Clientes usarão este nome para localizá-lo
        registry.rebind("ComputerService", servico);

        System.out.println("Objeto 'ComputerService' registrado com sucesso.");
        System.out.println("Aguardando requisições dos clientes...\n");
    }

    private static String descobrirHostAnunciado() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                continue;
            }

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();

                if (address instanceof Inet4Address
                        && !address.isLoopbackAddress()
                        && !address.isAnyLocalAddress()
                        && !address.isLinkLocalAddress()) {
                    return address.getHostAddress();
                }
            }
        }

        throw new IllegalStateException("Nenhum endereço IPv4 de rede foi encontrado para anunciar no RMI.");
    }
}
