# Entrega 3 — Server REST e Clientes

Conteúdo: implementação REST (server-rest) e clientes de exemplo em Python e Node.js.

Requisitos:
- Java 17+ e Maven para o server REST
- Python 3.8+ e `requests` para o cliente Python
- Node.js e `axios` para o cliente Node.js

Server (build e run):

```bash
# via Maven (recomendado)
cd server-rest
mvn -DskipTests package

# executa o JAR gerado (ajuste o nome do JAR se necessário)
java -jar target/*.jar
```

Cliente Python (ex.: clientes em `entrega-3/clients/client-python`):

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install requests
python entrega-3/clients/client-python/client.py
```

Cliente Node.js (ex.: `entrega-3/clients/client-node`):

```bash
cd entrega-3/clients/client-node
npm init -y
npm install axios
node index.js
```

Endpoints principais do servidor REST (exemplo):
- `GET /api/computadores` — listar
- `GET /api/computadores/{codigo}` — buscar
- `POST /api/computadores` — adicionar (JSON)
- `GET /api/computadores/stats/categoria` — contar por categoria
- `DELETE /api/computadores/{codigo}` — remover

OpenAPI / Postman:

- O arquivo OpenAPI (Swagger) está em `entrega-3/openapi.yaml` — importe em Swagger UI ou Swagger Editor.
- A coleção Postman está em `entrega-3/postman_collection.json` — importe no Postman para testar as requisições.

Importante: os exemplos usam `http://localhost:8080` como servidor; ajuste a URL se o servidor estiver rodando em outra porta/host.
# Entrega 3

Conteúdo:

- Servidor REST (server-rest module)
- Clientes de exemplo em Python e Node.js (pasta clients)

Como rodar os clientes de exemplo:

- Python: instale `requests` e rode `python3 clients/client-python/client.py`
- Node: instale `axios` (`npm install axios`) e rode `node clients/client-node/index.js`

Obs: o servidor REST deve estar rodando em `http://localhost:8080`.
