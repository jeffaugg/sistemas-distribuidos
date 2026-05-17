package br.edu.ufc.quixada.sd.t2.rmi;

import java.nio.charset.StandardCharsets;

/**
 * Mensagem trocada entre cliente e servidor no protocolo requisição-resposta.
 *
 * Estrutura (seção 5.2 do livro Coulouris):
 * ┌─────────────────┬──────────────────────────────┐
 * │ messageType     │ 0 = Requisição, 1 = Resposta  │
 * │ requestId       │ número único da requisição    │
 * │ objectReference │ nome do objeto remoto         │
 * │ methodId        │ nome do método a invocar      │
 * │ arguments       │ argumentos em JSON (bytes)    │
 * └─────────────────┴──────────────────────────────┘
 */
public class RmiMessage {
    public static final int TYPE_REQUEST = 0;
    public static final int TYPE_REPLY   = 1;

    private final int    messageType;
    private final int    requestId;
    private final String objectReference;
    private final String methodId;
    private final byte[] arguments;

    public RmiMessage(int messageType, int requestId,
                      String objectReference, String methodId, byte[] arguments) {
        this.messageType     = messageType;
        this.requestId       = requestId;
        this.objectReference = objectReference;
        this.methodId        = methodId;
        this.arguments       = arguments != null ? arguments : new byte[0];
    }

    public int    getMessageType()     { return messageType; }
    public int    getRequestId()       { return requestId; }
    public String getObjectReference() { return objectReference; }
    public String getMethodId()        { return methodId; }
    public byte[] getArguments()       { return arguments; }

    public String getArgumentsAsString() {
        if (arguments.length == 0) return "(vazio)";
        return new String(arguments, StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        String tipo = messageType == TYPE_REQUEST ? "REQUISIÇÃO" : "RESPOSTA  ";
        String args = arguments.length > 80
                ? new String(arguments, 0, 80, StandardCharsets.UTF_8) + "..."
                : getArgumentsAsString();
        return String.format("[%s id=%d obj='%s' method='%s' args=%s]",
                tipo, requestId, objectReference, methodId, args);
    }
}
