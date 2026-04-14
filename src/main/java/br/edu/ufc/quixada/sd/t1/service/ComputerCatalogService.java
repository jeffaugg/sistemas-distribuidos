package br.edu.ufc.quixada.sd.t1.service;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComputerCatalogService {
    private final List<Computer> computers = new ArrayList<>();

    public void loadAll(List<Computer> items) {
        computers.clear();
        computers.addAll(items);
    }

    public void add(Computer computer) {
        computers.add(computer);
    }

    public List<Computer> findAll() {
        return Collections.unmodifiableList(computers);
    }
}