package br.edu.ufc.quixada.sd.t2.service;

import br.edu.ufc.quixada.sd.t2.domain.Computer;
import br.edu.ufc.quixada.sd.t2.repository.InMemoryComputerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ComputerService {
    private final InMemoryComputerRepository repo;

    public ComputerService(InMemoryComputerRepository repo) { this.repo = repo; }

    public List<Computer> listar() { return repo.listar(); }
    public Computer buscar(String codigo) { return repo.buscarPorCodigo(codigo); }
    public void adicionar(Computer c) { repo.adicionar(c); }
    public boolean remover(String codigo) { return repo.remover(codigo); }
    public Map<String,Integer> contarPorCategoria() { return repo.contarPorCategoria(); }
}
