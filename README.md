
# Kafka Playground — Spring Boot (Java 21)

### Monorepo sederhana berisi 2 service:

* sender — REST API untuk menerbitkan event ke Kafka dengan pola topik berbeda (no-retry, simple retry, staged retry, exclude).
* consumer — Mengkonsumsi event, mengelola retry & DLT, serta mengarsipkan DLT ke PostgreSQL untuk observability & replay.

### Fokus proyek:

* Praktik error handling Kafka di Spring: DefaultErrorHandler, @RetryableTopic, dan DLT (dead-letter topic).
* Tracing event lintas service menggunakan header x-trace-id.
* Endpoint admin untuk eksplor DLT & replay (pada implementasi saat ini endpoint admin diletakkan di sender agar consumer tetap bersih).


## Tech Stack

**Java** 21 , **Spring Boot** 3.3.x

**spring-kafka :** 3.2.x

**Spring Data JPA + PostgreSQL** 

**Kafka UI** (Opsional) Kafka UI untuk inspeksi topik & pesan


## Struktur Project

```
.
├── sender/            # Service HTTP → publish ke Kafka (+ tracing header)
├── consumer/          # Service Kafka consumer + arsip DLT ke Postgres
├── docker/            # File Docker Compose db + kafka
├── http_client/       # Referensi Skrip API
└── README.md          # Deskripsi Repo
```
## Topik & Perilaku

| Topik  | Perilaku  | Handler/Config  |
| :-------------: | :-------------: | :-------------: |
| `demo.noretry`| **No retry** → gagal langsung ke `demo.noretry.DLT`                                      | `DefaultErrorHandler(0x) + DLPRecoverer`    |
| `demo.retry.simple`  | **Simple retry (in-place)** 3x (exp backoff) → DLT                                       | `DefaultErrorHandler(3x)`                   |
| `demo.retry.staged`  | **Staged retry** via retry topics `-retry-0/-retry-1/-retry-2` → DLT                     | `@RetryableTopic + RetryTopicConfiguration` |
| `demo.retry.exclude` | **Staged retry**, tapi error tertentu (e.g. `IllegalArgumentException`) **langsung DLT** | `@RetryableTopic(exclude=...)`              |

## Cara Menjalankan

1) Jalankan consumer (membuat topik & siap konsumsi)

```bash
  mvn -q -pl consumer -am spring-boot:run
```
2) Jalankan sender (REST API)

```bash
  mvn -q -pl sender -am spring-boot:run
```


## 🚀 About Me
Passionate Learner | System Optimizer | Challenge Enthusiast


## 🔗 Links
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/yogisyaputra/)

