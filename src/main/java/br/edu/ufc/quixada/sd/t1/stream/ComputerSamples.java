package br.edu.ufc.quixada.sd.t1.stream;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Mainframe;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Microcomputador;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Minicomputador;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Notebook;

public final class ComputerSamples {
    private ComputerSamples() {
    }

    public static Computer[] defaultSet() {
        return new Computer[]{
                new Microcomputador("MC-01", "Dell", "OptiPlex", 16, 512, 3200.00),
                new Minicomputador("MN-01", "Lenovo", "ThinkCentre", 32, 1024, 5200.00),
                new Mainframe("MF-01", "IBM", "Z16", 128, 8192, 300000.00),
                new Notebook("NB-01", "Acer", "Aspire", 8, 256, 2900.00)
        };
    }
}