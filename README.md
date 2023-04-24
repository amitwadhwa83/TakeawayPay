# Takeaway Pay API
Swagger api : http://localhost:8080/swagger-ui/index.html
H2 console : http://localhost:8080/h2-console

## How to use

With the Web server started and running, perform the following requests to consume the API:

### List all transfers

**Example request:**

- **GET** (/transfers)
- **Accept:** application/json
- **Content-Type:** application/json

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
- **Accept:** application/json
- **Content-Type:** application/json

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
- **Accept:** application/json
- **Content-Type:** application/json

**Example request:**

```json
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