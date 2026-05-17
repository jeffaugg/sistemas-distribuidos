package br.edu.ufc.quixada.sd.t2.client;

import br.edu.ufc.quixada.sd.t1.protocol.JsonCodec;
import br.edu.ufc.quixada.sd.t2.domain.Notebook;
import br.edu.ufc.quixada.sd.t2.protocol.RequestReplyProtocol;
import br.edu.ufc.quixada.sd.t2.rmi.RemoteObjectRef;

import java.nio.charset.StandardCharsets;

/**
 * Cliente RMI — demonstra todas as operações disponíveis no serviço remoto.
 *
 * O cliente NUNCA tem o objeto real. Ele possui apenas uma REFERÊNCIA REMOTA
 * (stub), que é um procurador que envia chamadas pela rede ao servidor.
 *
 * Uso: java RmiClient [host] [porta]
 *   Padrão: localhost:1099
 */
public class RmiClient {

    public static void main(String[] args) throws Exception {
        String host  = args.length > 0 ? args[0] : "localhost";
        int    porta = args.length > 1 ? Integer.parseInt(args[1]) : 1099;

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       Cliente RMI - Trabalho 2           ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // RemoteObjectRef: "onde encontrar" o objeto remoto
        RemoteObjectRef ref = new RemoteObjectRef("ComputerService", host, porta);
        System.out.println("Referência remota: " + ref + "\n");

        RequestReplyProtocol protocolo = new RequestReplyProtocol();

        // =====================================================================
        // Operação 1 — listarComputadores
        // =====================================================================
        secao("1. Listar todos os computadores");
        byte[] resultado = protocolo.doOperation(ref, "listarComputadores", new byte[0]);
        System.out.println("Resultado JSON (" + resultado.length + " bytes):");
        System.out.println(new String(resultado, StandardCharsets.UTF_8));

        // =====================================================================
        // Operação 2 — buscarPorCodigo
        // =====================================================================
        secao("2. Buscar computador por código: NB-001");
        String codigo = "NB-001";
        resultado = protocolo.doOperation(ref, "buscarPorCodigo",
                codigo.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encontrado: " + new String(resultado, StandardCharsets.UTF_8));

        // =====================================================================
        // Operação 3 — adicionarComputador (PASSAGEM POR VALOR via JSON)
        // =====================================================================
        secao("3. Adicionar novo computador (passagem por valor + JSON)");
        Notebook novoNotebook = new Notebook("NB-999", "Samsung", "Galaxy Book3", 16, 512, 5200.00);

        // Serializa o objeto para JSON ANTES de enviar — representação externa de dados
        String jsonDoComputador = JsonCodec.toJson(novoNotebook);
        System.out.println("Objeto serializado para JSON:");
        System.out.println(jsonDoComputador);

        byte[] jsonBytes = jsonDoComputador.getBytes(StandardCharsets.UTF_8);
        protocolo.doOperation(ref, "adicionarComputador", jsonBytes);

        // Verifica se foi adicionado
        resultado = protocolo.doOperation(ref, "buscarPorCodigo",
                "NB-999".getBytes(StandardCharsets.UTF_8));
        System.out.println("Verificação — encontrado: " + new String(resultado, StandardCharsets.UTF_8));

        // =====================================================================
        // Operação 4 — contarPorCategoria
        // =====================================================================
        secao("4. Contar computadores por categoria");
        resultado = protocolo.doOperation(ref, "contarPorCategoria", new byte[0]);
        System.out.println("Contagem por categoria: " + new String(resultado, StandardCharsets.UTF_8));

        // =====================================================================
        // Operação 5 — removerComputador
        // =====================================================================
        secao("5. Remover computador: NB-999");
        resultado = protocolo.doOperation(ref, "removerComputador",
                "NB-999".getBytes(StandardCharsets.UTF_8));
        System.out.println("Removido: " + new String(resultado, StandardCharsets.UTF_8));

        // Confirma remoção
        resultado = protocolo.doOperation(ref, "contarPorCategoria", new byte[0]);
        System.out.println("Contagem após remoção: " + new String(resultado, StandardCharsets.UTF_8));

        System.out.println("\n✓ Demonstração concluída.");
    }

    private static void secao(String titulo) {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  " + titulo);
        System.out.println("══════════════════════════════════════════");
    }
}
