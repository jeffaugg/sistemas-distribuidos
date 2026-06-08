package br.edu.ufc.quixada.sd.t2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Computer {
    private String codigo;
    private String categoria;
    private String marca;
    private String modelo;
    private int ramGb;
    private int storageGb;
    private double preco;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getRamGb() { return ramGb; }
    public void setRamGb(int ramGb) { this.ramGb = ramGb; }
    public int getStorageGb() { return storageGb; }
    public void setStorageGb(int storageGb) { this.storageGb = storageGb; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    @JsonIgnore
    public abstract String getCategory();
}
