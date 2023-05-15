# Takeaway Pay API

## Notes:
I did not use any database implementations, but made a simple storage based on ConcurrentHashMap. 
I have not included any currency into my design for simplicity.
There can be more integration tests that can be included to test the application I focused on the important ones.
Exception handling could have been handled better with a framework approach.

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

| HTTP METHOD | PATH                     | USAGE                                            | URL                                                                                                                                                     |
|-------------|--------------------------|--------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| PUT         | /pay/createCustomer/     | Create a new customer with zero allowance        | curl -X PUT http://localhost:8080/pay/createCustomer?id=78                                                                                              | 
| PUT         | /pay/createRestaurant/   | Create a restaurant with zero account balance    | curl -X PUT http://localhost:8080/pay/createRestaurant?id=3                                                                                             | 
| GET         | /pay/getCustomerBalance  | Get cutomer allowance balance                    | curl -X GET http://localhost:8080/pay/getCustomerBalance?id=78                                                                                          | 
| GET        | /pay/getRestaurantBalance | Get restaurant account balance                   | curl -X GET http://localhost:8080/pay/getRestaurantBalance?id=3                                                                                         | 
| POST        | /pay/doTopup             | Topup customer allowance                         | curl -X POST http://localhost:8080/pay/doTopup -H "Content-Type: application/json" -d '{"customerId": 78, "topUpAmount": 20.0}'                         | 
| POST        | /pay/doTransfer          | Do tranfer from customer allowance to restaurant | curl -X POST http://localhost:8080/pay/doTransfer -H "Content-Type: application/json" -d '{"customerId": 78, "restaurantId": 3, "transferAmount": 8.0}' | 

Note : for windows based cmd runs POST request should be like this

| HTTP METHOD | PATH            | URL                                                                                                                                                |
|-------------|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| POST        | /pay/doTopup    | curl -X POST http://localhost:8080/pay/doTopup -H "Content-Type: application/json" -d "{"""customerId""":78,"""topUpAmount""":20.0}"               | 
| POST        | /pay/doTransfer | curl -X POST http://localhost:8080/pay/doTransfer -H "Content-Type: application/json" -d "{"""customerId""": 78,"""restaurantId""":3,"""transferAmount""":8.0}" | 
