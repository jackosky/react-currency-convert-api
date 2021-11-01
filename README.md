Reactive REST endpoint which handles currency conversion
=========

Design choices
--------

This project is using two APIs allowing free of charge currency conversion:
* https://api.exchangerate-api.com/v4/latest/EUR
* http://api.exchangeratesapi.io/v1/latest?base=EUR

The conversion rates table is always downloaded for EUR and based on that other conversion are happening.

For example:
In order to convert USD -> PLN, at first conversion USD -> EUR will be done and then EUR -> PLN.

Building
--------

To build sources locally follow these instructions.

### Setup access key

One of the clients requires access key. Please put your access key in `application.yml` before starting.

### Build and Run Unit Tests

Execute from project base directory:

    ./gradlew build bootJar

or use convenient script to do so

    ./build.sh

### How to start

To start application with default configuration execute

    ./start.sh

or run

    java -jar build/libs/currency-0.0.1-SNAPSHOT.jar

### Example curl API queries

Convert 1 USD -> PLN

    curl -vvv -XPOST --header "Content-Type: application/json" "http://localhost:8080/currency/convert" --data '{"from":"USD","to":"PLN", "amount":1.0}'

Example response:

    {"from":"USD","to":"PLN","amount":1.0,"converted":3.98}
