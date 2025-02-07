# ParsingCodeProject

## Opis projektu
ParsingCodeProject to aplikacja napisana w Javie, która analizuje kod źródłowy w języku Swift. Projekt wykorzystuje Spring Boot oraz bazę danych PostgreSQL.

## Wymagania
Aby uruchomić projekt, upewnij się, że masz zainstalowane:
- **Java 23 (Oracle JDK)**
- **Maven**
- **PostgreSQL**

## Instalacja i konfiguracja
### 1. Klonowanie repozytorium
```sh
git clone https://github.com/StanislawKarwala/ParsingCodeProject.git
cd ParsingCodeProject
```

### 2. Konfiguracja bazy danych
1. Utwórz bazę danych PostgreSQL.
2. Zaktualizuj plik `application.properties` lub `application.yml` w katalogu `src/main/resources/`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/twoja_baza
spring.datasource.username=twoj_uzytkownik
spring.datasource.password=twoje_haslo
```

### 3. Uruchomienie aplikacji
#### Z IntelliJ IDEA
1. Otwórz projekt w IntelliJ IDEA.
2. Skonfiguruj bazę danych w `application.properties`.
3. Uruchom aplikację, wybierając klasę główną i klikając **Run**.

#### Z Maven
```sh
mvn clean install
mvn spring-boot:run
```

## Endpointy API
Aplikacja działa na porcie **8080**.

## Autor
Stanislaw Karwala
