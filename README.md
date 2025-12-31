# Crypto Brokerage API

## Tech Stack

* **Java 21**
* **Spring Boot 3.5.9** (WebFlux, R2DBC, Validation)
* **PostgreSQL** (Reactive Driver)
* **Docker**

---

## How to Run

### 1. Start DB
Start the PostgreSQL database using Docker Compose:
```bash
docker-compose up -d
```

### 2. Start the Exchange Mock Service (Python)

```bash
# Install dependencies (if requirements.txt exists)
pip install -r requirements.txt

# Run the service
python3 exchange.py
```

### 3. Run the app

```bash
./mvnw spring-boot:run
```

### API Usage

Create an Account 

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice", "usdBalance": 50000}'
```

Get Account Details

```bash
curl http://localhost:8080/api/accounts/1
```

Place a Limit Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"accountId": 1, "priceLimit": 3500, "amount": 0.5}'
```

Check Order Status

```bash
curl http://localhost:8080/api/orders/1
```