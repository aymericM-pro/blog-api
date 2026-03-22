# Review Feature — Documentation technique

> Comment la section "Avis lecteurs" a été conçue, structurée et connectée au reste du blog.

---

## Structure des fichiers

```
src/main/java/com/example/blog/
├── domain/
│   └── Review.java                                  ← Document MongoDB
├── repository/
│   └── ReviewRepository.java                        ← Requêtes Spring Data
├── dto/
│   └── ReviewDtos.java                              ← ReviewRequest + ReviewResponse
├── mapper/
│   └── ReviewMapper.java                            ← MapStruct (entité ↔ DTO)
├── service/
│   ├── ReviewService.java                           ← Interface métier
│   └── impl/ReviewServiceImpl.java                  ← Implémentation
├── controller/
│   ├── ReviewController.java                        ← Interface REST + OpenAPI
│   └── impl/ReviewControllerImpl.java               ← Délégation au service
├── event/
│   └── ReviewCreatedEvent.java                      ← Événement Spring
└── enums/
    └── ReviewError.java                             ← Codes d'erreur 4xxx
```

**Fichiers modifiés :**
- `enums/NotificationType.java` — ajout de `NEW_REVIEW`
- `event/NotificationEventListener.java` — ajout de `onReviewCreated`
- `config/SecurityConfig.java` — ajout de la règle POST pour les reviews

---

## Le modèle `Review`

Collection MongoDB : `reviews`

| Champ             | Type            | Description                                                   |
|-------------------|-----------------|---------------------------------------------------------------|
| `id`              | `String`        | Identifiant MongoDB                                           |
| `articleId`       | `String`        | Référence à `Article._id` (indexé)                           |
| `articleSlug`     | `String`        | Dénormalisé pour les requêtes par slug (indexé)              |
| `reviewerUserId`  | `String`        | Référence à `User._id` — pour l'ownership et l'unicité       |
| `author`          | `Author`        | Snapshot embarqué au moment de la création                   |
| `rating`          | `int`           | Note de 1 à 5                                                 |
| `content`         | `String`        | Commentaire (max 2000 caractères)                             |
| `createdAt`       | `LocalDateTime` | Date de création (géré par Spring Data, `@CreatedDate`)      |

**Index composé unique** : `(articleId, reviewerUserId)` — garantit qu'un utilisateur ne peut poster qu'une seule review par article.

**Snapshot auteur** : l'objet `Author` est embarqué dans le document `Review` au moment de la création (via `userMapper.toAuthor(user)`). Il ne sera pas mis à jour si l'utilisateur change son profil — choix délibéré pour préserver l'historique, identique au pattern utilisé dans `Article.author`.

---

## Endpoints REST

Base URL : `/api/v1/articles/{slug}/reviews`

### `GET /api/v1/articles/{slug}/reviews`

Liste les reviews d'un article, triées du plus récent au plus ancien.

- **Auth** : non requise (public)
- **Réponse** : `200 OK`

```json
[
  {
    "id": "68a1b2c3d4e5f6a7b8c9d0e1",
    "author": {
      "id": "67f0a1b2c3d4e5f6a7b8c9d0",
      "name": "Sakura T.",
      "avatar": "https://...",
      "role": "AUTHOR"
    },
    "rating": 5,
    "content": "Un article vraiment excellent, très bien écrit.",
    "publishedAt": "2026-03-21T10:30:00"
  }
]
```

---

### `POST /api/v1/articles/{slug}/reviews`

Soumet une nouvelle review sur l'article identifié par `{slug}`.

- **Auth** : requise (`Authorization: Bearer <token>`)
- **Réponse** : `201 Created`
- **Erreurs** :
  - `404` — article non trouvé
  - `409` — l'utilisateur a déjà reviewé cet article

**Corps de la requête :**
```json
{
  "rating": 4,
  "content": "Très bon article, quelques points pourraient être approfondis."
}
```

| Champ     | Contraintes                      |
|-----------|----------------------------------|
| `rating`  | Requis, entier entre 1 et 5      |
| `content` | Requis, max 2000 caractères      |

---

### `DELETE /api/v1/articles/{slug}/reviews/{reviewId}`

Supprime une review.

- **Auth** : requise
- **Autorisation** : auteur de la review ou `ADMIN`
- **Réponse** : `204 No Content`
- **Erreurs** :
  - `404` — review non trouvée
  - `403` — l'utilisateur n'est ni l'auteur ni un admin

---

## Codes d'erreur (ReviewError)

| Code | Message                                    | HTTP |
|------|--------------------------------------------|------|
| 4001 | Review not found                           | 404  |
| 4002 | You have already reviewed this article     | 409  |
| 4003 | You are not allowed to perform this action | 403  |

---

## Flow événementiel — Notification à l'auteur

Quand une review est créée, un événement `ReviewCreatedEvent` est publié. Le `NotificationEventListener` l'intercepte et crée une notification pour l'auteur de l'article.

```
POST /api/v1/articles/{slug}/reviews
        │
        ▼
ReviewServiceImpl.createReview()
        │
        ├── reviewRepository.save(review)
        │
        └── eventPublisher.publishEvent(new ReviewCreatedEvent(review, article))
                │
                ▼
        NotificationEventListener.onReviewCreated()
                │
                ├── authorRepository.findById(article.getAuthorId())
                │     └── résout le userId réel de l'auteur
                │
                ├── notificationService.create(userId, NEW_REVIEW, ...)
                │
                └── sseEmitterService.push(userId, notification)
                      └── notification temps réel via SSE
```

La notification contiendra :
- **Titre** : `"New review on your article"`
- **Message** : `"\"<titre de l'article>\" received a new <N>-star review."`

---

## Sécurité

| Méthode  | Route                                        | Auth requise | Couverture                                   |
|----------|----------------------------------------------|-------------|----------------------------------------------|
| `GET`    | `/api/v1/articles/{slug}/reviews`            | Non         | Couverte par la règle existante `GET /api/v1/articles/**` |
| `POST`   | `/api/v1/articles/{slug}/reviews`            | Oui         | Règle ajoutée explicitement dans `SecurityConfig` |
| `DELETE` | `/api/v1/articles/{slug}/reviews/{reviewId}` | Oui         | Couverte par la règle existante `DELETE /api/v1/articles/**` |

---

## Connexion à l'article

Le `slug` de l'article est le seul identifiant exposé dans l'URL, cohérent avec le reste de l'API (`GET /api/v1/articles/slug/{slug}`). Le service résout l'`Article` complet via `articleRepository.findBySlug(slug)` pour :

1. Vérifier que l'article existe avant toute opération
2. Récupérer l'`articleId` à stocker dans la review
3. Passer l'`Article` dans l'événement (pour éviter un appel DB supplémentaire dans le listener)

---

## Roadmap (prochaines étapes)

| Étape | Description |
|-------|-------------|
| 1 | Ajouter la pagination (`Page<ReviewResponse>`) sur `GET` côté backend + infinite scroll ou bouton "Voir plus" côté frontend |
| 2 | Exposer la note moyenne sur l'endpoint `GET /api/v1/articles/slug/{slug}` (champ `averageRating` dans `ArticleResponse`) |
| 3 | Permettre la mise à jour d'une review existante (`PUT /api/v1/articles/{slug}/reviews/{reviewId}`) |
| 4 | Ajouter un système de modération (signalement, masquage par un admin) |
