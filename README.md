# Prepare

## Run docker-compose

```bash
docker compose up -d
```

## Run

```bash
./gradlew build
```

## Note
- use 'dev' Spring profile to make exchange rate values generate each 3 seconds
instead of each hour
- in the 'postman' folder you can find postman collection to test API
- to run functional tests you need to have docker environment, otherwise some of the tests will be skipped

