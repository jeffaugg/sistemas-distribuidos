package br.edu.ufc.quixada.sd.t1.domain.computadores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LaboratorioInformatica {
    private String name;
    private String building;
    private final List<Computer> computers = new ArrayList<>();

    public LaboratorioInformatica() {
    }

    public LaboratorioInformatica(String name, String building) {
        this.name = name;
        this.building = building;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public List<Computer> getComputers() {
        return Collections.unmodifiableList(computers);
    }

    public void addComputer(Computer computer) {
        computers.add(computer);
    }
}