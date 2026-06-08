package br.edu.ufc.quixada.sd.t2.protocol;

import br.edu.ufc.quixada.sd.t1.protocol.JsonCodec;
import br.edu.ufc.quixada.sd.t2.rmi.IComputerService;
import br.edu.ufc.quixada.sd.t2.rmi.RemoteObjectRef;
import br.edu.ufc.quixada.sd.t2.rmi.RmiMessage;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Métodos:
 *   - doOperation  → usado pelo CLIENTE para invocar um método remoto
 *   - getRequest   → usado pelo SERVIDOR ao receber uma requisição
 *   - sendReply    → usado pelo SERVIDOR ao enviar a resposta
 *
 */
public class RequestReplyProtocol {

    private final AtomicInteger idCounter = new AtomicInteger(0);

    // LADO CLIENTE

    /**
     * Envia uma mensagem de requisição para o objeto remoto e retorna a resposta.
     *
     * @param ref       referência ao objeto remoto (onde está e qual é)
     * @param methodId  nome do método a invocar
     * @param arguments argumentos do método serializados em JSON
     * @return resultado do método serializado em JSON
     */
    public byte[] doOperation(RemoteObjectRef ref, String methodId, byte[] arguments)
            throws Exception {

        int reqId = idCounter.incrementAndGet();

        // 1. Empacota a requisição na estrutura de mensagem do protocolo
        RmiMessage request = new RmiMessage(
                RmiMessage.TYPE_REQUEST, reqId,
                ref.getObjectName(), methodId, arguments);

        System.out.println("\n[CLIENTE → SERVIDOR] " + request);

        // 2. Localiza o objeto remoto no Registro RMI (PASSAGEM POR REFERÊNCIA)
        //    O objeto real está no servidor. O cliente recebe apenas um STUB,
        //    que é uma referência remota, não uma cópia do objeto.
        IComputerService service = (IComputerService) Naming.lookup(ref.toUrl());

        // 3. Invoca o método no servidor. O Java RMI cuida do transporte.
        Object result = dispatch(service, methodId, arguments);

        // 4. Serializa o resultado em JSON (representação externa de dados)
        byte[] replyBytes = result != null
                ? JsonCodec.toJson(result).getBytes(StandardCharsets.UTF_8)
                : new byte[0];

        // 5. Empacota a resposta
        RmiMessage reply = new RmiMessage(
                RmiMessage.TYPE_REPLY, reqId,
                ref.getObjectName(), methodId, replyBytes);

        System.out.println("[SERVIDOR → CLIENTE] " + reply);

        return replyBytes;
    }

    // LADO SERVIDOR

    /**
     * Obtém uma requisição recebida de um cliente.
     *
     * No Java RMI, esse passo acontece automaticamente quando o framework
     * desserializa a mensagem de rede e chama o método correspondente na
     * implementação do servidor.
     *
     * @param methodId  nome do método invocado
     * @param arguments argumentos recebidos em JSON
     * @return a mensagem de requisição estruturada
     */
    public RmiMessage getRequest(String methodId, byte[] arguments) {
        int reqId = idCounter.incrementAndGet();
        RmiMessage request = new RmiMessage(
                RmiMessage.TYPE_REQUEST, reqId,
                "ComputerService", methodId, arguments);
        System.out.println("  [SERVIDOR] getRequest()  → " + request);
        return request;
    }

    /**
     * Envia a mensagem de resposta para o cliente.
     *
     * No Java RMI, esse passo acontece automaticamente quando o método remoto
     * retorna um valor — o framework serializa o resultado e envia pela rede.
     *
     * @param reply      resultado serializado em JSON
     * @param clientHost endereço IP do cliente (gerenciado pelo Java RMI)
     * @param clientPort porta do cliente (gerenciado pelo Java RMI)
     */
    public void sendReply(byte[] reply, InetAddress clientHost, int clientPort) {
        String destino = clientHost != null
                ? clientHost.getHostAddress() + ":" + clientPort
                : "gerenciado pelo Java RMI";
        System.out.println("  [SERVIDOR] sendReply()   → " + reply.length +
                           " bytes para [" + destino + "]");
    }

    // AUXILIAR — despacha a chamada para o método correto no serviço

    /**
     * Mapeia o nome do método (String) para a chamada tipada na interface remota.
     * Isso representa o que a camada de skeleton faz no protocolo RMI clássico.
     */
    private Object dispatch(IComputerService service, String methodId, byte[] args)
            throws Exception {
        switch (methodId) {
            case "listarComputadores":
                return service.listarComputadores();

            case "buscarPorCodigo":
                return service.buscarPorCodigo(new String(args, StandardCharsets.UTF_8));

            case "adicionarComputador":
                // args já é o JSON do Computer (passagem por valor com repr. externa)
                service.adicionarComputador(args);
                return null;

            case "contarPorCategoria":
                return service.contarPorCategoria();

            case "removerComputador":
                return service.removerComputador(new String(args, StandardCharsets.UTF_8));

            default:
                throw new IllegalArgumentException("Método desconhecido: " + methodId);
        }
    }
}
