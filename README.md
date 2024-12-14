<p align="center">
<img src="https://picocli.info/images/spring-boot.png" alt="spring and spring boot logos" height="150px">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/spring--boot-3.4.0.RELEASE-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/java-17-brightgreen.svg" alt="Java">
</p>

# # ecommerce-microservice

## ATENÇÃO  

Algumas rotas estão desprotegidas, pois são utilizadas exclusivamente para a comunicação entre os microserviços. Essas rotas não fazem parte das especificações fornecidas no desafio.  

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

  **MinIO**
  - Servidor de armazenamento de objetos utilizado para realizar o upload do arquivo CSV.

  **Mockito e JUnit**  
  - O JUnit foi empregado para estruturar e executar os testes, enquanto o Mockito permitiu a simulação de dependências para garantir o isolamento das unidades de 
    código testadas.  

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

  ## Tela do Eureka exibindo os microserviços registrados e suas instâncias, permitindo monitorar o status dos serviços e realizar balanceamento de carga (load balancing):

  ![Captura de tela de 2024-12-13 22-35-09](https://github.com/user-attachments/assets/42ceac65-043c-4e4a-847f-0154ab1d235a)

  ## Um dos endpoints do spring boot actuator que retorna informações sobre a quantidade de memória que está sendo utilizada pela JVM:
  
  ![Captura de tela de 2024-12-13 23-02-41](https://github.com/user-attachments/assets/fce9f749-f29e-487c-a8d4-49346adbbdd2)

  ## Tela do Swagger do microserviço de clientes:

  ![Captura de tela de 2024-12-13 23-05-56](https://github.com/user-attachments/assets/cedd8b15-40e8-4164-bcf1-40f2be063af8)

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
