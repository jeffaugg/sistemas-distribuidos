package br.edu.ufc.quixada.sd.t1.domain.computadores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Estoque {
    private String name;
    private final List<Computer> computers = new ArrayList<>();

    public Estoque() {
    }

    public Estoque(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Computer> getComputers() {
        return Collections.unmodifiableList(computers);
    }

    public void addComputer(Computer computer) {
        computers.add(computer);
    }
}