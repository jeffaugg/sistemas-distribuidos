package br.edu.ufc.quixada.sd.t2.domain;

import java.io.Serializable;

/**
 * Laboratório de informática que possui um Estoque de computadores.
 * Relação HAS-A: LaboratorioInformatica "tem" um Estoque (agregação).
 */
public class LaboratorioInformatica implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String predio;
    private Estoque estoque;

    public LaboratorioInformatica() {
    }

    public LaboratorioInformatica(String nome, String predio, Estoque estoque) {
        this.nome = nome;
        this.predio = predio;
        this.estoque = estoque;
    }

    public String getNome()              { return nome; }
    public void setNome(String nome)     { this.nome = nome; }
    public String getPredio()            { return predio; }
    public void setPredio(String predio) { this.predio = predio; }
    public Estoque getEstoque()          { return estoque; }
    public void setEstoque(Estoque e)    { this.estoque = e; }

    @Override
    public String toString() {
        int total = estoque != null ? estoque.listar().size() : 0;
        return "LaboratorioInformatica{nome='" + nome + "', predio='" + predio +
               "', totalComputadores=" + total + '}';
    }
}
