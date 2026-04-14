package br.edu.ufc.quixada.sd.t1.domain.computadores;

public class Notebook extends Computer {
    public Notebook() {
    }

    public Notebook(String code, String manufacturer, String model, int ramGb, int storageGb, double price) {
        super(code, manufacturer, model, ramGb, storageGb, price);
    }

    @Override
    public String getCategory() {
        return "Notebook";
    }
}