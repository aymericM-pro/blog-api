# Storage Feature — Documentation technique

> Configuration GCS, upload, listing, et conversion Markdown → PDF.

---

## Configuration

Les propriétés sont dans `src/main/resources/application.yml` sous `app.gcs` :

```yaml
app:
  gcs:
    bucket-name: ${GCS_BUCKET_NAME:blog_app_back}
    credentials-path: ${GCS_CREDENTIALS_PATH:google_auth.json}
    public-url-prefix: https://storage.googleapis.com
```

Le fichier `google_auth.json` (service account GCP) doit être placé à la racine du projet. Il est exclu du dépôt via `.gitignore`.

Au démarrage, le log suivant confirme que les credentials sont bien chargés :
```
GCS: using explicit service account credentials from google_auth.json
```

---

## Endpoints

### `POST /api/v1/storage/upload`

Upload un fichier vers GCS.

- **Auth** : requise
- **Content-Type** : `multipart/form-data`
- **Paramètres** : `file` (fichier), `folder` (optionnel, défaut : `uploads`)
- **Types acceptés** : `jpeg`, `png`, `webp`, `gif`, `md`
- **Taille max** : 5 MB

```
POST /api/v1/storage/upload
Content-Type: multipart/form-data

file=<fichier>
folder=covers
```

Réponse `200` :
```json
{ "url": "https://storage.googleapis.com/blog_app_back/covers/abc123.png" }
```

---

### `GET /api/v1/storage/files`

Liste les fichiers du bucket, avec filtre optionnel par dossier.

- **Auth** : requise
- **Paramètres** : `folder` (optionnel)

```
GET /api/v1/storage/files
GET /api/v1/storage/files?folder=avatars
```

Réponse `200` :
```json
[
  {
    "name": "avatars/abc123.png",
    "url": "https://storage.googleapis.com/blog_app_back/avatars/abc123.png",
    "sizeBytes": 45230,
    "contentType": "image/png"
  }
]
```

Les pseudo-objets de dossier GCS (se terminant par `/`) sont automatiquement filtrés.

---

### `POST /api/v1/storage/markdown-to-pdf`

Récupère un fichier `.md` depuis une URL, le convertit en PDF stylisé, l'upload dans GCS sous `pdfs/` et retourne l'URL publique.

- **Auth** : requise
- **Content-Type** : `application/json`

```json
{
  "url": "https://storage.googleapis.com/blog_app_back/uploads/mon-article.md",
  "filename": "mon-article"
}
```

| Champ      | Contraintes                              |
|------------|------------------------------------------|
| `url`      | Requis, doit se terminer par `.md`       |
| `filename` | Optionnel — base du nom GCS sans extension |

Réponse `200` :
```json
{ "url": "https://storage.googleapis.com/blog_app_back/pdfs/mon-article-a3f2b1c4.pdf" }
```

**Pipeline de conversion :**
```
URL .md → fetch contenu → CommonMark (MD→HTML) → OpenHtmlToPdf (HTML→PDF) → GCS upload → URL publique
```

---

## Erreur 403 lors de la conversion Markdown → PDF

### Symptôme

```
ERROR c.e.b.s.impl.MarkdownPdfServiceImpl : Failed to fetch markdown from
  https://storage.googleapis.com/blog_app_back/uploads/xxx.md: HTTP 403
WARN  c.e.b.exception.GlobalExceptionHandler : [502] POST /api/v1/storage/markdown-to-pdf
  → Failed to upload file to storage
```

### Cause

Les objets GCS sont **privés par défaut**. Un appel HTTP anonyme vers une URL GCS retourne `403 Forbidden`, même si l'URL est "connue". Le backend ne peut pas lire le fichier `.md` sans credentials.

### Solution appliquée

`MarkdownPdfServiceImpl.fetchMarkdown()` détecte si l'URL appartient au bucket configuré :

```java
String gcsPrefix = gcsProperties.publicUrlPrefix() + "/" + gcsProperties.bucketName() + "/";

if (markdownUrl.startsWith(gcsPrefix)) {
    // Lecture directe via SDK GCS → utilise le service account → pas de 403
    String objectName = markdownUrl.substring(gcsPrefix.length());
    byte[] bytes = storage.readAllBytes(gcsProperties.bucketName(), objectName);
    return new String(bytes, StandardCharsets.UTF_8);
}

// Sinon : appel HTTP classique (URLs externes — GitHub raw, etc.)
```

| Source du `.md`               | Méthode utilisée              |
|-------------------------------|-------------------------------|
| Bucket GCS du projet          | SDK `storage.readAllBytes()`  |
| URL externe (GitHub, etc.)    | `HttpClient` HTTP GET         |

### Ce qu'il ne faut pas faire

- ❌ Rendre le bucket public (`allUsers` → `Storage Object Viewer`) — expose tous les fichiers sans authentification
- ❌ Générer une signed URL côté client — complexité inutile, le backend a déjà les credentials
- ❌ Retirer la vérification de type MIME — les fichiers `.md` uploadés sont déjà acceptés (`text/markdown`, `text/x-markdown`, `text/plain`)

---

## Dépendances ajoutées

```xml
<!-- Markdown parser -->
<dependency>
    <groupId>org.commonmark</groupId>
    <artifactId>commonmark</artifactId>
    <version>0.22.0</version>
</dependency>

<!-- HTML → PDF renderer (utilise Apache PDFBox en transitive) -->
<dependency>
    <groupId>com.openhtmltopdf</groupId>
    <artifactId>openhtmltopdf-pdfbox</artifactId>
    <version>1.0.10</version>
</dependency>
```
