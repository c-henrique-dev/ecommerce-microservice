<p align="center">
<img src="https://picocli.info/images/spring-boot.png" alt="spring and spring boot logos" height="150px">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/spring--boot-3.4.0.RELEASE-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/java-17-brightgreen.svg" alt="Java">
</p>

# # ecommerce-microservice

## Descrição do Projeto

Este é um projeto de e-commerce desenvolvido em Java utilizando o framework Spring Boot com uma arquitetura de microserviços. O objetivo principal é criar uma aplicação
funcional, incorporando tecnologias modernas para construção de APIs.

## Principais Tecnologias e Funcionalidades

  **Spring Boot**
   - Estrutura base do projeto, facilitando o desenvolvimento de APIs RESTful.

  **Feign**
   - Cliente HTTP declarativo utilizado para comunicação entre serviços, permitindo fácil integração entre microserviços.

  **Redis**
   - Utilizado como cache para melhorar a performance da aplicação, armazenando informações frequentemente acessadas, como detalhes do carrinho de compras e tokens de           usuários.

  **RabbitMQ**
   - Sistema de mensagens para implementar a comunicação assíncrona entre componentes do sistema, como notificações que o pagamento foi realizado.

  **Spring Security**
   - Gerenciamento de autenticação e autorização, garantindo que somente usuários autenticados e com permissões adequadas possam acessar determinadas funcionalidades do sistema.

  **Swagger (Springdoc OpenAPI)**
   - Documentação interativa para a API, permitindo aos desenvolvedores visualizar e testar os endpoints de maneira prática e intuitiva.

  **Spring Boot Actuator**
   - Fornece métricas detalhadas sobre a aplicação, como monitoramento de saúde, performance e configurações em tempo real, facilitando a observabilidade.

  **Spring Cloud Gateway**
   - Configurado para gerenciar todas as requisições dos clientes, funcionando como uma camada de roteamento, autenticação e autorização centralizada.
   - Facilita a comunicação com os microserviços, agregando resultados e melhorando a experiência do cliente.


  **Eureka (Spring Cloud Netflix)**
   - Serviço de registro e descoberta para facilitar o **load balancing** e o escalonamento horizontal de microserviços, garantindo alta disponibilidade.

  **Java Mail (Jakarta Mail)**
   - Integração para envio de e-mails, como confirmações de cadastro.


## Requisitos

Para construir e executar o aplicativo você precisa:

- [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven 3](https://maven.apache.org)
- [Postgres](https://www.postgresql.org/)

## Executando a aplicação localmente

- Você pode executar a aplicação localmente adaptando o arquivo `application.properties` e executando o comando abaixo:

```shell
mvn spring-boot:run
```

## Executando a aplicação com docker
```bash
docker-compose up ou docker compose up
```
