# Trabalho 2 — Remote Method Invocation (RMI)
**Disciplina:** QXD0043 – Sistemas Distribuídos  
**Instituição:** Universidade Federal do Ceará – Campus Quixadá  

---

## Sumário

1. [O que é um Sistema Distribuído?](#1-o-que-é-um-sistema-distribuído)
2. [O problema que o RMI resolve](#2-o-problema-que-o-rmi-resolve)
3. [O que é RMI?](#3-o-que-é-rmi)
4. [O Protocolo Requisição-Resposta](#4-o-protocolo-requisição-resposta)
5. [A Estrutura da Mensagem](#5-a-estrutura-da-mensagem)
6. [Passagem por Referência e por Valor](#6-passagem-por-referência-e-por-valor)
7. [Representação Externa de Dados (JSON)](#7-representação-externa-de-dados-json)
8. [O Domínio da Aplicação](#8-o-domínio-da-aplicação)
9. [Arquitetura Geral](#9-arquitetura-geral)
10. [Descrição detalhada de cada classe](#10-descrição-detalhada-de-cada-classe)
11. [O fluxo completo de uma chamada remota](#11-o-fluxo-completo-de-uma-chamada-remota)
12. [Como compilar e executar](#12-como-compilar-e-executar)
13. [Interpretando a saída do programa](#13-interpretando-a-saída-do-programa)
14. [Checklist de requisitos atendidos](#14-checklist-de-requisitos-atendidos)

---

## 1. O que é um Sistema Distribuído?

Um **sistema distribuído** é um conjunto de programas que rodam em computadores separados e se comunicam pela rede para realizar um trabalho em conjunto, aparentando para o usuário ser um sistema único.

**Exemplo concreto:** Quando você acessa um site de banco, o seu navegador está em um computador, o servidor web está em outro, e o banco de dados com suas informações está em um terceiro. Eles colaboram para que você veja seu saldo — você não sabe (nem precisa saber) quantas máquinas estão envolvidas.

```
  [Seu computador]           [Servidor Web]          [Banco de Dados]
  Navegador          ──►    Aplica regras       ──►  Armazena dados
  "mostra o saldo"   ◄──    monta a resposta    ◄──  retorna dados
```

**Por que distribuir?**

- **Desempenho:** dividir o trabalho entre várias máquinas
- **Disponibilidade:** se uma máquina falha, outras continuam
- **Escalabilidade:** adicionar mais máquinas quando o volume cresce
- **Compartilhamento:** vários usuários acessam os mesmos dados

---

## 2. O problema que o RMI resolve

No Trabalho 1, a comunicação entre processos era feita com **sockets TCP** — conexões de rede de baixo nível onde você escreve bytes em um lado e lê bytes no outro. Funciona, mas é trabalhoso:

```java
// Trabalho 1 — com sockets (forma "manual")
Socket socket = new Socket("servidor.com", 8080);
OutputStream out = socket.getOutputStream();
out.write(serializar(requisicao)); // você cuida dos bytes
byte[] resposta = lerBytes(socket.getInputStream()); // você decodifica
Computer c = desserializar(resposta); // você reconstrói o objeto
```

Você tem que se preocupar com:
- Como formatar os dados para envio
- Como saber onde uma mensagem termina e outra começa
- Como lidar com erros de rede
- Como mapear o resultado de volta para um objeto Java

O **RMI** elimina todo esse trabalho. Com RMI, você chama um método remoto como se fosse local:

```java
// Trabalho 2 — com RMI (forma automática)
IComputerService servico = /* referência remota */;
List<Computer> lista = servico.listarComputadores(); // parece local!
```

O Java cuida de tudo: serializa os argumentos, envia pela rede, espera a resposta, desserializa e retorna o resultado. Para o programador, a chamada remota se parece exatamente com uma chamada local.

---

## 3. O que é RMI?

**RMI (Remote Method Invocation)** é um mecanismo do Java que permite invocar métodos de objetos que estão em outra JVM (Java Virtual Machine), possivelmente em outro computador na rede.

### Componentes principais

O RMI é composto por três peças que trabalham juntas:

```
┌─────────────────────────────────────────────────────────────────┐
│                        LADO DO CLIENTE                          │
│                                                                 │
│   Seu código           Stub (gerado pelo RMI)                   │
│   ┌──────────┐         ┌──────────────────┐                    │
│   │ RmiClient│────────►│  Stub do Serviço │                    │
│   │          │ chama   │  (procurador)    │──── rede ────►     │
│   └──────────┘         └──────────────────┘                    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                        LADO DO SERVIDOR                         │
│                                                                 │
│   ◄──── rede ────  Skeleton (gerenciado pelo RMI)   Seu código  │
│                    ┌────────────────────┐         ┌──────────┐ │
│                    │ UnicastRemoteObject│────────►│ ServiceImpl││
│                    │ (recebe e delega)  │  delega │ (objeto   ││
│                    └────────────────────┘         │  real)    ││
│                                                   └──────────┘ │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────────┐
│      REGISTRO RMI            │
│  ("lista telefônica")        │
│  "ComputerService" → IP:porta│
└──────────────────────────────┘
```

**Stub:** Um objeto procurador no lado do cliente. Quando você chama `servico.listarComputadores()`, na verdade está chamando o stub, que empacota os argumentos, manda pela rede e traz a resposta de volta. O Java RMI cria o stub automaticamente.

**Skeleton:** No lado do servidor, é a camada que recebe a chamada da rede, desempacota os argumentos e invoca o método real na sua implementação. Também criado automaticamente pelo Java RMI.

**Registro RMI (Registry):** Um serviço de nomes que funciona como uma lista telefônica. O servidor registra seus objetos com um nome (`"ComputerService"`). O cliente consulta o registro pelo nome e recebe de volta um stub para se comunicar.

### Por que "não crie sockets"?

O enunciado proíbe criar sockets diretamente porque o Java RMI já os gerencia internamente. Ao usar RMI, você programa em um nível de abstração mais alto — trabalha com objetos e métodos, não com bytes e conexões.

---

## 4. O Protocolo Requisição-Resposta

O RMI funciona sobre um protocolo mais simples chamado **protocolo de requisição-resposta** (seção 5.2 do livro Coulouris). Entender esse protocolo ajuda a entender o que acontece por baixo do RMI.

### A ideia central

É como uma conversa telefônica estruturada:

1. O cliente liga (envia uma **requisição**)
2. O servidor atende, processa e devolve a resposta
3. O cliente recebe a **resposta** e continua sua execução

```
       CLIENTE                              SERVIDOR
   ┌──────────────┐                     ┌──────────────┐
   │              │                     │              │
   │ doOperation()│──── REQUISIÇÃO ────►│ getRequest() │
   │              │                     │   execute()  │
   │   (espera)   │                     │              │
   │              │◄──── RESPOSTA ──────│ sendReply()  │
   │  (continua)  │                     │              │
   └──────────────┘                     └──────────────┘
```

O protocolo define **três operações obrigatórias**, todas implementadas em `RequestReplyProtocol.java`:

### `doOperation` — lado do cliente

```
public byte[] doOperation(RemoteObjectRef o, String methodId, byte[] arguments)
```

Significado de cada parâmetro:
- `RemoteObjectRef o` — *para qual objeto remoto* enviar (qual serviço, em qual máquina)
- `String methodId` — *qual método* invocar (ex: `"listarComputadores"`)
- `byte[] arguments` — *quais argumentos* passar, codificados em JSON

O que `doOperation` faz internamente (ver `RequestReplyProtocol.java`, linha 49):
1. Gera um `requestId` único para rastrear esta chamada
2. Monta a `RmiMessage` de requisição
3. Localiza o objeto remoto no Registro RMI
4. Chama o método via stub (o Java RMI faz o transporte)
5. Serializa o resultado em JSON
6. Retorna os bytes da resposta

### `getRequest` — lado do servidor

```
public RmiMessage getRequest(String methodId, byte[] arguments)
```

Chamado no início de cada método remoto no servidor. Registra que uma requisição foi recebida e mostra sua estrutura. No Java RMI puro, isso acontece automaticamente dentro do skeleton; aqui o tornamos explícito para fins didáticos (ver `ComputerServiceImpl.java`, linha 44).

### `sendReply` — lado do servidor

```
public void sendReply(byte[] reply, InetAddress clientHost, int clientPort)
```

Chamado no fim de cada método remoto, antes de retornar o resultado. Registra que a resposta está sendo enviada. No Java RMI, o retorno do método já é suficiente para que o skeleton empacote e envie a resposta de volta ao stub do cliente (ver `ComputerServiceImpl.java`, linha 49).

---

## 5. A Estrutura da Mensagem

Toda comunicação no protocolo é empacotada em uma mensagem padronizada. A classe `RmiMessage.java` implementa essa estrutura:

```
┌──────────────────┬────────────────────────────────────────────┐
│  Campo           │  Descrição                                 │
├──────────────────┼────────────────────────────────────────────┤
│  messageType     │  0 = Requisição  /  1 = Resposta           │
│  requestId       │  Número único que casa cada req. com resp. │
│  objectReference │  Nome do objeto remoto ("ComputerService") │
│  methodId        │  Nome do método ("listarComputadores")     │
│  arguments       │  Argumentos ou resultado, em JSON (bytes)  │
└──────────────────┴────────────────────────────────────────────┘
```

**Por que o `requestId` é importante?**

Imagine que o cliente envia duas requisições quase ao mesmo tempo. As respostas podem chegar fora de ordem. O `requestId` permite que o cliente saiba qual resposta pertence a qual requisição, evitando confusão.

**Como a mensagem aparece no terminal:**

```
[REQUISIÇÃO id=3 obj='ComputerService' method='adicionarComputador' args={"code":"NB-999"...}]
[RESPOSTA   id=3 obj='ComputerService' method='adicionarComputador' args=(vazio)]
```

Note que ambas têm `id=3` — assim sabemos que a resposta corresponde à terceira requisição enviada.

O `objectReference` e o `methodId` são Strings (conforme permitido pelo enunciado), representando respectivamente o nome do serviço e o nome do método.

---

## 6. Passagem por Referência e por Valor

Este é um dos conceitos mais importantes do RMI e da programação distribuída em geral.

### Passagem por Referência — Objetos Remotos

Quando você obtém uma referência ao `IComputerService`, você **não tem uma cópia** do objeto. Você tem um **stub** — um procurador que redireciona suas chamadas para o objeto real, que vive no servidor.

```
CLIENTE                               SERVIDOR
┌──────────────────┐                 ┌────────────────────────┐
│ stub             │                 │ ComputerServiceImpl    │
│ (procurador)     │                 │ (objeto REAL)          │
│                  │                 │                        │
│ servico          │                 │ estoque = Estoque(...)  │
│  .listar()   ────┼──── rede ──────►│   → listar()           │
│               ◄──┼──── resposta ───│   ← [NB-001, NB-002...]│
└──────────────────┘                 └────────────────────────┘
```

Alterar algo pelo stub afeta o objeto real no servidor. Não existe cópia do serviço no cliente.

**No código:** A linha `IComputerService service = (IComputerService) Naming.lookup(ref.toUrl())` em `RequestReplyProtocol.java` (linha 64) retorna um stub — uma referência remota, não o objeto em si.

### Passagem por Valor — Objetos de Dados

Quando um método retorna um `Computer` ou uma `List<Computer>`, o Java RMI **copia** o objeto através da rede usando serialização. O cliente recebe uma instância independente.

```
SERVIDOR                              CLIENTE
┌───────────────┐                    ┌───────────────┐
│ Computer      │                    │ Computer      │
│ code="NB-001" │──[serializa/copia]►│ code="NB-001" │
│ price=4500.0  │                    │ price=4500.0  │
│ (original)    │                    │ (cópia)       │
└───────────────┘                    └───────────────┘
       ↑                                    ↑
  alterar aqui                       não afeta aqui
```

Para que um objeto seja passado por valor, sua classe precisa implementar `java.io.Serializable` — que é declarado em `Computer.java` (linha 7) e em todas as subclasses e `Estoque.java`.

**O método `adicionarComputador` vai além:** em vez de depender da serialização padrão Java, ele usa JSON explicitamente como formato externo (ver seção 7 abaixo).

---

## 7. Representação Externa de Dados (JSON)

Quando dois programas em computadores diferentes trocam dados, eles precisam de um formato que **ambos entendam**, independente de linguagem de programação ou sistema operacional. Isso é a **representação externa de dados**.

O enunciado permite usar JSON como esse formato. A classe `JsonCodec` (do Trabalho 1, em `t1/protocol/JsonCodec.java`) transforma objetos Java em texto JSON e vice-versa.

### O que é JSON?

JSON (JavaScript Object Notation) é um formato de texto simples para representar dados estruturados:

```json
{
  "code": "NB-001",
  "manufacturer": "Dell",
  "model": "Latitude 5520",
  "ramGb": 16,
  "storageGb": 512,
  "price": 4500.0,
  "categoria": "Notebook"
}
```

Qualquer linguagem consegue ler esse texto — Java, Python, JavaScript, C#, etc. Isso torna o sistema interoperável.

### Como é usado na prática

O método `adicionarComputador` demonstra o ciclo completo:

**No cliente** (`RmiClient.java`, linha 59):
```java
Notebook novoNotebook = new Notebook("NB-999", "Samsung", "Galaxy Book3", 16, 512, 5200.00);

// Passo 1: objeto Java → texto JSON
String jsonDoComputador = JsonCodec.toJson(novoNotebook);
// Resultado: {"code":"NB-999","manufacturer":"Samsung",...,"categoria":"Notebook"}

// Passo 2: texto → bytes para envio pela rede
byte[] jsonBytes = jsonDoComputador.getBytes(StandardCharsets.UTF_8);

// Passo 3: envia para o servidor
protocolo.doOperation(ref, "adicionarComputador", jsonBytes);
```

**No servidor** (`ComputerServiceImpl.java`, linha 122):
```java
private Computer desserializarComputador(byte[] json) {
    // Passo 4: bytes → texto JSON
    Map<String, Object> data = JsonCodec.fromJson(new String(json, UTF_8), Map.class);

    // Passo 5: lê o campo "categoria" para saber qual subclasse instanciar
    String categoria = (String) data.get("categoria"); // "Notebook"

    // Passo 6: cria a subclasse correta
    Computer c = new Notebook(); // porque categoria == "Notebook"

    // Passo 7: preenche os campos a partir do mapa
    c.setCode((String) data.get("code")); // "NB-999"
    c.setManufacturer((String) data.get("manufacturer")); // "Samsung"
    // ...

    return c; // objeto reconstruído no servidor
}
```

**Por que o campo `categoria` existe no JSON?**

A classe `Computer` é abstrata — não dá para instanciar ela diretamente. Precisamos saber se o computador é um `Notebook`, `Microcomputador` ou `Mainframe` para criar o objeto certo. O campo `categoria` (definido em `Computer.java`, linha 26) é preenchido automaticamente pelo construtor de cada subclasse (ex: `Notebook.java` linha 3: `setCategoria("Notebook")`) e aparece no JSON. O servidor usa esse campo para escolher a subclasse correta.

---

## 8. O Domínio da Aplicação

O domínio é um **sistema de gerenciamento de estoque de computadores** para uma instituição de ensino.

### Hierarquia de Classes (relação É-UM / herança)

A herança modela a relação "é um tipo de":

```
         Computer  (abstrato — não pode ser instanciado)
        /    |    \
  Notebook  Micro  Mainframe
  "é um"    "é um"   "é um"
  Computer  Computer  Computer
```

**`Computer.java`** — classe base abstrata. Define os atributos comuns a todo computador: `code`, `manufacturer`, `model`, `ramGb`, `storageGb`, `price`, `categoria`. O método abstrato `getCategory()` obriga cada subclasse a declarar sua própria categoria.

**`Notebook.java`** — representa um notebook portátil. Herda todos os campos de `Computer` e implementa `getCategory()` retornando `"Notebook"`.

**`Microcomputador.java`** — representa um computador de mesa (desktop). Retorna `"Microcomputador"`.

**`Mainframe.java`** — representa um computador de grande porte. Retorna `"Mainframe"`. Usado para exemplificar um equipamento de alto valor (RAM de 4096 GB, preço de R$ 2,5 milhões).

### Composições (relação TEM-UM / agregação)

A agregação modela a relação "faz parte de" ou "contém":

**`Estoque.java`** — TEM uma lista de `Computer`. É o repositório central de dados. Contém todos os métodos de manipulação: `adicionar`, `listar`, `buscarPorCodigo`, `remover`, `contarPorCategoria`.

```java
public class Estoque implements Serializable {
    private final List<Computer> computadores = new ArrayList<>(); // TEM-UM (agregação)
    // ...
}
```

**`LaboratorioInformatica.java`** — TEM um `Estoque`. Representa um laboratório físico que possui um conjunto de computadores organizado em um estoque.

```java
public class LaboratorioInformatica implements Serializable {
    private Estoque estoque; // TEM-UM (agregação)
    // ...
}
```

### Diagrama de relacionamentos

```
LaboratorioInformatica
  └── TEM-UM ──► Estoque
                  └── TEM-VÁRIOS ──► Computer (abstrato)
                                         ├── Notebook
                                         ├── Microcomputador
                                         └── Mainframe
```

---

## 9. Arquitetura Geral

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         PROCESSO SERVIDOR                               │
│                                                                         │
│  ┌──────────────┐    ┌───────────────────┐    ┌───────────────────────┐│
│  │  RmiServer   │    │ComputerServiceImpl│    │       Estoque         ││
│  │  (main)      │───►│  (objeto remoto)  │───►│  List<Computer>       ││
│  │              │    │  extends          │    │  ┌──────────────────┐ ││
│  │ cria registro│    │  UnicastRemote    │    │  │ NB-001 (Notebook)│ ││
│  │ registra obj │    │  Object           │    │  │ MC-001 (Micro)   │ ││
│  └──────────────┘    └───────────────────┘    │  │ MF-001 (Mainframe│ ││
│                                               │  └──────────────────┘ ││
│                             ▲                 └───────────────────────┘│
│                             │ delega                                    │
│                    ┌────────┴────────┐                                  │
│                    │    Skeleton     │ (gerado pelo Java RMI)            │
│                    │ (recebe rede)   │                                  │
│                    └────────┬────────┘                                  │
└─────────────────────────────┼───────────────────────────────────────────┘
                              │ TCP/IP
┌─────────────────────────────┼───────────────────────────────────────────┐
│                             │ PROCESSO CLIENTE                          │
│                    ┌────────┴────────┐                                  │
│                    │      Stub       │ (gerado pelo Java RMI)            │
│                    │  (envia rede)   │                                  │
│                    └────────┬────────┘                                  │
│                             │ chama                                     │
│  ┌──────────────┐    ┌──────┴────────────────┐                         │
│  │  RmiClient   │    │ RequestReplyProtocol  │                         │
│  │  (main)      │───►│  doOperation()        │                         │
│  │              │    │  (monta mensagem,     │                         │
│  │              │    │   localiza stub,      │                         │
│  │              │    │   chama método)       │                         │
│  └──────────────┘    └───────────────────────┘                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                  REGISTRO RMI (porta 1099)                              │
│          "ComputerService" ──► stub que aponta para servidor            │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 10. Descrição detalhada de cada classe

### Pacote `t2.domain` — as entidades

#### `Computer.java`

**O que é:** Classe base abstrata para todos os tipos de computador.

**Para que serve:** Define o contrato comum — todos os computadores têm código, fabricante, modelo, RAM, armazenamento e preço. Por ser abstrata, não pode ser instanciada diretamente: você sempre trabalha com um `Notebook`, `Microcomputador` ou `Mainframe`.

**Pontos importantes:**
- Implementa `Serializable` (linha 7): obrigatório para que o Java RMI consiga copiar objetos desta classe pela rede (passagem por valor).
- O campo `categoria` (linha 24): armazenado junto com os outros campos para aparecer no JSON. Permite que o servidor saiba qual subclasse recriar durante a desserialização.
- O método `getCategory()` é abstrato (linha 31): cada subclasse é forçada a declarar sua própria categoria.

#### `Notebook.java`, `Microcomputador.java`, `Mainframe.java`

**O que são:** Subclasses concretas de `Computer`. Cada uma representa um tipo específico de computador.

**Para que servem:** Implementam `getCategory()` retornando seu nome (`"Notebook"`, `"Microcomputador"`, `"Mainframe"`). Também chamam `setCategoria(...)` no construtor, garantindo que o campo esteja sempre preenchido quando o objeto for serializado para JSON.

**Relação IS-A:** `Notebook extends Computer` significa "Notebook É UM Computer". Em qualquer lugar que o código espera um `Computer`, pode-se passar um `Notebook`.

#### `Estoque.java`

**O que é:** Repositório de computadores. Contém a lista e todos os métodos para manipulá-la.

**Para que serve:** Centraliza a lógica de dados no servidor. O `ComputerServiceImpl` delega todas as operações para o `Estoque`.

**Métodos principais:**
- `adicionar(Computer c)` — insere um computador na lista
- `listar()` — retorna cópia imutável da lista
- `buscarPorCodigo(String codigo)` — busca por código, retorna `null` se não encontrado
- `remover(String codigo)` — remove e retorna `true` se encontrou
- `contarPorCategoria()` — retorna `Map<String, Integer>` com a contagem por tipo

**Relação HAS-A:** `Estoque` tem uma `List<Computer>` como atributo — "Estoque TEM computadores".

#### `LaboratorioInformatica.java`

**O que é:** Representa um laboratório físico de informática.

**Para que serve:** Demonstra a segunda relação de agregação do sistema. Um laboratório tem um nome, um prédio e um estoque de computadores.

**Relação HAS-A:** `LaboratorioInformatica` tem um `Estoque` como atributo — "Laboratório TEM um estoque".

---

### Pacote `t2.rmi` — a infraestrutura RMI

#### `IComputerService.java`

**O que é:** Interface que define o contrato do serviço remoto.

**Para que serve:** É o "cardápio" do servidor — declara quais operações estão disponíveis para chamada remota. O cliente só precisa conhecer esta interface; não precisa saber nada sobre a implementação.

**Por que estende `Remote`?** Toda interface RMI precisa estender `java.rmi.Remote`. Isso sinaliza ao Java RMI que os objetos que implementam esta interface podem ser acessados remotamente.

**Por que `throws RemoteException`?** Toda chamada remota pode falhar por problemas de rede (cabo desconectado, servidor caído, timeout). O `RemoteException` é o Java RMI avisando: "esta chamada pode dar errado por razões de rede — trate esse caso".

```java
public interface IComputerService extends Remote {
    List<Computer> listarComputadores() throws RemoteException;
    Computer buscarPorCodigo(String codigo) throws RemoteException;
    void adicionarComputador(byte[] computerJson) throws RemoteException;
    Map<String, Integer> contarPorCategoria() throws RemoteException;
    boolean removerComputador(String codigo) throws RemoteException;
}
```

#### `RemoteObjectRef.java`

**O que é:** Referência a um objeto remoto — contém as informações para encontrá-lo.

**Para que serve:** Encapsula o endereço do servidor (`host`), a porta do Registro RMI (`port`) e o nome com que o objeto foi registrado (`objectName`). O método `toUrl()` monta a URL no formato que o Java RMI entende: `rmi://localhost:1099/ComputerService`.

**Analogia:** É como o endereço de uma casa. Você não tem a casa na mão, mas sabe onde encontrá-la. O stub é a chave da casa.

#### `RmiMessage.java`

**O que é:** Representa a mensagem trocada no protocolo requisição-resposta.

**Para que serve:** Modela a estrutura de mensagem descrita na seção 5.2 do livro Coulouris. No código, é usada para registrar (logar) o que está acontecendo durante a comunicação, tornando o protocolo visível.

**Os cinco campos:**

| Campo | Tipo | Exemplo |
|-------|------|---------|
| `messageType` | `int` (0=req, 1=resp) | `0` |
| `requestId` | `int` | `3` |
| `objectReference` | `String` | `"ComputerService"` |
| `methodId` | `String` | `"adicionarComputador"` |
| `arguments` | `byte[]` | `{"code":"NB-999",...}` |

---

### Pacote `t2.protocol`

#### `RequestReplyProtocol.java`

**O que é:** O coração do trabalho — implementa os três métodos do protocolo.

**Para que serve:** Torna explícito o fluxo que o Java RMI realiza internamente. Sem essa classe, o protocolo seria invisível — o Java só chamaria o método e retornaria o resultado. Com ela, cada passo é registrado no terminal.

**`doOperation` (linha 49):** Executado pelo cliente. Monta a mensagem de requisição, localiza o objeto remoto no registro, despacha a chamada para o método correto via `dispatch()`, serializa o resultado em JSON e monta a mensagem de resposta.

**`getRequest` (linha 101):** Executado pelo servidor no início de cada método remoto. Constrói a mensagem de requisição para log.

**`sendReply` (linha 120):** Executado pelo servidor antes de retornar cada método remoto. Registra a resposta que está sendo enviada.

**`dispatch` (linha 136):** Método privado que traduz o nome do método (String) para a chamada tipada na interface. Isso representa o que o skeleton faz no protocolo RMI clássico: recebe `"listarComputadores"` e chama `service.listarComputadores()`.

---

### Pacote `t2.server`

#### `ComputerServiceImpl.java`

**O que é:** A implementação concreta do serviço remoto. É o objeto real que vive no servidor.

**Para que serve:** Implementa todos os métodos declarados em `IComputerService`. Quando o cliente chama um método pelo stub, o Java RMI encaminha a chamada para aqui.

**Por que estende `UnicastRemoteObject`?** Esta superclasse do Java RMI faz duas coisas ao ser chamada no construtor: exporta o objeto (torna-o acessível via rede) e cria o skeleton que vai receber e despachar as chamadas remotas.

**Fluxo interno de cada método remoto:**
```java
public List<Computer> listarComputadores() throws RemoteException {
    // 1. Registra que chegou uma requisição
    serverProtocol.getRequest("listarComputadores", new byte[0]);

    // 2. Executa a lógica real
    List<Computer> resultado = estoque.listar();

    // 3. Registra que vai enviar a resposta
    byte[] reply = JsonCodec.toJson(resultado).getBytes(UTF_8);
    serverProtocol.sendReply(reply, null, 0);

    // 4. Retorna — o Java RMI serializa e envia pela rede automaticamente
    return resultado;
}
```

**`desserializarComputador` (linha 122):** Método privado que reconstrói um `Computer` a partir de JSON. Como `Computer` é abstrato, usa o campo `categoria` do JSON para decidir qual subclasse instanciar. É aqui que a representação externa de dados (JSON) é convertida de volta em objeto Java.

#### `RmiServer.java`

**O que é:** Ponto de entrada (main) do servidor.

**Para que serve:** Inicia o Registro RMI, cria a instância do serviço e a registra com o nome `"ComputerService"`. A partir daí, o servidor fica aguardando indefinidamente por chamadas de clientes.

**Ordem de execução:**
1. `LocateRegistry.createRegistry(porta)` — inicia o servidor de nomes na porta 1099
2. `new ComputerServiceImpl()` — cria o objeto remoto (e o exporta, via `UnicastRemoteObject`)
3. `registry.rebind("ComputerService", servico)` — registra o objeto com um nome

---

### Pacote `t2.client`

#### `RmiClient.java`

**O que é:** Demonstração de todas as operações disponíveis no serviço remoto.

**Para que serve:** Exercita todos os 5 métodos remotos, mostrando o fluxo completo do protocolo para cada um.

**O que acontece em cada operação:**

| Operação | Método | Argumento | O que demonstra |
|----------|--------|-----------|-----------------|
| 1 | `listarComputadores` | nenhum | chamada sem argumentos, retorno de lista |
| 2 | `buscarPorCodigo` | `"NB-001"` (String→bytes) | argumento simples, retorno de objeto |
| 3 | `adicionarComputador` | JSON do Notebook | passagem por valor com repr. externa |
| 4 | `contarPorCategoria` | nenhum | retorno de mapa |
| 5 | `removerComputador` | `"NB-999"` | argumento simples, retorno primitivo |

---

## 11. O fluxo completo de uma chamada remota

Vamos rastrear o que acontece passo a passo quando o cliente chama `adicionarComputador`:

```
CLIENTE (RmiClient.java)
│
│  1. Cria o objeto local
│     Notebook nb = new Notebook("NB-999", "Samsung", "Galaxy Book3", 16, 512, 5200.0)
│
│  2. Serializa para JSON (representação externa de dados)
│     jsonStr = JsonCodec.toJson(nb)
│     → {"code":"NB-999","manufacturer":"Samsung",...,"categoria":"Notebook"}
│
│  3. Converte para bytes
│     jsonBytes = jsonStr.getBytes(UTF_8)
│
│  4. Chama doOperation(ref, "adicionarComputador", jsonBytes)
│     ┌──────────────────────────────────────────────────────┐
│     │ RequestReplyProtocol.doOperation()                   │
│     │                                                      │
│     │  a. Monta RmiMessage (TYPE_REQUEST, id=3, ...)       │
│     │  b. Imprime: [CLIENTE → SERVIDOR] [REQUISIÇÃO id=3]  │
│     │  c. Naming.lookup("rmi://localhost:1099/ComputerService")│
│     │     → retorna stub (referência remota)               │
│     │  d. dispatch(stub, "adicionarComputador", jsonBytes)  │
│     │     → stub.adicionarComputador(jsonBytes)             │
│     └─────────────────────────┬────────────────────────────┘
│                               │ Java RMI serializa e envia
│                               ▼ pela rede TCP/IP
```

```
SERVIDOR (ComputerServiceImpl.java)
│
│  Java RMI (skeleton) recebe a chamada e invoca:
│
│  5. adicionarComputador(byte[] jsonBytes) é chamado
│
│  6. serverProtocol.getRequest("adicionarComputador", jsonBytes)
│     → imprime: [SERVIDOR] getRequest() → [REQUISIÇÃO id=X ...]
│
│  7. desserializarComputador(jsonBytes)
│     a. JsonCodec.fromJson(json, Map.class) → mapa de campos
│     b. categoria = "Notebook"
│     c. c = new Notebook()
│     d. c.setCode("NB-999"), c.setManufacturer("Samsung"), ...
│     e. retorna o objeto Computer reconstruído
│
│  8. estoque.adicionar(computer) → insere na lista interna
│
│  9. serverProtocol.sendReply(new byte[0], null, 0)
│     → imprime: [SERVIDOR] sendReply() → 0 bytes para [...]
│
│  10. return (void) — Java RMI serializa o "nada" e envia de volta
│                     │
│                     ▼ resposta pela rede
```

```
CLIENTE (de volta no doOperation)
│
│  11. Recebe o resultado (vazio neste caso)
│  12. Monta RmiMessage (TYPE_REPLY, id=3, ...)
│  13. Imprime: [SERVIDOR → CLIENTE] [RESPOSTA id=3 args=(vazio)]
│  14. Retorna byte[0]
```

---

## 12. Como compilar e executar

### Pré-requisitos

- **Java 11 ou superior** instalado
- Dois terminais (abas) disponíveis
- Estar na raiz do projeto (`sistemas-distribuidos/`)

### Passo 1 — Compilar

```bash
mkdir -p target/classes

javac --release 11 \
  -d target/classes \
  $(find src/main/java -name '*.java')
```

O que este comando faz:
- `mkdir -p target/classes` — cria o diretório de saída se não existir
- `--release 11` — compila para Java 11
- `-d target/classes` — coloca os `.class` compilados nesse diretório
- `$(find src/main/java -name '*.java')` — encontra todos os arquivos `.java` do projeto

Se a compilação for bem-sucedida, nenhuma saída será exibida.

### Passo 2 — Iniciar o Servidor

Abra o **Terminal 1** e execute:

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

O servidor **fica em execução** — não fecha sozinho. Ele aguarda chamadas indefinidamente. Mantenha este terminal aberto.

**Porta personalizada:**
```bash
java -cp target/classes br.edu.ufc.quixada.sd.t2.server.RmiServer 2000
```

### Passo 3 — Executar o Cliente

Abra o **Terminal 2** e execute:

```bash
java -cp target/classes br.edu.ufc.quixada.sd.t2.client.RmiClient
```

O cliente se conectará ao servidor em `localhost:1099` (padrão), executará todas as operações e encerrará.

**Cliente em outra máquina** (servidor no IP `192.168.1.10`, porta `2000`):
```bash
java -cp target/classes br.edu.ufc.quixada.sd.t2.client.RmiClient 192.168.1.10 2000
```

### Possíveis erros

**`Connection refused`** — o servidor não está rodando. Certifique-se de iniciar o Terminal 1 primeiro.

**`ClassNotFoundException` no servidor** — o servidor não consegue encontrar a classe `Computer` quando o cliente envia um objeto. Certifique-se de que o `target/classes` contém as classes do cliente também.

**`java.rmi.server.ExportException: Port already in use`** — a porta 1099 já está em uso. Encerre o servidor anterior ou use outra porta.

---

## 13. Interpretando a saída do programa

Ao executar o cliente, você verá linhas como estas. Veja o que cada uma significa:

```
══════════════════════════════════════════
  3. Adicionar novo computador (passagem por valor + JSON)
══════════════════════════════════════════
```
Separador de seção — indica qual operação está sendo demonstrada.

```
Objeto serializado para JSON:
{"code":"NB-999","manufacturer":"Samsung","model":"Galaxy Book3","ramGb":16,"storageGb":512,"price":5200.0,"categoria":"Notebook"}
```
O objeto `Notebook` foi convertido para JSON no cliente, **antes** de ser enviado. Isso é a representação externa de dados — o objeto Java virou texto portátil.

```
[CLIENTE → SERVIDOR] [REQUISIÇÃO id=3 obj='ComputerService' method='adicionarComputador' args={"code":"NB-999"...}]
```
A mensagem de requisição saindo do cliente. Campos visíveis: tipo (`REQUISIÇÃO`), número de rastreamento (`id=3`), objeto alvo (`ComputerService`), método solicitado (`adicionarComputador`) e os argumentos em JSON.

```
  [SERVIDOR] getRequest()  → [REQUISIÇÃO id=3 obj='ComputerService' method='adicionarComputador' args=...]
```
O servidor recebeu a requisição. O recuo (`  `) indica que esta linha veio do código do servidor. `getRequest()` foi chamado no início do método `adicionarComputador`.

```
  [SERVIDOR] Computador adicionado: Notebook{code='NB-999', manufacturer='Samsung'...}
```
O servidor desserializou o JSON de volta para um objeto Java e inseriu no estoque.

```
  [SERVIDOR] sendReply()   → 0 bytes para [gerenciado pelo Java RMI]
```
`sendReply()` foi chamado antes do retorno. `0 bytes` porque `adicionarComputador` retorna `void`. A frase "gerenciado pelo Java RMI" indica que o endereço do cliente é conhecido pelo framework de rede, não pelo código da aplicação.

```
[SERVIDOR → CLIENTE] [RESPOSTA   id=3 obj='ComputerService' method='adicionarComputador' args=(vazio)]
```
A mensagem de resposta chegou ao cliente. Note que `id=3` casa com o `id=3` da requisição — confirmando que esta resposta corresponde àquela requisição específica. `args=(vazio)` porque não há valor de retorno.

---

## 14. Checklist de requisitos atendidos

| Requisito do enunciado | Onde está no código | Status |
|------------------------|---------------------|--------|
| Reimplementar questão 1 do T1 com cliente-servidor | `RmiServer.java` + `RmiClient.java` | ✓ |
| Usar RMI (não criar sockets) | `java.rmi.*` em todas as classes RMI | ✓ |
| `doOperation(RemoteObjectRef, methodId, arguments)` | `RequestReplyProtocol.java` linha 49 | ✓ |
| `getRequest()` | `RequestReplyProtocol.java` linha 101 | ✓ |
| `sendReply(reply, clientHost, clientPort)` | `RequestReplyProtocol.java` linha 120 | ✓ |
| Estrutura de mensagem com 5 campos | `RmiMessage.java` | ✓ |
| objectReference e methodId como String | `RmiMessage.java` linhas 23–24 | ✓ |
| Mínimo 4 classes entidade | `Computer`, `Notebook`, `Microcomputador`, `Mainframe`, `Estoque`, `LaboratorioInformatica` (6 no total) | ✓ |
| Mínimo 2 composição agregação (tem-um) | `Estoque` tem `List<Computer>` / `LaboratorioInformatica` tem `Estoque` | ✓ |
| Mínimo 2 composição extensão (é-um) | `Notebook`, `Microcomputador`, `Mainframe` estendem `Computer` (3 no total) | ✓ |
| Mínimo 4 métodos para invocação remota | `listarComputadores`, `buscarPorCodigo`, `adicionarComputador`, `contarPorCategoria`, `removerComputador` (5 no total) | ✓ |
| Passagem por referência (objetos remotos) | `IComputerService` retornado como stub pelo `Naming.lookup()` | ✓ |
| Passagem por valor (objetos locais) | `Computer`, `List<Computer>`, `Map` são `Serializable` e copiados pelo RMI | ✓ |
| Representação externa de dados (JSON) | `JsonCodec.toJson()` / `desserializarComputador()` em `ComputerServiceImpl.java` | ✓ |
