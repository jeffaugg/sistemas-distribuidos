# Trabalho 1 - Comunicação entre processos

Implementação em Java do Trabalho 1 da disciplina de Sistemas Distribuídos.

## Requisitos atendidos

- Domínio escolhido (tema 14): Fábrica de Computadores.
- Classes POJO e classes de serviço.
- Subclasse de OutputStream para envio de conjunto de POJOs.
- Subclasse de InputStream para leitura do formato binário customizado.
- Testes de stream em saída padrão, arquivo e TCP.
- Comunicação cliente-servidor via sockets TCP com serialização em JSON.
- Sistema de votação com servidor multi-threaded.
- Notas informativas via multicast UDP.

## Estrutura principal

- [src/main/java/br/edu/ufc/quixada/sd/t1/domain/computadores](src/main/java/br/edu/ufc/quixada/sd/t1/domain/computadores)
- [src/main/java/br/edu/ufc/quixada/sd/t1/stream](src/main/java/br/edu/ufc/quixada/sd/t1/stream)
- [src/main/java/br/edu/ufc/quixada/sd/t1/protocol](src/main/java/br/edu/ufc/quixada/sd/t1/protocol)
- [src/main/java/br/edu/ufc/quixada/sd/t1/voting](src/main/java/br/edu/ufc/quixada/sd/t1/voting)

## Compilação

Como o ambiente pode não ter Maven, os comandos abaixo usam javac:

```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
```

## Execução rápida

### 1) Domínio da fábrica

```bash
java -cp out br.edu.ufc.quixada.sd.t1.App
```

### 2) Streams customizados

#### 2.1 OutputStream para stdout

```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamOutputDemo stdout
```

#### 2.2 OutputStream para arquivo

```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamOutputDemo file computadores.bin
```

#### 2.3 InputStream de arquivo

```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamInputDemo file computadores.bin
```

#### 2.4 InputStream de stdin

```bash
cat computadores.bin | java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamInputDemo stdin
```

#### 2.5 OutputStream para servidor TCP (cliente envia)

Terminal 1:

```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamTcpServer receive 7000
```

Terminal 2:

```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamOutputDemo tcp 127.0.0.1 7000
```

#### 2.6 InputStream via TCP (servidor envia)

Terminal 1:

```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamTcpServer send 7001
```

Terminal 2:

```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamInputDemo tcp 127.0.0.1 7001
```

### 3) Sistema de votação (TCP + UDP multicast)

#### 3.1 Subir servidor

```bash
java -cp out br.edu.ufc.quixada.sd.t1.voting.VotingServer 5000 300 230.0.0.1 6000
```

Argumentos:

- porta TCP do servidor
- duração da votação em segundos
- endereço multicast
- porta multicast

#### 3.2 Cliente interativo

```bash
java -cp out br.edu.ufc.quixada.sd.t1.voting.VotingCli 127.0.0.1 5000
```

No cliente:

- Entre com Username.
- Escolha perfil VOTER ou ADMIN.
- Use o menu para listar, votar, administrar candidatos, enviar notas multicast e encerrar votação.

## Observações

- A serialização de request/reply foi implementada em JSON com codec próprio do projeto.
- O servidor de votação atende múltiplos clientes com pool de threads.
- O multicast UDP é usado apenas para notas informativas, conforme solicitado.