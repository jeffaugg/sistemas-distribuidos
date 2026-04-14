package br.edu.ufc.quixada.sd.t1;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Mainframe;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Microcomputador;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Minicomputador;
import br.edu.ufc.quixada.sd.t1.domain.computadores.Notebook;
import br.edu.ufc.quixada.sd.t1.service.ComputerCatalogService;
import br.edu.ufc.quixada.sd.t1.service.StockService;

import java.util.List;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        ComputerCatalogService catalogService = new ComputerCatalogService();
        StockService stockService = new StockService();

        List<Computer> demoComputers = List.of(
                new Microcomputador("MC-01", "Dell", "OptiPlex 7010", 16, 512, 3200.00),
                new Minicomputador("MC-02", "Lenovo", "ThinkCentre M70", 32, 1024, 5200.00),
                new Mainframe("MF-01", "IBM", "Z16", 128, 8192, 300000.00),
                new Notebook("NB-01", "Acer", "Aspire 5", 8, 256, 2900.00)
        );

        catalogService.loadAll(demoComputers);
        stockService.registerAll(demoComputers);

        System.out.println("Catálogo inicial da fábrica:");
        catalogService.findAll().forEach(System.out::println);
        System.out.println();
        System.out.println("Resumo do estoque:");
        stockService.countByType().forEach((type, count) -> System.out.println(type + ": " + count));
    }
}