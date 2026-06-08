# server-rest

Módulo Spring Boot minimal para expor a API REST de computadores.

Build:

```bash
cd server-rest
mvn -DskipTests package
```

Run:

```bash
java -jar target/*.jar
```

Endpoints:

- `GET /api/computadores`
- `GET /api/computadores/{codigo}`
- `POST /api/computadores` (JSON)
- `DELETE /api/computadores/{codigo}`
- `GET /api/computadores/stats/categoria`

Nota: neste ambiente `mvn` não está disponível; execute build localmente.
