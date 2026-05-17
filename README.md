# Trabalhos de Sistemas Distribuídos — UFC Quixadá

Implementações em Java para a disciplina **QXD0043 – Sistemas Distribuídos**.

---

## Trabalho 2 — Remote Method Invocation (RMI)

> Antes de mergulhar no código, entenda os conceitos que estão por trás dele.

---

### O que é um Sistema Distribuído?

Imagine que você quer consultar o estoque de computadores de uma empresa, mas o
banco de dados fica em outro prédio. Você não quer copiar o banco de dados inteiro
para o seu computador — você quer *fazer uma pergunta* e receber uma *resposta*.

Isso é um sistema distribuído: **dois ou mais programas em computadores diferentes
se comunicando para resolver um problema em conjunto**.

```
  [Seu computador]  ←── rede ──→  [Servidor da empresa]
  "Quantos notebooks  "            "Temos 3 notebooks."
   temos em estoque?"
```

---

### O que é RMI?

**RMI (Remote Method Invocation)** é uma forma de chamar um método de um objeto
que está em *outro computador*, como se ele estivesse no seu próprio programa.

Sem RMI, você precisaria:
1. Abrir uma conexão de rede manualmente
2. Formatar os argumentos como bytes
3. Enviar pela rede
4. Esperar a resposta
5. Decodificar os bytes de volta

Com RMI, você apenas chama o método:
```java
List<Computer> lista = servico.listarComputadores(); // parece local, mas vai até o servidor!
```

O Java cuida de toda a comunicação de rede nos bastidores.

---

### O Protocolo Requisição-Resposta (seção 5.2 do livro Coulouris)

O RMI segue um protocolo simples: o cliente envia uma **requisição** e aguarda
a **resposta** do servidor. Este é o mesmo padrão de uma ligação telefônica:
você faz a pergunta (requisição) e aguarda a resposta.

```
       CLIENTE                              SERVIDOR
   ┌─────────────┐                      ┌─────────────┐
   │             │                      │             │
   │doOperation()│──── REQUISIÇÃO ─────►│getRequest() │
   │   (espera)  │                      │  execute()  │
   │             │◄───── RESPOSTA ───── │sendReply()  │
   │ (continua)  │                      │             │
   └─────────────┘                      └─────────────┘
```

Os três métodos do protocolo são:

| Método | Quem usa | O que faz |
|--------|----------|-----------|
| `doOperation(ref, método, args)` | Cliente | Envia a requisição e espera a resposta |
| `getRequest()` | Servidor | Recebe e registra a requisição que chegou |
| `sendReply(resposta, host, porta)` | Servidor | Envia a resposta de volta ao cliente |

#### Estrutura da Mensagem

Cada mensagem trocada na rede contém 5 campos:

```
┌─────────────────┬──────────────────────────────────────┐
│ messageType     │ 0 = Requisição  /  1 = Resposta       │
│ requestId       │ número único (casa req. com resp.)    │
│ objectReference │ nome do objeto remoto ("ComputerService") │
│ methodId        │ nome do método ("listarComputadores") │
│ arguments       │ argumentos em formato JSON (bytes)   │
└─────────────────┴──────────────────────────────────────┘
```

---

### Passagem por Referência vs. Passagem por Valor

Este é um dos conceitos mais importantes em sistemas distribuídos.

#### Passagem por Referência (objetos remotos)

O objeto `IComputerService` **vive apenas no servidor**. O cliente não tem uma
cópia — ele tem um **stub** (procurador/representante), que é como um controle
remoto: você aperta o botão aqui, mas a ação acontece lá.

```
CLIENTE                          SERVIDOR
  ┌──────────────┐                ┌──────────────────────┐
  │ stub (proxy) │ ─── rede ────► │ ComputerServiceImpl  │
  │ referência   │                │ (objeto REAL)        │
  └──────────────┘                └──────────────────────┘
```

#### Passagem por Valor (objetos locais)

Quando o servidor retorna um objeto `Computer`, ele envia uma **cópia** do objeto.
O cliente recebe uma instância independente — alterar a cópia no cliente não afeta
o original no servidor.

```
SERVIDOR                         CLIENTE
┌──────────────┐                 ┌──────────────┐
│ Computer     │ ──[cópia]──►   │ Computer     │
│ (original)   │                 │ (cópia)      │
└──────────────┘                 └──────────────┘
```

---

### Representação Externa de Dados (JSON)

Para enviar um objeto `Computer` pela rede, precisamos transformá-lo em uma sequência
de bytes que qualquer programa possa entender — isso é a **representação externa de dados**.

Neste trabalho usamos **JSON** (JavaScript Object Notation):

```
Objeto Java:                    Representação JSON:
Notebook {                      {"code":"NB-001",
  code = "NB-001"     ──►        "manufacturer":"Dell",
  manufacturer = "Dell"          "model":"Latitude 5520",
  ramGb = 16                     "ramGb":16,
}                                "categoria":"Notebook"}
```

O método `adicionarComputador(byte[] json)` usa explicitamente JSON: o cliente
serializa o objeto, envia os bytes, e o servidor desserializa e reconstrói o objeto.

---

### Modelo de Domínio

O domínio é um **Estoque de Computadores** de uma instituição de ensino.

#### Hierarquia de Classes (relação É-UM / herança)

```
Computer (abstrato)
├── Notebook          ← é um Computer
├── Microcomputador   ← é um Computer
└── Mainframe         ← é um Computer
```

#### Composições (relação TEM-UM / agregação)

```
Estoque  ──TEM── List<Computer>       (um estoque tem vários computadores)
LaboratorioInformatica  ──TEM── Estoque   (um laboratório tem um estoque)
```

#### Requisitos atendidos

| Requisito | Valor mínimo | Implementado |
|-----------|-------------|--------------|
| Classes entidade | 4 | 6 (Computer, Notebook, Microcomputador, Mainframe, Estoque, LaboratorioInformatica) |
| Agregação (tem-um) | 2 | 2 (Estoque→Computer, Laboratorio→Estoque) |
| Herança (é-um) | 2 | 3 (Notebook, Microcomputador, Mainframe) |
| Métodos remotos | 4 | 5 (listar, buscar, adicionar, contar, remover) |
| Passagem por referência | ✓ | IComputerService (stub RMI) |
| Passagem por valor | ✓ | Computer, List, Map (Serializable + JSON) |
| Representação externa | JSON | JsonCodec customizado |

---

### Estrutura do Código

```
src/main/java/br/edu/ufc/quixada/sd/t2/
│
├── domain/                       ← Classes de domínio (entidades)
│   ├── Computer.java             ← Entidade abstrata base (Serializable)
│   ├── Notebook.java             ← É-UM Computer (herança)
│   ├── Microcomputador.java      ← É-UM Computer (herança)
│   ├── Mainframe.java            ← É-UM Computer (herança)
│   ├── Estoque.java              ← TEM-UM List<Computer> (agregação)
│   └── LaboratorioInformatica.java ← TEM-UM Estoque (agregação)
│
├── rmi/                          ← Infraestrutura RMI
│   ├── IComputerService.java     ← Interface remota (estende Remote)
│   ├── RemoteObjectRef.java      ← Referência a um objeto remoto
│   └── RmiMessage.java           ← Estrutura da mensagem req/resp
│
├── protocol/                     ← Protocolo Requisição-Resposta
│   └── RequestReplyProtocol.java ← doOperation, getRequest, sendReply
│
├── server/                       ← Lado do servidor
│   ├── ComputerServiceImpl.java  ← Implementação do serviço remoto
│   └── RmiServer.java            ← Ponto de entrada do servidor
│
└── client/                       ← Lado do cliente
    └── RmiClient.java            ← Demonstração de todas as operações
```

---

### Como Compilar e Executar

#### Pré-requisitos

- Java 11 ou superior
- Dois terminais abertos

#### Compilação

```bash
# Na raiz do projeto:
mkdir -p target/classes

javac --release 11 \
  -d target/classes \
  $(find src/main/java -name '*.java')
```

#### Execução

**Terminal 1 — Servidor:**

```bash
java -cp target/classes br.edu.ufc.quixada.sd.t2.server.RmiServer
```

Saída esperada:
```
╔══════════════════════════════════════════╗
║       Servidor RMI - Trabalho 2          ║
╚══════════════════════════════════════════╝
Registro RMI iniciado na porta 1099
Objeto 'ComputerService' registrado com sucesso.
Aguardando requisições dos clientes...
```

**Terminal 2 — Cliente:**

```bash
java -cp target/classes br.edu.ufc.quixada.sd.t2.client.RmiClient
```

O cliente executará 5 operações e mostrará o fluxo completo do protocolo:
- Mensagem `[CLIENTE → SERVIDOR]` = requisição saindo do cliente
- Linha `[SERVIDOR] getRequest()` = servidor recebendo
- Linha `[SERVIDOR] sendReply()` = servidor respondendo
- Mensagem `[SERVIDOR → CLIENTE]` = resposta chegando no cliente

#### Servidor em máquina diferente

```bash
# Terminal 1 (servidor, IP 192.168.1.10):
java -cp target/classes br.edu.ufc.quixada.sd.t2.server.RmiServer

# Terminal 2 (cliente, outra máquina):
java -cp target/classes br.edu.ufc.quixada.sd.t2.client.RmiClient 192.168.1.10 1099
```

---

### Exemplo de Saída

```
══════════════════════════════════════════
  3. Adicionar novo computador (passagem por valor + JSON)
══════════════════════════════════════════
Objeto serializado para JSON:
{"code":"NB-999","manufacturer":"Samsung","model":"Galaxy Book3","ramGb":16,"storageGb":512,"price":5200.0,"categoria":"Notebook"}

[CLIENTE → SERVIDOR] [REQUISIÇÃO id=3 obj='ComputerService' method='adicionarComputador' args={"code":"NB-999"...}]
  [SERVIDOR] getRequest()  → [REQUISIÇÃO id=3 ...]
  [SERVIDOR] Computador adicionado: Notebook{code='NB-999', manufacturer='Samsung'...}
  [SERVIDOR] sendReply()   → 0 bytes para [gerenciado pelo Java RMI]
[SERVIDOR → CLIENTE] [RESPOSTA   id=3 obj='ComputerService' method='adicionarComputador' args=(vazio)]
```

---

## Trabalho 1 — Comunicação entre Processos

Implementação do tema 14 (Fábrica de Computadores) com:
- Streams binários customizados (`OutputStream`/`InputStream`)
- Comunicação TCP com serialização JSON própria
- Sistema de votação multi-threaded
- Notas informativas via multicast UDP

### Compilação

```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
```

### Execução rápida

```bash
# Domínio
java -cp out br.edu.ufc.quixada.sd.t1.App

# Streams — saída padrão
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamOutputDemo stdout

# Streams — arquivo
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamOutputDemo file computadores.bin
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamInputDemo file computadores.bin
```

#### TCP — cliente envia para servidor

Terminal 1:
```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamTcpServer receive 7000
```
Terminal 2:
```bash
java -cp out br.edu.ufc.quixada.sd.t1.stream.ComputerStreamOutputDemo tcp 127.0.0.1 7000
```

#### Sistema de votação

Terminal 1 (servidor):
```bash
java -cp out br.edu.ufc.quixada.sd.t1.voting.VotingServer 5000 300 230.0.0.1 6000
```
Terminal 2 (cliente):
```bash
java -cp out br.edu.ufc.quixada.sd.t1.voting.VotingCli 127.0.0.1 5000
```
