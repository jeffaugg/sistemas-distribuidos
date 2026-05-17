package br.edu.ufc.quixada.sd.t2.domain;

public class Mainframe extends Computer {
    public Mainframe() {
        setCategoria("Mainframe");
    }

    public Mainframe(String code, String manufacturer, String model,
                     int ramGb, int storageGb, double price) {
        super(code, manufacturer, model, ramGb, storageGb, price, "Mainframe");
    }

    @Override
    public String getCategory() { return "Mainframe"; }
}
