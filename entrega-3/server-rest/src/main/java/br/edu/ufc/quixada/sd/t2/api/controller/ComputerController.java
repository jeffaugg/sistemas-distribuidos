package br.edu.ufc.quixada.sd.t2.api.controller;

import br.edu.ufc.quixada.sd.t2.domain.*;
import br.edu.ufc.quixada.sd.t2.service.ComputerService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/computadores")
public class ComputerController {
    private final ComputerService service;

    public ComputerController(ComputerService service) { this.service = service; }

    @GetMapping
    public List<Computer> listar() { return service.listar(); }

    @GetMapping("/stats/categoria")
    public Map<String,Integer> stats() { return service.contarPorCategoria(); }

    @GetMapping("/{codigo}")
    public ResponseEntity<Computer> buscar(@PathVariable String codigo) {
        Computer c = service.buscar(codigo);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Void> adicionar(@RequestBody JsonNode body) {
        String categoria = body.hasNonNull("categoria") ? body.get("categoria").asText() : "";

        Computer c;
        switch (categoria) {
            case "Notebook": c = new Notebook(); break;
            case "Microcomputador": c = new Microcomputador(); break;
            case "Mainframe": c = new Mainframe(); break;
            default: c = new Notebook(); break;
        }

        if (body.hasNonNull("codigo")) c.setCodigo(body.get("codigo").asText());
        if (body.hasNonNull("marca")) c.setMarca(body.get("marca").asText());
        if (body.hasNonNull("modelo")) c.setModelo(body.get("modelo").asText());
        if (body.hasNonNull("ramGb")) c.setRamGb(body.get("ramGb").asInt());
        if (body.hasNonNull("storageGb")) c.setStorageGb(body.get("storageGb").asInt());
        if (body.hasNonNull("preco")) c.setPreco(body.get("preco").asDouble());

        service.adicionar(c);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> remover(@PathVariable String codigo) {
        boolean removed = service.remover(codigo);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
