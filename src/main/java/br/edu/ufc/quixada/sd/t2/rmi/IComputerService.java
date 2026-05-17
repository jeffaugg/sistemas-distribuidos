package br.edu.ufc.quixada.sd.t2.rmi;

import br.edu.ufc.quixada.sd.t2.domain.Computer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Interface do serviço remoto de computadores.
 *
 * Toda interface RMI deve estender java.rmi.Remote.
 * Todo método deve declarar throws RemoteException, pois
 * qualquer chamada remota pode falhar por problemas de rede.
 *
 * Métodos remotos disponíveis (mínimo 4 exigido):
 *   1. listarComputadores   – retorna todos os computadores do estoque
 *   2. buscarPorCodigo      – busca um computador pelo código
 *   3. adicionarComputador  – adiciona um computador via JSON (passagem por valor)
 *   4. contarPorCategoria   – retorna a contagem agrupada por categoria
 *   5. removerComputador    – remove um computador pelo código
 */
public interface IComputerService extends Remote {

    /** Retorna todos os computadores cadastrados no estoque. */
    List<Computer> listarComputadores() throws RemoteException;

    /** Busca um computador pelo seu código. Retorna null se não encontrado. */
    Computer buscarPorCodigo(String codigo) throws RemoteException;

    /**
     * Adiciona um novo computador ao estoque.
     * O parâmetro é o objeto Computer serializado em JSON (representação externa de dados).
     * Isso demonstra a PASSAGEM POR VALOR com representação externa.
     */
    void adicionarComputador(byte[] computerJson) throws RemoteException;

    /** Retorna um mapa de categoria → quantidade de computadores. */
    Map<String, Integer> contarPorCategoria() throws RemoteException;

    /** Remove um computador pelo código. Retorna true se removido com sucesso. */
    boolean removerComputador(String codigo) throws RemoteException;
}
