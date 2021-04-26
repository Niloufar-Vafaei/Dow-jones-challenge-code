# Dow Jones stocks

## Install

Run the following command in the root directory of the project to build and start the application server:

```
mvn install
mvn spring-boot:run
```

## REST API
The following can be tested using Postman while the server is running.


### GET Dow Jones Indices by stock
```
http://localhost:8080/api/stocks/getByStockTickerAndQuarter?stockTicker=AA&quarter=1
```
### POST create a single new Dow Jones Index
```
http://localhost:8080/api/stocks/addStockTicker
```
Select a raw body with JSON type in Postman. Here is a test record you may use in the body of the request:
```
{"quarter": 1,"stock": "TEST","date": "2011/01/07","open": 15.82,"high": 16.72,"low": 15.78,"close": 16.42,"volume": 239655616,"percentChangePrice": 3.79267,"percentChangeVolumeOverLastWeek": null,"previousWeeksVolume": null,"nextWeeksOpen": 16.71,"nextWeeksClose": 15.97,"percentChangeNextWeeksPrice": -4.42849,"daysToNextDividend": 26,"percentReturnNextDividend": 0.182704}
```
### POST bulk data upload from file
```
http://localhost:8080/api/stocks/uploadStock
```
Add a form-data body to the request with a key value of 'file', and a type of File. Select your csv data file from your filesystem (http://archive.ics.uci.edu/ml/machine-learning-databases/00312/) under Value.

(NOTE: Swagger http://localhost:8080/swagger-ui/index.html)

# Dow-jones-challenge-code
