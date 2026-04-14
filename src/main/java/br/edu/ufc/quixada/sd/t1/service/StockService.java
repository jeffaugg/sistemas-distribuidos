package br.edu.ufc.quixada.sd.t1.service;

import br.edu.ufc.quixada.sd.t1.domain.computadores.Computer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockService {
    private final Map<String, Integer> stockByType = new LinkedHashMap<>();

    public void registerAll(List<Computer> computers) {
        stockByType.clear();
        for (Computer computer : computers) {
            stockByType.merge(computer.getCategory(), 1, Integer::sum);
        }
    }

    public Map<String, Integer> countByType() {
        return stockByType.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (left, right) -> left, LinkedHashMap::new));
    }
}