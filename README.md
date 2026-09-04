# Pistakio Gelato — Backend

API REST per l'e-commerce di **Pistakio Gelato**(https://github.com/kevindonati/PistakioGelatoFE), sviluppata con Spring Boot.

Il backend gestisce l'intero flusso dell'e-commerce: catalogo prodotti, utenti, carrello, ordini, pagamenti, spedizioni e area amministrativa.

Frontend: repository separata, sviluppata con React + TypeScript + Vite.

---

## Indice

- [Panoramica](#panoramica)
- [Funzionalità](#funzionalità)
  - [Area cliente](#area-cliente)
  - [Area amministrativa](#area-amministrativa)
  - [Pagamenti](#pagamenti)
  - [Integrazioni](#integrazioni)
- [Stack tecnologico](#stack-tecnologico)
- [Struttura del progetto](#struttura-del-progetto)
- [Autenticazione e autorizzazione](#autenticazione-e-autorizzazione)
- [Documentazione API](#documentazione-api)
  - [Swagger UI](#swagger-ui)
  - [Autenticazione tramite Swagger](#autenticazione-tramite-swagger)
  - [Specifica OpenAPI](#specifica-openapi)
- [Panoramica delle API](#panoramica-delle-api)
  - [Autenticazione](#autenticazione)
  - [Utenti](#utenti)
  - [Indirizzi](#indirizzi)
  - [Categorie](#categorie)
  - [Gusti](#gusti)
  - [Vaschette](#vaschette)
- [Ordini](#ordini)
  - [Ciclo di vita dell'ordine](#ciclo-di-vita-dellordine)
  - [Stati disponibili](#stati-disponibili)
  - [Endpoint](#endpoint)
- [Articoli dell'ordine](#articoli-dellordine)
- [Pagamenti](#pagamenti-1)
  - [Stati del pagamento](#stati-del-pagamento)
  - [Provider](#provider)
- [Spedizioni](#spedizioni)
- [Impostazioni](#impostazioni)
- [Dashboard amministrativa](#dashboard-amministrativa)
- [Database](#database)
- [Internazionalizzazione](#internazionalizzazione)
- [Gestione delle immagini](#gestione-delle-immagini)
- [Configurazione](#configurazione)
- [Avvio del progetto](#avvio-del-progetto)
  - [Requisiti](#requisiti)
  - [Avvio](#avvio)
  - [Swagger](#swagger)
- [Sicurezza](#sicurezza)
- [Stato del progetto](#stato-del-progetto)
- [Frontend](#frontend)

---


## Panoramica

Pistakio Gelato è progettato come un sistema e-commerce completo composto da due aree principali:

- **Area cliente** — consultazione del catalogo, gestione del carrello, acquisto dei prodotti e gestione dei propri dati.
- **Area amministrativa** — gestione di prodotti, clienti, ordini, pagamenti, spedizioni e statistiche.

Il backend espone una **REST API** utilizzata dal frontend per tutte le operazioni dell'applicazione.

---

## Funzionalità

### Area cliente

- Registrazione e autenticazione
- Autenticazione tramite JWT
- Gestione del profilo
- Gestione degli indirizzi di spedizione
- Catalogo multilingua
- Consultazione delle categorie
- Consultazione dei gusti
- Selezione dei formati delle vaschette
- Carrello
- Checkout
- Annullamento degli ordini
- Storico degli ordini
- Dettaglio degli ordini
- Calcolo dei costi di spedizione
- Recupero e reimpostazione della password

### Area amministrativa

- Gestione degli utenti
- Gestione dei ruoli
- Gestione delle categorie
- Gestione dei gusti
- Gestione delle vaschette
- Gestione degli ordini
- Gestione della preparazione degli ordini
- Gestione dei pagamenti
- Gestione delle spedizioni
- Configurazione dei costi di spedizione
- Dashboard amministrativa
- Statistiche sulle vendite

### Sistema email automatiche

Il backend integra un sistema di **invio automatico delle email** per comunicare all'utente le principali variazioni relative al proprio account e agli ordini.

Le email vengono generate e inviate automaticamente dal backend in base agli eventi dell'applicazione.

Il sistema può essere utilizzato, ad esempio, per:

- conferma creazione account;
- conferme relative agli ordini;
- aggiornamenti sullo stato degli ordini;
- comunicazioni relative ai pagamenti;
- recupero e reimpostazione della password;

L'invio delle email viene gestito lato backend, senza richiedere l'intervento manuale dell'amministratore.

### Pagamenti

- Integrazione con Stripe
- Integrazione con PayPal
- Gestione dello stato dei pagamenti

### Integrazioni

- Cloudinary per la gestione delle immagini
- Google Reviews
- Spedizioni internazionali
- Supporto multilingua
- Sistema automatico di invio email

---

## Stack tecnologico

| Tecnologia | Utilizzo |
|---|---|
| Java 26 | Linguaggio di programmazione |
| Spring Boot 4.1.0 | Framework backend |
| Spring Security | Autenticazione e autorizzazione |
| JWT | Autenticazione stateless |
| Spring Data JPA | Persistenza dei dati |
| Hibernate | ORM |
| PostgreSQL | Database relazionale |
| Maven | Gestione delle dipendenze |
| Stripe | Pagamenti online |
| PayPal | Pagamenti online |
| Cloudinary | Gestione delle immagini |
| Springdoc OpenAPI | Documentazione delle API |

---

# Struttura del progetto

| Directory | Responsabilità |
|---|---|
| `controller` | Espone gli endpoint REST |
| `service` | Contiene la logica applicativa |
| `repository` | Gestisce l'accesso al database |
| `entity` | Rappresenta le entità JPA |
| `dto` | Gestisce i dati trasferiti tramite API |
| `security` | Gestisce autenticazione JWT e sicurezza |
| `config` | Contiene le configurazioni |
| `exception` | Gestisce le eccezioni personalizzate |

# Autenticazione e autorizzazione

L'API utilizza un sistema di autenticazione **JWT stateless**.

Dopo un login effettuato con successo, il server restituisce un access token che deve essere inserito nelle richieste agli endpoint protetti.

```http
Authorization: Bearer <JWT>
```

### Ruoli

| Ruolo | Descrizione |
|---|---|
| `USER` | Utente standard |
| `ADMIN` | Amministratore |

Le operazioni amministrative sono protette tramite autorizzazione a livello di metodo:

```java
@PreAuthorize("hasRole('ADMIN')")
```

---

# Documentazione API

La documentazione completa delle API viene generata automaticamente tramite **OpenAPI 3** e **Swagger UI**.

## Swagger UI

Con il backend avviato:

```text
http://localhost:3001/swagger-ui.html
```

Swagger permette di:

- visualizzare tutti gli endpoint;
- consultare parametri e request body;
- visualizzare le response;
- consultare i modelli utilizzati dall'API;
- autenticarsi tramite JWT Bearer;
- eseguire richieste direttamente dalla documentazione;
- distinguere endpoint pubblici e protetti;
- visualizzare gli endpoint suddivisi per risorsa.

### Autenticazione tramite Swagger

Utilizzare il pulsante **Authorize** e inserire:

```text
Bearer <JWT>
```

Swagger aggiungerà automaticamente il token alle richieste protette.

## Specifica OpenAPI

```text
http://localhost:3001/v3/api-docs
```

---

# Panoramica delle API

## Autenticazione

| Metodo | Endpoint | Descrizione | Autenticazione |
|---|---|---|---|
| `POST` | `/auth/register` | Registrazione di un nuovo utente | ❌ |
| `POST` | `/auth/login` | Autenticazione dell'utente | ❌ |

## Utenti

| Metodo | Endpoint | Descrizione | Autenticazione |
|---|---|---|---|
| `GET` | `/users` | Recupera gli utenti | 🔒 ADMIN |
| `GET` | `/users/me` | Recupera l'utente autenticato | 🔒 |
| `GET` | `/users/{id}` | Recupera un utente | 🔒 |
| `PUT` | `/users/{id}` | Modifica un utente | 🔒 |
| `DELETE` | `/users/{id}` | Elimina un utente | 🔒 ADMIN |
| `POST` | `/users/forgot-password` | Richiede il reset della password | ❌ |
| `POST` | `/users/reset-password` | Reimposta la password | ❌ |
| `PUT` | `/users/{id}/admin` | Modifica il ruolo dell'utente | 🔒 ADMIN |

## Indirizzi

| Metodo | Endpoint | Descrizione | Autenticazione |
|---|---|---|---|
| `GET` | `/addresses` | Recupera gli indirizzi | 🔒 |
| `GET` | `/addresses/{id}` | Recupera un indirizzo | 🔒 |
| `POST` | `/addresses` | Crea un indirizzo | 🔒 |
| `PUT` | `/addresses/{id}` | Modifica un indirizzo | 🔒 |
| `DELETE` | `/addresses/{id}` | Elimina un indirizzo | 🔒 |

## Categorie

| Metodo | Endpoint | Descrizione | Autenticazione |
|---|---|---|---|
| `GET` | `/categories` | Recupera le categorie | ❌ |
| `GET` | `/categories/{id}` | Recupera una categoria | ❌ |
| `POST` | `/categories` | Crea una categoria | 🔒 ADMIN |
| `PUT` | `/categories/{id}` | Modifica una categoria | 🔒 ADMIN |
| `DELETE` | `/categories/{id}` | Elimina una categoria | 🔒 ADMIN |

## Gusti

| Metodo | Endpoint | Descrizione | Autenticazione |
|---|---|---|---|
| `GET` | `/flavors` | Recupera i gusti disponibili | ❌ |
| `GET` | `/flavors/{id}` | Recupera un gusto | ❌ |
| `POST` | `/flavors` | Crea un gusto | 🔒 ADMIN |
| `PUT` | `/flavors/{id}` | Modifica un gusto | 🔒 ADMIN |
| `DELETE` | `/flavors/{id}` | Elimina un gusto | 🔒 ADMIN |

## Vaschette

| Metodo | Endpoint | Descrizione | Autenticazione |
|---|---|---|---|
| `GET` | `/tubs` | Recupera i formati disponibili | ❌ |
| `GET` | `/tubs/{id}` | Recupera un formato | ❌ |
| `POST` | `/tubs` | Crea un formato | 🔒 ADMIN |
| `PUT` | `/tubs/{id}` | Modifica un formato | 🔒 ADMIN |
| `DELETE` | `/tubs/{id}` | Elimina un formato | 🔒 ADMIN |

---

# Ordini

Il carrello viene gestito attraverso l'entità `Order` utilizzando lo stato `CART`.

### Ciclo di vita dell'ordine

```text
CART
  │
  ▼
PENDING_PAYMENT
  │
  ▼
PAID
  │
  ▼
PREPARING
  │
  ▼
SHIPPED
  │
  ▼
DELIVERED
```

### Stati disponibili

| Stato | Descrizione |
|---|---|
| `CART` | Carrello attivo |
| `PENDING_PAYMENT` | In attesa del pagamento |
| `PAID` | Pagamento completato |
| `PREPARING` | Ordine in preparazione |
| `SHIPPED` | Ordine spedito |
| `DELIVERED` | Ordine consegnato |
| `CANCELLED` | Ordine annullato |

### Endpoint

| Metodo | Endpoint | Descrizione | Autenticazione |
|---|---|---|---|
| `GET` | `/orders` | Recupera gli ordini | 🔒 |
| `GET` | `/orders/cart` | Recupera il carrello | 🔒 |
| `GET` | `/orders/my` | Recupera i propri ordini | 🔒 |
| `GET` | `/orders/{id}` | Recupera un ordine | 🔒 |
| `GET` | `/orders/{id}/shipping-cost` | Calcola il costo di spedizione | 🔒 |
| `POST` | `/orders` | Crea un carrello | 🔒 |
| `PUT` | `/orders/{id}/checkout` | Effettua il checkout | 🔒 |
| `PUT` | `/orders/{id}/cancel` | Annulla un ordine | 🔒 |
| `PUT` | `/orders/{id}/prepare` | Avvia la preparazione | 🔒 ADMIN |

---

# Articoli dell'ordine

Gli `OrderItem` rappresentano i singoli prodotti presenti all'interno di un ordine.

Ogni articolo conserva il prezzo applicato al momento dell'acquisto tramite `unitPrice`, mantenendo così lo storico corretto anche se il prezzo del prodotto cambia successivamente.

```text
Order
 │
 ├── OrderItem
 │    ├── Flavor
 │    ├── Tub
 │    ├── Quantity
 │    └── Unit Price
 │
 └── ...
```

---

# Pagamenti

Il sistema supporta due provider:

- **Stripe**
- **PayPal**

### Stati del pagamento

| Stato | Descrizione |
|---|---|
| `PENDING` | Pagamento iniziato ma non completato |
| `COMPLETED` | Pagamento completato |
| `FAILED` | Pagamento fallito |

### Provider

| Provider | Flusso |
|---|---|
| `STRIPE` | Checkout Session + Webhook |
| `PAYPAL` | Creazione + Approvazione + Capture |

---

# Spedizioni

La gestione delle spedizioni è separata dall'ordine.

Le informazioni includono:

- corriere;
- numero di tracking;
- stato della spedizione;
- informazioni sulla consegna.

Le operazioni amministrative sulle spedizioni richiedono il ruolo `ADMIN`.

---

# Impostazioni

Le impostazioni dell'applicazione comprendono la configurazione dei costi di spedizione.

Il costo di spedizione utilizzato dal cliente può essere consultato tramite un endpoint pubblico, mentre la gestione delle impostazioni richiede autenticazione amministrativa.

---

# Dashboard amministrativa

La dashboard permette di visualizzare le principali statistiche relative all'attività dell'e-commerce.

Le statistiche possono essere filtrate per:

- giorno;
- settimana;
- mese;
- anno.

Le informazioni comprendono vendite, ordini, clienti, fatturato, ordini recenti e statistiche del periodo selezionato.

Gli endpoint della dashboard sono accessibili esclusivamente agli amministratori.

---

# Database

Il progetto utilizza **PostgreSQL** come database relazionale, con **Spring Data JPA** e **Hibernate** per la persistenza.

---

# Internazionalizzazione

Il catalogo supporta quattro lingue:

| Codice | Lingua |
|---|---|
| `IT` | Italiano |
| `EN` | Inglese |
| `FR` | Francese |
| `DE` | Tedesco |

Le traduzioni dei contenuti del catalogo vengono gestite tramite entità dedicate.

```text
Flavor
  │
  ├── IT
  ├── EN
  ├── FR
  └── DE
```

Questo permette di mantenere separati i dati principali del prodotto dalle relative traduzioni, mentre per il frontend viene usato i18n.

---

# Gestione delle immagini

Le immagini dei prodotti vengono gestite tramite **Cloudinary**.

Il database mantiene le informazioni necessarie per collegare i prodotti alle immagini, mentre Cloudinary si occupa della memorizzazione e distribuzione dei file multimediali.

---

# Configurazione

Le informazioni sensibili non vengono salvate direttamente nel repository.

La configurazione viene gestita tramite variabili d'ambiente.

Esempi:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

JWT_SECRET

STRIPE_SECRET_KEY
STRIPE_WEBHOOK_SECRET

PAYPAL_CLIENT_ID
PAYPAL_CLIENT_SECRET

CLOUDINARY_URL
```

---

# Avvio del progetto

## Requisiti

Prima di avviare il backend è necessario avere installati e configurati:

- **Java 26**
- **Maven**
- **PostgreSQL**

Sono inoltre necessarie le credenziali per i servizi esterni utilizzati dal progetto:

- **Stripe** per i pagamenti
- **PayPal** per i pagamenti
- **Cloudinary** per la gestione delle immagini
- **Google Reviews** per l'integrazione delle recensioni

## Configurazione

Prima dell'avvio è necessario configurare le variabili d'ambiente utilizzate dal backend.

Le principali variabili richieste sono:

```text
PORT
DB_URL
DB_USERNAME
DB_PASSWORD

JWT_SECRET

STRIPE_SECRET_KEY
STRIPE_WEBHOOK_SECRET
PAYMENT_SUCCESS_URL
PAYMENT_CANCEL_URL

PAYPAL_ID
PAYPAL_SECRET
PAYPAL_URL

CLOUDINARY_NAME
CLOUDINARY_SECRET
CLOUDINARY_APIKEY

MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
```

Le credenziali e le chiavi private **non devono essere inserite direttamente nel codice sorgente** e non devono essere pubblicate nel repository Git.

La configurazione viene gestita tramite le proprietà Spring e le relative variabili d'ambiente.

### Database PostgreSQL

Il backend utilizza **PostgreSQL** come database relazionale.

È necessario disporre di un database PostgreSQL attivo e raggiungibile dal backend.

Esempio di configurazione:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/pistakio_gelato
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=********
```

Il nome del database e le credenziali possono essere modificati in base alla configurazione dell'ambiente locale.

## Installazione delle dipendenze

Dalla directory principale del backend, eseguire:

```bash
mvn clean install
```

Il comando:

- pulisce le build precedenti;
- scarica le dipendenze definite nel `pom.xml`;
- compila il progetto;
- esegue i test disponibili;
- genera gli artefatti necessari all'esecuzione.

## Avvio del backend

Dopo aver configurato PostgreSQL e le variabili d'ambiente, il backend può essere avviato con:

```bash
mvn spring-boot:run
```

Se l'avvio viene completato correttamente, il backend sarà disponibile all'indirizzo:

```text
http://localhost:3001
```

La REST API utilizzerà quindi la seguente Base URL:

```text
http://localhost:3001
```

## Verifica dell'avvio

Per verificare che il backend sia stato avviato correttamente è possibile accedere a Swagger UI:

```text
http://localhost:3001/swagger-ui.html
```

Se Swagger UI viene visualizzato correttamente, significa che l'applicazione Spring Boot è attiva e che la documentazione della REST API è disponibile.

## Swagger UI

Il progetto utilizza **Springdoc OpenAPI** per generare automaticamente la documentazione delle API REST.

Swagger UI è disponibile all'indirizzo:

```text
http://localhost:3001/swagger-ui.html
```

### Autenticazione tramite Swagger

Gli endpoint protetti utilizzano un **JWT Bearer Token**.

Per ottenere un token è necessario effettuare il login tramite:

```http
POST /auth/login
```

Dopo aver ricevuto il JWT, all'interno di Swagger premere **Authorize** e inserire:

```text
Bearer <JWT>
```

Dopo l'autorizzazione, Swagger includerà automaticamente il token nelle richieste agli endpoint protetti.

Gli endpoint amministrativi richiedono inoltre un utente autenticato con ruolo:

```text
ADMIN
```

## Specifica OpenAPI

La specifica OpenAPI generata dal backend è disponibile in formato JSON:

```text
http://localhost:3001/v3/api-docs
```

Questa specifica contiene la descrizione strutturata della REST API e può essere utilizzata con strumenti compatibili con lo standard OpenAPI.

## Avvio del frontend

Il frontend è sviluppato separatamente utilizzando:

- **React**
- **TypeScript**
- **Vite**

Dopo aver avviato il backend, il frontend deve essere configurato per comunicare con la REST API:

```text
http://localhost:3001
```

Dalla directory del frontend è possibile installare le dipendenze con:

```bash
npm install
```

e avviare l'ambiente di sviluppo con:

```bash
npm run dev
```

Il frontend sarà quindi disponibile sull'indirizzo indicato da Vite, normalmente:

```text
http://localhost:5173
```

## Ordine consigliato di avvio

Per avviare correttamente l'intero progetto in ambiente locale:

1. Avviare **PostgreSQL**.
2. Verificare che il database sia disponibile.
3. Configurare le variabili d'ambiente del backend.
4. Posizionarsi nella directory `PistakioGelatoBE`.
5. Installare e compilare le dipendenze con `mvn clean install`.
6. Avviare il backend con `mvn spring-boot:run`.
7. Verificare il funzionamento tramite Swagger UI.
8. Posizionarsi nella directory `PistakioGelatoFE`.
9. Installare le dipendenze con `npm install`.
10. Avviare il frontend con `npm run dev`.
11. Verificare la comunicazione tra frontend e backend.

---

# Sicurezza

Il backend implementa:

- autenticazione stateless;
- autenticazione tramite JWT;
- autorizzazione basata sui ruoli;
- password protette tramite BCrypt;
- configurazione CORS;
- protezione degli endpoint amministrativi;
- verifica della firma dei webhook Stripe.

Le password degli utenti non vengono mai memorizzate in chiaro.

---

# Stato del progetto

Le principali funzionalità previste per il backend sono state implementate:

- [x] REST API
- [x] PostgreSQL
- [x] JPA / Hibernate
- [x] Autenticazione JWT
- [x] Autorizzazione tramite ruoli
- [x] Gestione utenti
- [x] Gestione indirizzi
- [x] Catalogo multilingua
- [x] Gestione categorie
- [x] Gestione gusti
- [x] Gestione vaschette
- [x] Carrello
- [x] Gestione ordini
- [x] Stripe
- [x] PayPal
- [x] Gestione pagamenti
- [x] Gestione spedizioni
- [x] Cloudinary
- [x] Dashboard amministrativa
- [x] Swagger / OpenAPI
- [ ] Google Reviews

---

# Frontend

Il frontend del progetto è sviluppato separatamente utilizzando:

- React
- TypeScript
- Vite

Il frontend comunica con il backend attraverso la REST API.
