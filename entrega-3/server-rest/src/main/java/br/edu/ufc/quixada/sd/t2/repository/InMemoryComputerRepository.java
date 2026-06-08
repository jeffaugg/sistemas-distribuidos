package br.edu.ufc.quixada.sd.t2.repository;

import br.edu.ufc.quixada.sd.t2.domain.*;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryComputerRepository {
    private final Estoque estoque = new Estoque();

    @PostConstruct
    public void init() {
        estoque.adicionar(new Notebook("NB-001","Dell","Latitude 5520",16,512,4500.0));
        estoque.adicionar(new Notebook("NB-002","Lenovo","ThinkPad E14",8,256,3200.0));
        estoque.adicionar(new Microcomputador("MC-001","Dell","OptiPlex 3080",8,500,2800.0));
        estoque.adicionar(new Mainframe("MF-001","IBM","z15 T02",4096,32000,2500000.0));
    }

    public List<Computer> listar() { return estoque.listar(); }
    public Computer buscarPorCodigo(String codigo) { return estoque.buscarPorCodigo(codigo); }
    public void adicionar(Computer c) { estoque.adicionar(c); }
    public boolean remover(String codigo) { return estoque.remover(codigo); }
    public Map<String,Integer> contarPorCategoria() { return estoque.contarPorCategoria(); }
}
