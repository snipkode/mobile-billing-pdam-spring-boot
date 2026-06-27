# Mobile Billing PDAM — Spring Boot Backend

REST API backend untuk aplikasi mobile billing PDAM Indonesia.
Dibangun dengan **Spring Boot 3.3**, **Clean Architecture**, **JWT Auth**, dan **H2/PostgreSQL**.

## Tech Stack

- Java 17 + Spring Boot 3.3.5
- Spring Security (JWT via jjwt 0.12)
- Spring Data JPA + Hibernate
- H2 (development) / PostgreSQL (production)
- Lombok + Bean Validation

## Arsitektur

```
src/main/java/id/pdam/billing/
├── domain/
│   ├── entity/          # JPA Entities: Pelanggan, Tagihan, Notifikasi, Pengaduan
│   └── repository/      # Spring Data JPA interfaces
├── application/
│   ├── dto/             # Request & Response records
│   ├── mapper/          # Entity → DTO mappers
│   └── usecase/         # Business logic: AuthService, BillingService, dll
├── infrastructure/
│   ├── config/          # SecurityConfig
│   ├── security/        # JwtService, JwtAuthFilter, UserDetailsServiceImpl
│   └── seeder/          # DataSeeder (H2 only)
└── presentation/
    ├── controller/      # REST controllers
    └── advice/          # GlobalExceptionHandler
```

## API Endpoints

Base URL: `http://localhost:8080/api/v1`

### Auth
| Method | Endpoint | Auth | Deskripsi |
|--------|----------|------|-----------|
| POST | `/auth/login` | ❌ | Login dengan nomor pelanggan & password |
| POST | `/auth/logout` | ✅ | Logout (client-side token invalidation) |

### Billing
| Method | Endpoint | Auth | Deskripsi |
|--------|----------|------|-----------|
| GET | `/billing/current` | ✅ | Tagihan bulan ini (status BELUM_LUNAS) |
| GET | `/billing/history` | ✅ | Riwayat pemakaian & tagihan |
| POST | `/billing/pay` | ✅ | Bayar tagihan |

### Notifikasi
| Method | Endpoint | Auth | Deskripsi |
|--------|----------|------|-----------|
| GET | `/notifications` | ✅ | Daftar notifikasi |
| PUT | `/notifications/read-all` | ✅ | Tandai semua sudah dibaca |

### Pengaduan
| Method | Endpoint | Auth | Deskripsi |
|--------|----------|------|-----------|
| POST | `/complaints` | ✅ | Buat pengaduan baru |
| GET | `/complaints` | ✅ | Riwayat pengaduan |

### Profil
| Method | Endpoint | Auth | Deskripsi |
|--------|----------|------|-----------|
| GET | `/profile` | ✅ | Data pelanggan saat ini |

## Request / Response

### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "nomorPelanggan": "12345678",
  "password": "password123"
}
```
```json
{
  "success": true,
  "message": null,
  "data": {
    "token": "eyJhbGci...",
    "type": "Bearer",
    "user": { "id": 1, "nama": "Budi Setiawan", ... }
  }
}
```

Gunakan token di header selanjutnya:
```http
Authorization: Bearer eyJhbGci...
```

### Bayar Tagihan
```http
POST /api/v1/billing/pay
Authorization: Bearer {token}

{
  "tagihanId": 1,
  "metode": "Transfer Bank"
}
```

### Buat Pengaduan
```http
POST /api/v1/complaints
Authorization: Bearer {token}

{
  "kategori": "Air Tidak Mengalir",
  "deskripsi": "Air tidak mengalir selama 2 hari di RT 05."
}
```

## Setup & Jalankan

### Prasyarat
- Java 17+
- Maven 3.6+

### Jalankan (H2 — Development)
```bash
mvn spring-boot:run
```

H2 Console tersedia di: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:pdam`
- Username: `sa` / Password: *(kosong)*

### Jalankan dengan PostgreSQL
```bash
# Set env variables
export DB_USER=postgres
export DB_PASS=postgres

mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Build JAR
```bash
mvn clean package -DskipTests
java -jar target/billing-0.0.1-SNAPSHOT.jar
```

## Data Seed (H2)

Saat profile `h2` aktif, data berikut otomatis dibuat:

| Field | Value |
|-------|-------|
| Nomor Pelanggan | `12345678` |
| Password | `password123` |
| Nama | Budi Setiawan |

Tagihan: Oktober (BELUM_LUNAS), September & Agustus 2023 (LUNAS).

## Variabel Konfigurasi

| Property | Default | Keterangan |
|----------|---------|------------|
| `app.jwt.secret` | `404E63...` | JWT signing key (ganti di production!) |
| `app.jwt.expiration` | `86400000` | Token TTL (ms) — default 24 jam |
| `DB_USER` | `postgres` | PostgreSQL username |
| `DB_PASS` | `postgres` | PostgreSQL password |

## Deployment JAR (Production Server)

### 1. Build JAR
```bash
mvn clean package -DskipTests
# Output: target/billing-0.0.1-SNAPSHOT.jar
```

### 2. Transfer ke Server
```bash
scp target/billing-0.0.1-SNAPSHOT.jar user@server:/opt/billing/
```

### 3. Buat systemd Service
```bash
sudo nano /etc/systemd/system/billing.service
```

```ini
[Unit]
Description=PDAM Billing Spring Boot
After=network.target

[Service]
WorkingDirectory=/opt/billing
ExecStart=/usr/bin/java -jar billing-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=5
Environment=SPRING_PROFILES_ACTIVE=h2

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable billing
sudo systemctl start billing
```

### 4. Perintah Berguna
```bash
systemctl status billing        # cek status
systemctl restart billing       # restart
journalctl -u billing -f        # lihat log live
journalctl -u billing -n 100    # 100 baris log terakhir
```

### 5. HTTPS via Stunnel (opsional)
Jika aplikasi berjalan di belakang stunnel untuk SSL termination:

```ini
# /etc/stunnel/stunnel.conf
[springboot]
accept  = 8080
connect = 127.0.0.1:8082
cert    = /etc/letsencrypt/live/domain.tld/fullchain.pem
key     = /etc/letsencrypt/live/domain.tld/privkey.pem
client  = no
```

```bash
sudo systemctl restart stunnel4
```

> Pastikan port yang digunakan (misal 8080) dibuka di firewall:
> ```bash
> sudo ufw allow 8080/tcp
> ```

## Related

- Frontend: [mobile-billing-pdam-rn](https://github.com/snipkode/mobile-billing-pdam-rn) — React Native
