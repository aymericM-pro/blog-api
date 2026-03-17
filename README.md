# Blog API — Spring Boot 4 / MongoDB

REST API blog avec CRUD articles, authentification JWT, Spring Security et MapStruct.

## Stack technique

| Technologie | Version |
|---|---|
| Spring Boot | 4.0.0 |
| Java | 21 |
| MongoDB | 8 |
| Spring Security + JWT | JJWT 0.12.6 |
| MapStruct | 1.6.3 |
| Lombok | 1.18.36 |
| Slugify | 3.0.6 |
| SpringDoc OpenAPI | 2.8.6 |
| Flapdoodle (tests) | 4.18.0 |

## Lancer le projet

### Option 1 — MongoDB seul (dev local)

```bash
docker compose -f docker-compose.db.yml up -d
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 2 — Stack complète Docker

```bash
docker compose -f docker-compose.dev.yml up --build
```

## Endpoints

### Auth

| Méthode | URL | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Créer un compte |
| POST | `/api/v1/auth/login` | Se connecter |

### Articles

| Méthode | URL | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/articles` | Non | Liste paginée + filtres |
| GET | `/api/v1/articles/{id}` | Non | Détail article |
| GET | `/api/v1/articles/slug/{slug}` | Non | Détail par slug |
| POST | `/api/v1/articles` | JWT (AUTHOR/ADMIN) | Créer |
| PUT | `/api/v1/articles/{id}` | JWT (auteur ou ADMIN) | Modifier |
| DELETE | `/api/v1/articles/{id}` | JWT (auteur ou ADMIN) | Supprimer |
| POST | `/api/v1/articles/{id}/bookmark` | JWT | Toggle bookmark |

### Filtres GET /articles

`category`, `tag`, `author`, `published`, `search`, `page`, `size`, `sort`

## Tests

```bash
mvn test
```

## Documentation API

Swagger UI : http://localhost:8080/swagger-ui.html
