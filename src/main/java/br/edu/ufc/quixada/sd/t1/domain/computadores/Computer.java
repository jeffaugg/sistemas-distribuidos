package br.edu.ufc.quixada.sd.t1.domain.computadores;

import java.util.Objects;

public abstract class Computer {
    private String code;
    private String manufacturer;
    private String model;
    private int ramGb;
    private int storageGb;
    private double price;

    protected Computer() {
    }

    protected Computer(String code, String manufacturer, String model, int ramGb, int storageGb, double price) {
        this.code = code;
        this.manufacturer = manufacturer;
        this.model = model;
        this.ramGb = ramGb;
        this.storageGb = storageGb;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getRamGb() {
        return ramGb;
    }

    public void setRamGb(int ramGb) {
        this.ramGb = ramGb;
    }

    public int getStorageGb() {
        return storageGb;
    }

    public void setStorageGb(int storageGb) {
        this.storageGb = storageGb;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public abstract String getCategory();

    @Override
    public String toString() {
        return "Computer{" +
                "category='" + getCategory() + '\'' +
                ", code='" + code + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", ramGb=" + ramGb +
                ", storageGb=" + storageGb +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Computer computer = (Computer) o;
        return Objects.equals(code, computer.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}