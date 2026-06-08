# Entrega 3 — Servidor REST e Clientes

Reimplementação do catálogo de computadores como uma **API REST** (Spring Boot),
com clientes de exemplo em Node.js e Python, especificação OpenAPI e coleção Postman.

## Como rodar com Docker

```bash
cd entrega-3
docker compose up --build
```

O servidor sobe em `http://localhost:8080`. Para rodar em segundo plano use
`docker compose up --build -d`; para parar, `docker compose down`.

## Acesso a partir de outros computadores (via IP)

O servidor escuta em todas as interfaces (`0.0.0.0`), e o container publica a porta
`8080` no host. Outros computadores na mesma rede acessam pelo IP da máquina que está
rodando o servidor:

```
http://<IP-do-host>:8080/api/computadores
```

Descubra o IP do host com `hostname -I` (Linux) ou `ipconfig` (Windows).

Se a máquina host tiver firewall ativo (ex.: `ufw`), libere a porta:

```bash
sudo ufw allow 8080/tcp
```

## Clientes de exemplo

Os clientes usam `http://localhost:8080` por padrão, mas aceitam a variável de
ambiente `API_URL` para apontar para outro host/IP.

Python (requer `requests`):

```bash
pip install requests
python3 clients/client-python/client.py
# apontando para outro host:
API_URL="http://192.168.0.12:8080/api/computadores" python3 clients/client-python/client.py
```

Node.js (requer `axios`):

```bash
cd clients/client-node
npm install axios
node index.js
# apontando para outro host:
API_URL="http://192.168.0.12:8080/api/computadores" node index.js
```

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/computadores` | Lista todos |
| GET | `/api/computadores/{codigo}` | Busca por código (404 se inexistente) |
| POST | `/api/computadores` | Adiciona (201) |
| DELETE | `/api/computadores/{codigo}` | Remove (204 / 404) |
| GET | `/api/computadores/stats/categoria` | Contagem por categoria |

## OpenAPI / Postman

- `openapi.yaml` — especificação OpenAPI 3.0; importe no Swagger UI ou Swagger Editor.
- `postman_collection.json` — coleção Postman cobrindo as cinco operações.
