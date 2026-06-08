package br.edu.ufc.quixada.sd.t2.server;

import br.edu.ufc.quixada.sd.t1.protocol.JsonCodec;
import br.edu.ufc.quixada.sd.t2.domain.Computer;
import br.edu.ufc.quixada.sd.t2.domain.Estoque;
import br.edu.ufc.quixada.sd.t2.domain.Mainframe;
import br.edu.ufc.quixada.sd.t2.domain.Microcomputador;
import br.edu.ufc.quixada.sd.t2.domain.Notebook;
import br.edu.ufc.quixada.sd.t2.protocol.RequestReplyProtocol;
import br.edu.ufc.quixada.sd.t2.rmi.IComputerService;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

/**
 * Implementação do serviço remoto de computadores.
 *
 * Estende UnicastRemoteObject, o que faz com que o Java RMI:
 *   - Exporte automaticamente este objeto (torna-o acessível pela rede)
 *   - Crie um skeleton (esqueleto) que recebe chamadas e as delega aqui
 *
 * O objeto REAL vive aqui, no servidor.
 * O cliente nunca tem uma cópia — ele apenas possui um STUB (referência remota).
 */
public class ComputerServiceImpl extends UnicastRemoteObject implements IComputerService {

    private final Estoque estoque;
    private final RequestReplyProtocol serverProtocol = new RequestReplyProtocol();

    public ComputerServiceImpl() throws RemoteException {
        this.estoque = new Estoque("Estoque Central");
        carregarDadosIniciais();
    }

    // Métodos remotos — chamados pelo cliente via stub

    @Override
    public List<Computer> listarComputadores() throws RemoteException {
        serverProtocol.getRequest("listarComputadores", new byte[0]);

        List<Computer> resultado = estoque.listar();

        byte[] reply = JsonCodec.toJson(resultado).getBytes(StandardCharsets.UTF_8);
        serverProtocol.sendReply(reply, null, 0);

        return resultado;
    }

    @Override
    public Computer buscarPorCodigo(String codigo) throws RemoteException {
        serverProtocol.getRequest("buscarPorCodigo",
                codigo.getBytes(StandardCharsets.UTF_8));

        Computer resultado = estoque.buscarPorCodigo(codigo);

        byte[] reply = JsonCodec.toJson(resultado).getBytes(StandardCharsets.UTF_8);
        serverProtocol.sendReply(reply, null, 0);

        return resultado;
    }

    /**
     * Adiciona um computador recebido como JSON.
     */
    @Override
    public void adicionarComputador(byte[] computerJson) throws RemoteException {
        serverProtocol.getRequest("adicionarComputador", computerJson);

        Computer computer = desserializarComputador(computerJson);
        estoque.adicionar(computer);
        System.out.println("  [SERVIDOR] Computador adicionado: " + computer);

        serverProtocol.sendReply(new byte[0], null, 0);
    }

    @Override
    public Map<String, Integer> contarPorCategoria() throws RemoteException {
        serverProtocol.getRequest("contarPorCategoria", new byte[0]);

        Map<String, Integer> resultado = estoque.contarPorCategoria();

        byte[] reply = JsonCodec.toJson(resultado).getBytes(StandardCharsets.UTF_8);
        serverProtocol.sendReply(reply, null, 0);

        return resultado;
    }

    @Override
    public boolean removerComputador(String codigo) throws RemoteException {
        serverProtocol.getRequest("removerComputador",
                codigo.getBytes(StandardCharsets.UTF_8));

        boolean removido = estoque.remover(codigo);

        byte[] reply = String.valueOf(removido).getBytes(StandardCharsets.UTF_8);
        serverProtocol.sendReply(reply, null, 0);

        return removido;
    }

    // Auxiliares privados

    /**
     * Desserializa um Computer a partir de JSON.
     */
    @SuppressWarnings("unchecked")
    private Computer desserializarComputador(byte[] json) {
        try {
            Map<String, Object> data = JsonCodec.fromJson(
                    new String(json, StandardCharsets.UTF_8), Map.class);

            String categoria = (String) data.getOrDefault("categoria", "");
            Computer c;
            switch (categoria) {
                case "Notebook":       c = new Notebook(); break;
                case "Microcomputador": c = new Microcomputador(); break;
                case "Mainframe":      c = new Mainframe(); break;
                default: throw new IllegalArgumentException("Categoria desconhecida: " + categoria);
            }

            if (data.get("code")        != null) c.setCode((String) data.get("code"));
            if (data.get("manufacturer") != null) c.setManufacturer((String) data.get("manufacturer"));
            if (data.get("model")       != null) c.setModel((String) data.get("model"));
            if (data.get("ramGb")       != null) c.setRamGb(((Number) data.get("ramGb")).intValue());
            if (data.get("storageGb")   != null) c.setStorageGb(((Number) data.get("storageGb")).intValue());
            if (data.get("price")       != null) c.setPrice(((Number) data.get("price")).doubleValue());

            return c;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao desserializar Computer: " + e.getMessage(), e);
        }
    }

    private void carregarDadosIniciais() {
        estoque.adicionar(new Notebook("NB-001", "Dell",   "Latitude 5520",   16, 512,  4500.00));
        estoque.adicionar(new Notebook("NB-002", "Lenovo", "ThinkPad E14",    8,  256,  3200.00));
        estoque.adicionar(new Notebook("NB-003", "Apple",  "MacBook Air M2",  16, 256,  9800.00));
        estoque.adicionar(new Microcomputador("MC-001", "Dell",  "OptiPlex 3080",   8,  500,  2800.00));
        estoque.adicionar(new Microcomputador("MC-002", "HP",    "ProDesk 400 G7", 16, 1000,  3500.00));
        estoque.adicionar(new Mainframe("MF-001", "IBM", "z15 T02", 4096, 32000, 2500000.00));
    }
}
