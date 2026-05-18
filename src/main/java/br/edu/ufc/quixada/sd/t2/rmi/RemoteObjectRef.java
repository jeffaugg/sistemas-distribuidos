package br.edu.ufc.quixada.sd.t2.rmi;

/**
 * Referência a um objeto remoto.
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
