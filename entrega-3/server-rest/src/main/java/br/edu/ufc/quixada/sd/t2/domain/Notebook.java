package br.edu.ufc.quixada.sd.t2.domain;

public class Notebook extends Computer {
    public Notebook() { setCategoria("Notebook"); }
    public Notebook(String codigo, String marca, String modelo, int ramGb, int storageGb, double preco) {
        setCodigo(codigo); setMarca(marca); setModelo(modelo); setRamGb(ramGb); setStorageGb(storageGb); setPreco(preco); setCategoria("Notebook");
    }
    @Override public String getCategory() { return "Notebook"; }
}
