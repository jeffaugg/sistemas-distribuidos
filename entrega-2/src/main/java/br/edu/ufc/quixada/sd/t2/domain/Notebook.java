package br.edu.ufc.quixada.sd.t2.domain;

public class Notebook extends Computer {
    public Notebook() {
        setCategoria("Notebook");
    }

    public Notebook(String code, String manufacturer, String model,
                    int ramGb, int storageGb, double price) {
        super(code, manufacturer, model, ramGb, storageGb, price, "Notebook");
    }

    @Override
    public String getCategory() { return "Notebook"; }
}
