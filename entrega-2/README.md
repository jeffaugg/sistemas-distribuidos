# Entrega 2 — Trabalho 2 (RMI)

Conteúdo: implementação original baseada em RMI (interfaces, servidor, cliente, protocolo).

Requisitos:
- Java 17+
- Maven (opcional) ou JDK para compilação manual

Build (Maven, recomendado):

```bash
cd entrega-2
mvn -DskipTests package
```

Compilação manual (sem Maven):

```bash
javac -d out $(find src/main/java -name "*.java")

# executar servidor (ex.: RmiServer) e cliente (RmiClient)
java -cp out br.edu.ufc.quixada.sd.t2.rmi.RmiServer
java -cp out br.edu.ufc.quixada.sd.t2.client.RmiClient [host] [port]
```

Observações específicas RMI:
- Se necessário, inicie o `rmiregistry` localmente antes de executar o servidor: `rmiregistry &`.
- O `RmiServer` da pasta libera o serviço `ComputerService` (nome: `ComputerService`).
