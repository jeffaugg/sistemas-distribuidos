package br.edu.ufc.quixada.sd.t2.domain;

public class Microcomputador extends Computer {
    public Microcomputador() { setCategoria("Microcomputador"); }
    public Microcomputador(String codigo, String marca, String modelo, int ramGb, int storageGb, double preco) {
        setCodigo(codigo); setMarca(marca); setModelo(modelo); setRamGb(ramGb); setStorageGb(storageGb); setPreco(preco); setCategoria("Microcomputador");
    }
    @Override public String getCategory() { return "Microcomputador"; }
}
