# Entrega 1 — Trabalho 1

Conteúdo: implementação de streaming e módulos do Trabalho 1 (streams, voting, domain, services).

Requisitos:
- Java 17+
- Maven (recomendado) ou JDK para compilação manual

Build (Maven, recomendado):

```bash
cd entrega-1
mvn -DskipTests package
```

Compilação manual (sem Maven):

```bash
# compila todas as classes para a pasta out
javac -d out $(find src/main/java -name "*.java")

# executa um demo (ex.: ComputerStreamInputDemo)
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamInputDemo file path/to/input.dat
```

Principais pontos de entrada:
- `br.edu.ufc.quixada.sd.t1.stream.ComputerStreamInputDemo` — leitura a partir de stdin/arquivo/tcp
- `br.edu.ufc.quixada.sd.t1.voting.VotingServer` / `VotingClient` — demonstração de votação (UDP/multicast)

Observações:
- Caso use Maven, o artefato gerado estará em `entrega-1/target/classes`.
- Se houver dúvidas sobre parâmetros de execução, abra a classe correspondente e verifique o `main()`.
