# ROSE

ROSE jest webowym systemem przeznaczonym do wspierania codziennego zarządzania restauracją. Aplikacja integruje w jednym środowisku obsługę użytkowników, restauracji, pracowników, grafików, menu, zamówień, pracy kuchni oraz rachunków.

System umożliwia właścicielowi utworzenie konta i restauracji, a następnie zarządzanie jej personelem i konfiguracją. Pracownicy, zależnie od posiadanych uprawnień, mogą korzystać z funkcji związanych między innymi z obsługą zamówień, przygotowywaniem pozycji zamówienia oraz rozliczaniem rachunków.

Do najważniejszych funkcjonalności systemu należą:

* rejestracja, logowanie i weryfikacja konta za pomocą wiadomości e-mail,
* odnawianie sesji użytkownika przy użyciu tokenów dostępu i odświeżania,
* tworzenie restauracji i zarządzanie dostępem użytkowników,
* przypisywanie użytkownikom ról w restauracji,
* zarządzanie pracownikami i ich stanowiskami,
* tworzenie i edytowanie grafików pracy,
* tworzenie menu, kategorii oraz pozycji menu,
* obsługa wielu wariantów menu restauracji,
* tworzenie i modyfikowanie zamówień,
* obsługa kolejnych etapów przygotowania zamówienia przez kuchnię,
* tworzenie rachunków na podstawie zamówień,
* rejestrowanie płatności i zarządzanie statusem rachunku,
* udostępnianie funkcjonalności zależnie od roli użytkownika.

Aplikacja posiada publiczną część internetową z możliwością rejestracji i logowania oraz panel restauracji dostępny dla zalogowanych użytkowników. W panelu można wybrać restaurację i przejść do modułów dotyczących pracowników, grafików, menu, zamówień i rachunków.

System został zrealizowany w architekturze mikrousługowej. Poszczególne usługi posiadają oddzielne zakresy odpowiedzialności oraz niezależne bazy danych PostgreSQL. Komunikacja pomiędzy usługami odbywa się za pomocą RabbitMQ, natomiast żądania aplikacji frontendowej są kierowane do odpowiednich usług przez API Gateway.

ROSE jest akronimem od *Restaurant Operations System Environment*, czyli Środowiska Systemu Operacji Restauracyjnych.

# Architektura

Backend systemu składa się z następujących mikrousług:

* **Identity Service** – konta użytkowników, uwierzytelnianie i autoryzacja,
* **Organization Service** – zarządzanie restauracjami,
* **Access Service** – członkostwa, role i uprawnienia użytkowników,
* **Workforce Service** – pracownicy i grafiki pracy,
* **Menu Service** – menu, kategorie oraz pozycje menu,
* **Order Service** – zamówienia i obsługa pracy kuchni,
* **Billing Service** – rachunki i rejestrowanie płatności,
* **Gateway Service** – wspólny punkt dostępu do API systemu.

Każda z głównych mikrousług posiada własną bazę danych. Dane są synchronizowane pomiędzy usługami za pomocą zdarzeń przesyłanych przez RabbitMQ. W systemie zastosowano między innymi mechanizmy Inbox i Outbox, które ograniczają ryzyko utraty lub wielokrotnego przetworzenia komunikatów.

# Technologie

## Backend

* Java 21,
* Spring Boot 4,
* Spring Security,
* Spring Data JPA,
* Spring Cloud Gateway,
* Hibernate,
* JWT,
* RabbitMQ,
* PostgreSQL,
* Liquibase,
* OpenAPI / Swagger.

## Frontend

* Angular 21,
* TypeScript,
* HTML,
* CSS,
* Node.js 22.

## Infrastruktura

* Docker,
* Docker Compose,
* Nginx,
* MailHog,
* API Gateway,
* osobna baza danych PostgreSQL dla każdej usługi.

Wszystkie mikrousługi, aplikacja frontendowa, bazy danych, RabbitMQ oraz pozostałe elementy infrastruktury mogą zostać uruchomione wspólnie w kontenerach Docker za pomocą pliku Docker Compose.

# Bezpieczeństwo

Dostęp do chronionych zasobów wymaga uwierzytelnienia użytkownika. System wykorzystuje tokeny JWT oraz mechanizm tokenów odświeżania. Uprawnienia są określane na podstawie członkostwa użytkownika w restauracji i przypisanej mu roli.

Dane poszczególnych restauracji są odseparowane logicznie. Użytkownik może wykonywać operacje wyłącznie w restauracjach, do których posiada odpowiedni dostęp.

# Uruchamianie

Cały system może zostać zbudowany i uruchomiony za pomocą Docker Compose. Po uruchomieniu aplikacja frontendowa komunikuje się z backendem przez usługę Gateway, która udostępnia jeden wspólny punkt wejścia do API.

Środowisko kontenerowe uruchamia między innymi:

* aplikację Angular,
* API Gateway,
* wszystkie mikrousługi backendowe,
* bazy danych PostgreSQL,
* RabbitMQ,
* MailHog.

# Informacje

Projekt został zrealizowany w ramach przedmiotów **Projekt Grupowy I** oraz **Projekt Grupowy II** na Wydziale Elektroniki, Telekomunikacji i Informatyki Politechniki Gdańskiej w roku akademickim 2025/2026.
