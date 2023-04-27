# Takeaway Pay API
Swagger api : http://localhost:8080/swagger-ui/index.html
H2 console : http://localhost:8080/h2-console

## Build:
```
mvn clean package
```

## Run:
- Run the app, default port 8080:
```
java -jar target/TakeawayPay-0.0.1-SNAPSHOT.jar
```

- Run the app, specify the port:
```
java -jar target/TakeawayPay-0.0.1-SNAPSHOT.jar -p 6666
```

- Run Test Cases:
```
mvn test
```

| HTTP METHOD | PATH              | USAGE                                  |
| -----------|-------------------|----------------------------------------|
| GET | /transfers/       | get all transfers                         | 
| GET | /accounts/        | get all acounts information            | 
| POST | /transfer/create/ | perform transaction between 2 accounts | 

## How to use

With the Web server started and running, perform the following requests to consume the API:

### List all transfers

**Example request:**

- **GET** (/transfers)
##### Request:
```sh
GET /transfers
```
**Example response:**

```json
[
  {
    "id": 1,
    "sourceAccount": 1,
    "destAccount": 2,
    "amount": 4,
    "lastUpdate": "2023-04-23T01:31:14.672742"
  }
]
```

### List all accounts

**Example request:**

- **GET** (/accounts/)
##### Request:
```sh
GET /accounts
```
**Example response:**

```json
[
  {
    "id": 1,
    "balance": 36,
    "lastUpdate": "2023-04-23T01:31:12.006365",
    "customer": true
  },
  {
    "id": 2,
    "balance": 14,
    "lastUpdate": "2023-04-23T01:31:12.087122",
    "customer": false
  }
]
```

### Take money from the customer's account and put it in the restaurant's account.

**Example request:**

- **POST** (/transfer/create)

**Example request:**
##### Request:
```sh
POST /transfer/create
```
```sh
{
  "sourceAccount": 1,
  "destAccount": 2,
  "amount": 4,
  "lastUpdate": "2023-04-24T18:54:35.487Z"
}
```
**Example response:**

```string
 3
```