package br.edu.ufc.quixada.sd.t2.domain;

public class Mainframe extends Computer {
    public Mainframe() { setCategoria("Mainframe"); }
    public Mainframe(String codigo, String marca, String modelo, int ramGb, int storageGb, double preco) {
        setCodigo(codigo); setMarca(marca); setModelo(modelo); setRamGb(ramGb); setStorageGb(storageGb); setPreco(preco); setCategoria("Mainframe");
    }
    @Override public String getCategory() { return "Mainframe"; }
}
