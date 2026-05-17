package br.edu.ufc.quixada.sd.t2.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Ponto de entrada do servidor RMI.
 *
 * O servidor faz três coisas:
 *   1. Cria um Registro RMI (RMI Registry) — funciona como uma "lista telefônica"
 *      onde clientes procuram objetos pelo nome.
 *   2. Cria uma instância do objeto remoto (ComputerServiceImpl).
 *   3. Registra o objeto com um nome, para que clientes possam encontrá-lo.
 *
 * Uso: java RmiServer [porta]
 *   Porta padrão: 1099 (porta padrão do Java RMI)
 */
public class RmiServer {

    private static final int PORTA_PADRAO = 1099;

    public static void main(String[] args) throws Exception {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : PORTA_PADRAO;

        // Cria o registro RMI nesta máquina, na porta especificada
        Registry registry = LocateRegistry.createRegistry(porta);
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       Servidor RMI - Trabalho 2          ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("Registro RMI iniciado na porta " + porta);

        // Cria o objeto remoto — este é o objeto REAL que vive no servidor
        ComputerServiceImpl servico = new ComputerServiceImpl();

        // Registra o objeto com o nome "ComputerService"
        // Clientes usarão este nome para localizá-lo
        registry.rebind("ComputerService", servico);

        System.out.println("Objeto 'ComputerService' registrado com sucesso.");
        System.out.println("Aguardando requisições dos clientes...\n");
    }
}
