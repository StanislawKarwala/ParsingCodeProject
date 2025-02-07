# ParsingCodeProject

## Opis projektu
ParsingCodeProject to aplikacja służąca do parsowania, przechowywania i udostępniania danych SWIFT poprzez REST API. Aplikacja wykorzystuje Spring Boot, PostgreSQL oraz Dockera do łatwego wdrażania i zarządzania środowiskiem.

## Wymagania
Przed rozpoczęciem pracy z projektem upewnij się, że masz zainstalowane:
- **Git** – do pobrania repozytorium
- **Docker** – do konteneryzacji aplikacji
- **Java 17+** – do uruchomienia aplikacji
- **Maven** – do zarządzania zależnościami i testowania

## Instalacja i uruchomienie

### 1. Sklonowanie repozytorium
Najpierw sklonuj repozytorium na swój lokalny komputer:
```sh
git clone https://github.com/StanislawKarwala/ParsingCodeProject
```

Przejdź do katalogu projektu:
```sh
cd ParsingCodeProject
```

### 2. Budowanie obrazu Dockera
```sh
docker build -t parsing-code-app .
```

### 3. Uruchomienie kontenera Dockera
```sh
docker run -d -p 8080:8080 --name parsing-container parsing-code-app
```

### 4. Sprawdzenie statusu kontenera
Możesz sprawdzić listę kontenerów:
```sh
docker ps -a
```

### 5. Wejście do kontenera
```sh
docker exec -it parsing-container sh
```

### 6. Uruchomienie testów
```sh
mvn test
```

## Dodatkowe informacje
- Aplikacja domyślnie korzysta z bazy danych PostgreSQL.
- Wszystkie logi aplikacji można znaleźć w konsoli kontenera Dockera.
- Jeśli chcesz ponownie uruchomić kontener, możesz go usunąć i uruchomić ponownie.

## Autor
Stanislaw Karwala

