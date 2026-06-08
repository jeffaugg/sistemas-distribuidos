package br.edu.ufc.quixada.sd.t2.domain;

import java.io.Serializable;
import java.util.Objects;

public abstract class Computer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String manufacturer;
    private String model;
    private int ramGb;
    private int storageGb;
    private double price;
    // campo armazenado no JSON para que o servidor saiba qual subclasse instanciar
    private String categoria;

    protected Computer() {
    }

    protected Computer(String code, String manufacturer, String model,
                        int ramGb, int storageGb, double price, String categoria) {
        this.code = code;
        this.manufacturer = manufacturer;
        this.model = model;
        this.ramGb = ramGb;
        this.storageGb = storageGb;
        this.price = price;
        this.categoria = categoria;
    }

    public abstract String getCategory();

    public String getCode()                          { return code; }
    public void   setCode(String code)               { this.code = code; }
    public String getManufacturer()                  { return manufacturer; }
    public void   setManufacturer(String m)          { this.manufacturer = m; }
    public String getModel()                         { return model; }
    public void   setModel(String model)             { this.model = model; }
    public int    getRamGb()                         { return ramGb; }
    public void   setRamGb(int ramGb)                { this.ramGb = ramGb; }
    public int    getStorageGb()                     { return storageGb; }
    public void   setStorageGb(int storageGb)        { this.storageGb = storageGb; }
    public double getPrice()                         { return price; }
    public void   setPrice(double price)             { this.price = price; }
    public String getCategoria()                     { return categoria; }
    protected void setCategoria(String categoria)    { this.categoria = categoria; }

    @Override
    public String toString() {
        return getCategory() + "{code='" + code + "', manufacturer='" + manufacturer +
               "', model='" + model + "', ramGb=" + ramGb +
               ", storageGb=" + storageGb + ", price=" + price + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(code, ((Computer) o).code);
    }

    @Override
    public int hashCode() { return Objects.hash(code); }
}
