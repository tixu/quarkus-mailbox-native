# Goals 
This project aims to test  quarkus : more specifically
  - [X] Reactive Programming
  - [X] Rest Service
  - [X] AMQP integration
  - [X] OpenTracing
  - [ ] Test
  - [X] HealthChceck
  - [X] Metrics
  - [X] Openapi 
  - [X] Native Binaries with graalvm with natie image
  - [X] graalvm with custom libraries (no provided by quarkus)
  

  # Components

  The docker compose file will install the following components : 
   - AMQP (message broker)
   - H2 database (Relational Database)
   - Minio (S3 implementation)


# Initial Set-up 

The host machine must have the following components installed to be able to build the project
 - openjdk
 - Graalvm
 - GCC 
 - Zlib header
 - Maven 

The host machine must have 
  - docker 
  - docker-compose 
to run the example
