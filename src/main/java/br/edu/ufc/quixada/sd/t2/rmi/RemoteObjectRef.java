package br.edu.ufc.quixada.sd.t2.rmi;

/**
 * Referência a um objeto remoto.
 *
 * Contém as informações necessárias para localizar um objeto no servidor:
 * nome do objeto no registro RMI, endereço do host e porta.
 *
 * Analogia: é como o endereço de uma casa. Você não tem a casa na mão,
 * mas sabe onde encontrá-la.
 */
public class RemoteObjectRef {
    private final String objectName;
    private final String host;
    private final int port;

    public RemoteObjectRef(String objectName, String host, int port) {
        this.objectName = objectName;
        this.host = host;
        this.port = port;
    }

    public String getObjectName() { return objectName; }
    public String getHost()       { return host; }
    public int    getPort()       { return port; }

    /** Retorna a URL usada para localizar o objeto no registro RMI. */
    public String toUrl() {
        return "rmi://" + host + ":" + port + "/" + objectName;
    }

    @Override
    public String toString() {
        return "RemoteObjectRef{url='" + toUrl() + "'}";
    }
}
