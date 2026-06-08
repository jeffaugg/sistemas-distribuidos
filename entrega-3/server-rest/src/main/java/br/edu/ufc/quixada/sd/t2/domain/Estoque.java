package br.edu.ufc.quixada.sd.t2.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Estoque {
    private final List<Computer> computadores = new ArrayList<>();

    public void adicionar(Computer c) { computadores.add(c); }
    public List<Computer> listar() { return Collections.unmodifiableList(computadores); }
    public Computer buscarPorCodigo(String codigo) {
        return computadores.stream().filter(c -> codigo.equals(c.getCodigo())).findFirst().orElse(null);
    }
    public boolean remover(String codigo) { return computadores.removeIf(c -> codigo.equals(c.getCodigo())); }
    public Map<String,Integer> contarPorCategoria() {
        Map<String,Integer> m = new LinkedHashMap<>();
        for (Computer c : computadores) m.merge(c.getCategory(), 1, Integer::sum);
        return m;
    }
}
