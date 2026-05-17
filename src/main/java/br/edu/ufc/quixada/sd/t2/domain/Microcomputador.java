package br.edu.ufc.quixada.sd.t2.domain;

public class Microcomputador extends Computer {
    public Microcomputador() {
        setCategoria("Microcomputador");
    }

    public Microcomputador(String code, String manufacturer, String model,
                           int ramGb, int storageGb, double price) {
        super(code, manufacturer, model, ramGb, storageGb, price, "Microcomputador");
    }

    @Override
    public String getCategory() { return "Microcomputador"; }
}
