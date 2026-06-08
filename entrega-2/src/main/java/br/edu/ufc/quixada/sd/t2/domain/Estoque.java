package br.edu.ufc.quixada.sd.t2.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estoque de computadores.
 * Relação HAS-A: Estoque "tem" uma lista de Computer (agregação).
 */
public class Estoque implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private final List<Computer> computadores = new ArrayList<>();

    public Estoque() {
    }

    public Estoque(String nome) {
        this.nome = nome;
    }

    public String getNome()          { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public void adicionar(Computer c) {
        computadores.add(c);
    }

    public List<Computer> listar() {
        return Collections.unmodifiableList(computadores);
    }

    public Computer buscarPorCodigo(String codigo) {
        return computadores.stream()
                .filter(c -> c.getCode().equals(codigo))
                .findFirst()
                .orElse(null);
    }

    public boolean remover(String codigo) {
        return computadores.removeIf(c -> c.getCode().equals(codigo));
    }

    public Map<String, Integer> contarPorCategoria() {
        Map<String, Integer> contagem = new LinkedHashMap<>();
        for (Computer c : computadores) {
            contagem.merge(c.getCategory(), 1, Integer::sum);
        }
        return contagem;
    }
}
