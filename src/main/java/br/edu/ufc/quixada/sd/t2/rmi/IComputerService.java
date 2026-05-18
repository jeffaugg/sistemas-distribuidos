package br.edu.ufc.quixada.sd.t2.rmi;

import br.edu.ufc.quixada.sd.t2.domain.Computer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Interface do serviço remoto de computadores.
 */
public interface IComputerService extends Remote {

    /** Retorna todos os computadores cadastrados no estoque. */
    List<Computer> listarComputadores() throws RemoteException;

    /** Busca um computador pelo seu código. Retorna null se não encontrado. */
    Computer buscarPorCodigo(String codigo) throws RemoteException;

    /**
     * Adiciona um novo computador ao estoque.
     * O parâmetro é o objeto Computer serializado em JSON (representação externa de dados).
     */
    void adicionarComputador(byte[] computerJson) throws RemoteException;

    /** Retorna um mapa de categoria → quantidade de computadores. */
    Map<String, Integer> contarPorCategoria() throws RemoteException;

    /** Remove um computador pelo código. Retorna true se removido com sucesso. */
    boolean removerComputador(String codigo) throws RemoteException;
}
