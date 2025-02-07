# 1. Pobieramy bazowy obraz OpenJDK 17 z najnowszym Alpine
FROM openjdk:17-alpine

# 2. Instalujemy PostgreSQL i inne zależności
RUN apk update && apk add --no-cache \
    postgresql \
    postgresql-contrib \
    bash \
    su-exec \
    tzdata \
    maven \
    && rm -rf /var/cache/apk/*

# 3. Tworzymy katalogi dla PostgreSQL i ustawiamy uprawnienia
RUN mkdir -p /var/lib/postgresql/data /run/postgresql \
    && chown -R postgres:postgres /var/lib/postgresql /run/postgresql \
    && chmod 777 /run/postgresql

# 4. Ustawiamy użytkownika postgres i inicjalizujemy bazę
USER postgres
RUN initdb -D /var/lib/postgresql/data

# 5. Konfigurujemy PostgreSQL
RUN echo "host all all 0.0.0.0/0 md5" >> /var/lib/postgresql/data/pg_hba.conf \
    && echo "listen_addresses='*'" >> /var/lib/postgresql/data/postgresql.conf

# 6. Tworzymy bazę danych i użytkownika
RUN pg_ctl -D /var/lib/postgresql/data start && \
    sleep 5 && \
    psql --username=postgres -c "CREATE DATABASE swiftcodesdb;" && \
    psql --username=postgres -c "ALTER USER postgres WITH PASSWORD 'admin';" && \
    pg_ctl -D /var/lib/postgresql/data stop

# 7. Przełączamy się na użytkownika root
USER root

# 8. Tworzymy katalog aplikacji
WORKDIR /app
COPY . .

# 9. Pobieramy wait-for-it
RUN wget -O wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x wait-for-it.sh

# 10. Budujemy aplikację Spring Boot (Maven)
RUN mvn clean package -DskipTests

# 11. Otwieramy port 8080
EXPOSE 8080

# 12. Uruchamiamy PostgreSQL i aplikację Spring Boot
CMD ["sh", "-c", "su-exec postgres pg_ctl -D /var/lib/postgresql/data start && \
    ./wait-for-it.sh localhost:5432 --timeout=30 --strict -- \
    java -jar target/ParsingCodeProject-0.0.1-SNAPSHOT.jar"]
