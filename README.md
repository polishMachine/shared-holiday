# Shared holiday microservice 

Created with Helidon SE.

## Build and run

With JDK11+
```bash
mvn package
java -jar target/helidon-se-shared-holiday-app.jar
```

By default configuration the endpoint is available under `http://localhost:8080/sharedholiday/v1`

## Assumptions
1. It is assumed that shared holiday can be found within a year from provided date or first following year. Shared holiday is searched in yearly iterations. Number of iterations can be configured.
2. Both requests and responses are in JSON format. Example of request:
```
{
    "countryCode1": "CN",
    "countryCode2": "PL",
    "date": "2012-04-14"
}
```
3. Following responses are possible:
- found shared holiday data in JSON format
- errors with error message in JSON format:
    - response with status 400 if request cannot be deserialized
    - response with status 404 when no shared holiday is found
    - response with status 503 when required holidays data is not present and it cannot be loaded from a provider
    - response with status 500 in case of any other error
- in case of failed authentication service responds with status 401

## Authorization
The shared holiday endpoint is secured with API key. Basic Authentication provider is used to secure the endpoint and it is expected that API key will be sent as a password and login should be left empty. 
API key can be configured in `application.yaml`. It is possible to override default configuration by setting JAVA system property:
```
java -Dapikey=<secret> -jar target/helidon-se-shared-holiday-app.jar
```

## Configuration

The main configuration file can be found under `src/main/resources/application.yaml`.
It is possible to override any configuration value by setting JAVA system property with the same key.

## Try health

```
curl -s -X GET http://localhost:8080/health
{"outcome":"UP",...
. . .
```
