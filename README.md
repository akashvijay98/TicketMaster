   

TicketMaster Service README

Overview

This project implements a distributed ticket booking system using Spring Boot with PostgreSQL as the primary database and Redis for distributed locking.

This guide will help you:

* *   Spin up PostgreSQL and Redis containers using Docker
* *   Create and populate events, ticket, and users tables in PostgreSQL
* *   Test booking APIs (reserve and confirm) via Postman

Prerequisites

* *   Docker installed
* *   Docker Compose (optional but recommended)
* *   Postman or any API client
* *   Java 17+ and Maven (to run the Spring Boot app)

Step 1: Spin up PostgreSQL and Redis Containers

Option A: Using Docker CLI

Run the following commands:

bash



\# Start PostgreSQL container  
docker run --name ticketmaster-postgres -e POSTGRES\_PASSWORD=postgres -e POSTGRES\_DB=ticketmasterdb -p 5432:5432 -d postgres

\# Start Redis container  
docker run --name ticketmaster-redis -p 6379:6379 -d redis

Option B: Using Docker Compose

Create a docker-compose.yml with:

yaml

  

version: "3.8"  
services:  
  postgres:  
    image: postgres  
    container\_name: ticketmaster-postgres  
    environment:  
      POSTGRES\_USER: postgres  
      POSTGRES\_PASSWORD: postgres  
      POSTGRES\_DB: ticketmasterdb  
    ports:  
      \- "5432:5432"  
    volumes:  
      \- pgdata:/var/lib/postgresql/data

redis:  
    image: redis  
    container\_name: ticketmaster-redis  
    ports:  
      \- "6379:6379"

volumes:  
  pgdata:

Then run:

  

docker-compose up -d

Step 2: Connect to PostgreSQL

docker exec -it ticketmaster-postgres psql -U postgres -d ticketmasterdb

Step 3: Insert Sample Data

Insert example data into tables:

  

INSERT INTO events (id, name, event\_date)  
VALUES ('f13b5e44-8c8a-4a29-b64c-54b272b7d3c9', 'Concert 2025', '2025-07-01');

INSERT INTO users (id, name)  
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'Alice');

INSERT INTO ticket (id, price, seat\_no, status, event\_id, booking\_id)  
VALUES  
('e7f1c6a0-1f4d-4b96-a4e1-8e0b9249e8a7', 15.00, 'A1', 'AVAILABLE', 'f13b5e44-8c8a-4a29-b64c-54b272b7d3c9', NULL),  
('d24a5b56-3827-41c1-8d60-9b5bbd1c227e', 20.00, 'A2', 'AVAILABLE', 'f13b5e44-8c8a-4a29-b64c-54b272b7d3c9', NULL);

Step 4: Configure Application Properties

Make sure your application.properties has the correct connection info:

properties



spring.datasource.url=jdbc:postgresql://localhost:5432/ticketmasterdb  
spring.datasource.username=postgres  
spring.datasource.password=postgres

spring.redis.host=localhost  
spring.redis.port=6379

spring.jpa.show-sql=true  
spring.jpa.hibernate.ddl-auto=validate

Step 5: Run the Spring Boot Application

From your project root:

bash



./mvnw spring-boot:run

Or build and run the jar:

bash



./mvnw clean package  
java -jar target/TicketMaster-0.0.1-SNAPSHOT.jar

Step 6: Test APIs in Postman

Reserve Ticket

* *   Method: POST
* *   URL: [http://localhost:8080/api/bookings/reserve](http://localhost:8080/api/bookings/reserve)
* *   Query params:
* * *   ticketId = e7f1c6a0-1f4d-4b96-a4e1-8e0b9249e8a7
* * *   userId = 550e8400-e29b-41d4-a716-446655440000

Confirm Booking

* *   Method: POST
* *   URL: [http://localhost:8080/api/bookings/confirm](http://localhost:8080/api/bookings/confirm)
* *   Query params:
* * *   ticketId = e7f1c6a0-1f4d-4b96-a4e1-8e0b9249e8a7
* * *   userId = 550e8400-e29b-41d4-a716-446655440000

Additional Tips

* *   Check Redis keys to debug locks:

bash

  

docker exec -it ticketmaster-redis redis-cli  
127.0.0.1:6379> keys ticket-lock:\*

* *   View PostgreSQL data:

sql

  

SELECT \* FROM ticket;  
SELECT \* FROM events;  
SELECT \* FROM users;

If you follow these steps, you should be able to run your distributed ticket booking system locally with Redis and PostgreSQL containers and test the endpoints easily.
