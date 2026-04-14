package br.edu.ufc.quixada.sd.t1.domain.computadores;

public class Microcomputador extends Computer {
    public Microcomputador() {
    }

    public Microcomputador(String code, String manufacturer, String model, int ramGb, int storageGb, double price) {
        super(code, manufacturer, model, ramGb, storageGb, price);
    }

    @Override
    public String getCategory() {
        return "Microcomputador";
    }
}