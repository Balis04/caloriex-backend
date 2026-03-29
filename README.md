# Caloriex Backend Docker

Ez a backend Spring Boot + PostgreSQL alapon fut, es Dockerrel ket kontenerben tudod inditani:

- `postgres` az adatbazisnak
- `backend` a Spring Boot API-nak

## Miert kell kulon Docker?

Dockerrel ugyanazt a futasi kornyezetet kapod lokalban es deploynal is. Ez azert fontos, mert:

- a backend nem a sajat geped `localhost` adatbazisat keresi majd
- a PostgreSQL kulon szolgaltataskent fut
- a frontendet kesobb egy masik helyen is hosztolhatod, csak az API URL-t es a CORS domaint kell beallitani

## Fontos kulonbseg lokalban es Dockerben

Lokal futtataskor az adatbazis URL tipikusan:

```properties
DB_URL=jdbc:postgresql://localhost:5432/caloriex
```

Docker Compose alatt a backend a Postgrest a szolgaltatas neve alapjan eri el:

```properties
DB_URL=jdbc:postgresql://postgres:5432/caloriex
```

Ez az egyik leggyakoribb hiba Dockeresitesnel.

## Szukseges .env valtozok

Peldak:

```env
POSTGRES_PASSWORD=postgres
USDA_API_KEY=your_usda_api_key
AUTH0_ISSUER_URI=https://your-tenant.us.auth0.com/
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,https://sajat-frontend-domain.hu
```

Session authhoz a backend alapertelmezett session timeoutja 7 nap. Ezt az
`APP_SESSION_TIMEOUT` valtozoval tudod felulirni, ha rovidebbre vagy hosszabbra
akarod venni.

## File policy

- A coach certificate fajlok publikus dokumentumkent kezelhetok, ezert a rendszer publikus URL-t ad vissza rajuk.
- A training plan fajlok vedett dokumentumok: a backend storage key-t tarol, es nem ad vissza nyers publikus fajllinket.
- A training plan letoltes az ownership-ellenorzott `GET /api/training-requests/{trainingRequestId}/training-plan/download` endpointon keresztul tortenik.
- A training plan fajlt csak az erintett coach es az a user toltheti le, akinek a terv keszult.

## Inditas

```bash
docker compose up --build
```

Ha minden rendben, akkor:

- PostgreSQL: `localhost:5433`
- Backend API: `http://localhost:8080`

## Frontend masik repoban

Ha a frontend kulon GitHub repoban van, az teljesen jo. Tipikus felallas:

1. ez a repo deployolja a backendet + adatbazist
2. a frontend repo deployolja a weboldalt
3. a frontend a backend publikus URL-jet hivja
4. a backend CORS-ban engedi a frontend domainjet

Peldak:

- frontend: `https://caloriex-frontend.vercel.app`
- backend: `https://api.caloriex.hu`

Ebben az esetben:

```env
APP_CORS_ALLOWED_ORIGINS=https://caloriex-frontend.vercel.app
```

## Deploy gondolkodas

Kesobb hostingnal altalaban ez kell:

1. egy szerver vagy platform a backendnek
2. egy menedzselt PostgreSQL vagy sajat Postgres kontener
3. domain a backendnek, pl. `api.sajatoldal.hu`
4. frontendben a backend publikus URL-je
5. backendben a frontend domain CORS allowlistre teve

## Hasznos parancsok

```bash
docker compose up --build
docker compose down
docker compose logs -f backend
docker compose logs -f postgres
```
