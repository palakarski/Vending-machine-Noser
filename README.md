# Vending Machine Service

This repository contains a Vending Machine Service implemented in Java 18 with Spring Boot 3.1.4. The purpose of this service is to help users operate vending machines by providing various functionalities for managing coins, banknotes, products, and more. This README.md file will provide an overview of the service and how to get it up and running.

## How the Vending Machine Works

- At the start of the application, a vending machine is created with the following initial setup:
    - Types of coins and banknotes.
    - Coins are filled in the machine to be used for providing change.
    - 15 products are loaded, with 10 of each product type.

## Available CRUD Operations

The Vending Machine Service provides the following base CRUD operations:

- **Insert Coin:** Insert coins into the machine.
- **Insert Banknote:** Insert banknotes into the machine.
- **Return Inserted Money:** Return the money inserted into the machine.
- **Buy Product by Slot:** Purchase a product by specifying its slot.
- **Return Change:** Return change to the user after a purchase.
- **Refill Coins for Change:** Refill the coins used for change.
- **Refill Products:** Refill the products in the machine.
- **Remove Product by Slot:** Remove a product from a specific slot.
- **Add Product:** Add a new product to the machine.
- **Get Statistic:** Retrieve statistics for profit and remaining products.
- **Take the Profit:** Withdraw the profit earned from the vending machine.

## Technologies Used

The Vending Machine Service is built using the following technologies:

- Java 18
- Spring Boot 3.1.4
- Maven 3.8.1
- Docker
- Swagger

## Getting Started

To start the application, follow these steps:

1. Clone the repository:
   ```
   git clone https://github.com/palakarski/Vending-machine-Noser-.git
   ```

2. Build the application using Maven:
   ```
   mvn clean install
   ```

### Starting the Application Locally

If you want to run the application locally from your IDE, you'll need to have MySQL server installed. Create a schema named "vending-machine" and configure the database connection in the `application.yaml` file. Port configurations can also be found in this file.

### Starting the Application with Docker

Alternatively, you can run the application in a Docker environment. After executing `mvn install` or `mvn package`, follow these steps:

1. Ensure that Docker is installed on your machine.

2. Build a Docker image for the application:
   ```
   docker build -t vending-machine .
   ```

3. Start the application and the MySQL database using Docker Compose:
   ```
   docker-compose up
   ```

The Docker Compose file contains information about the MySQL image and the vending machine image, and it will automatically start both services in a Docker environment.

## Accessing the APIs

Once the application is running, you can access the APIs in two ways:

1. **Postman Collection:** Use the provided Postman collection JSON file located at `src/main/resources/postman/Collateral Service.postman_collection.json` for example requests.

2. **Swagger Documentation:** Access the API documentation in Swagger by visiting `http://localhost:8080/swagger-ui/index.html/`.

## Conclusion

The Vending Machine Service is a Java-based application built with Spring Boot and Docker, designed to manage vending machine operations. It provides a wide range of functionalities for managing coins, banknotes, products, and more, making it easy for users to interact with the vending machine. Follow the steps above to get the application up and running, and explore the APIs through Postman or Swagger documentation.