# MCP Lucene Server

## Description

The MCP Lucene Server is a Java-based implementation of the Model Context Protocol (MCP) designed to provide efficient search and retrieval capabilities using Apache Lucene. This server allows you to manage and query documents, leveraging Lucene's powerful indexing and search features. It is built using Spring Boot for easy setup and deployment.


## Features

* **MCP Compliance:** Implements the core Model Context Protocol.

* **Lucene-Powered:** Utilizes Apache Lucene for full-text search and indexing.

* **RESTful API:** Provides a RESTful API for interacting with the server.

* **Document Management:**

    * **Upsert:** Add or update documents in the Lucene index.

    * **Delete:** Delete documents from the Lucene index.

    * **List:** Retrieve a list of documents from the index.

* **Querying:**

    * Supports complex queries using the Lucene query syntax.

    * Filtering: Filter queries based on document metadata.

* **Status:** Check the server status.

* **Spring Boot:** Built with Spring Boot for easy setup and deployment.
* **Dockerization:** Includes instructions for containerizing the application using Docker.

## Table of Contents

* [Description](#description)

* [Features](#features)

* [Getting Started](#getting-started)

    * [Prerequisites](#prerequisites)

    * [Installation](#installation)

    * [Running the Server](#running-the-server)

* [Usage](#usage)

    * [API Endpoints](#api-endpoints)

    * [Examples](#examples)

* [Configuration](#configuration)

* [License](#license)

## Getting Started

### Prerequisites

* **Java:** Java 11 or higher.

* **Maven:** Maven 3.6.0 or higher.
* **Docker:** [Install Docker](https://docs.docker.com/get-docker/) if you plan to use the Docker image.

### Installation

1.  **Clone the repository:**

    ```
    git clone [https://github.com/your-username/mcp-lucene-server.git](https://github.com/your-username/mcp-lucene-server.git)
    cd mcp-lucene-server
    ```

    (Replace `your-username` with your GitHub username)

2.  **Build the project using Maven:**

    ```
    mvn clean install
    ```

### Running the Server

#### Without Docker

1.  **Run the Spring Boot application:**
    ```bash
    java -jar target/mcp-lucene-server-0.0.1-SNAPSHOT.jar
    ```
    (The exact name of the `.jar` file might vary slightly depending on your project version.)

2.  The server will start on port `8080` by default.

#### With Docker

1.  **Ensure you have Docker installed:** Follow the instructions on the official Docker website: [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/)
2.  **Build the Docker image:**
    Navigate to the root directory of your project in your terminal and run:
    ```bash
    docker build -t mcp-lucene-server .
    ```

5.  **Run the Docker container:**
    ```bash
    docker run -p 8080:8080 mcp-lucene-server
    ```
    This will map port `8080` on your host machine to port `8080` inside the container.

## Usage

### API Endpoints

The server provides the following API endpoints:

* `GET /mcp/v1/status`

    * Returns the status of the server.

* `POST /mcp/v1/upsert`

    * Upserts (inserts or updates) one or more documents.

    * Request body:

        ```json
        {
          "documents": [
            {
              "id": "doc1",
              "text": "This is the text of document 1.",
              "metadata": {
                "category": "example",
                "language": "english"
              }
            },
            {
              "id": "doc2",
              "text": "This is document 2's text.",
              "metadata": {
                "category": "sample",
                "language": "spanish"
              }
            }
          ]
        }
        ```

* `POST /mcp/v1/query`

    * Queries the Lucene index.

    * Request body:

        ```json
        {
          "queries": [
            {
              "query": "document",
              "top_k": 10,
              "filter": {
                "language": "english"
              }
            },
             {
              "query": "text search",
              "filter": {
                 "category": "example"
               }
             }
          ]
        }
        ```

    * `query`: The Lucene query string.

    * `top_k`: (Optional) The maximum number of results to return (default: 10).

    * `filter`: (Optional) A map of metadata fields and values to filter by.

* `POST /mcp/v1/delete`

    * Deletes documents from the Lucene index.

    * Request body:

        ```json
        {
            "ids": ["doc1", "doc2"]
        }
        ```

* `GET /mcp/v1/list`

    * Lists documents from the Lucene index.

    * Request body:

        ```json
        {
            "ids": ["doc1", "doc2"]
        }
        ```

### Examples

**Get server status:**

```bash
curl http://localhost:8080/mcp/v1/status
```

**Upsert documents:**

```bash
curl -X POST 

http://localhost:8080/mcp/v1/upsert 

-H 'Content-Type: application/json' 

-d '{
"documents": [
{
"id": "doc1",
"text": "This is the text of document 1.",
"metadata": {
"category": "example",
"language": "english"
}
},
{
"id": "doc2",
"text": "This is document 2''s text.",
"metadata": {
"category": "sample",
"language": "spanish"
}
}
]
}'
```

**Query documents:**

```bash
curl -X POST 

http://localhost:8080/mcp/v1/query 

-H 'Content-Type: application/json' 

-d '{
"queries": [
{
"query": "document text",
"top_k": 5,
"filter": {
"language": "english"
}
}
]
}'
```

**Delete documents:**

```bash
curl -X POST 

http://localhost:8080/mcp/v1/delete 

-H 'Content-Type: application/json' 

-d '{
"ids": ["doc1"]
}'
```

**List documents:**

```bash
curl -X POST 

http://localhost:8080/mcp/v1/list 

-H 'Content-Type: application/json' 

-d '{
"ids": ["doc1", "doc2"]
}'
```

## Configuration

The server can be configured using Spring Boot's application properties. Here are some of the key properties:

* `server.port`: The port the server listens on (default: 8080).

* `lucene.index.path`: The path to the Lucene index directory. This is where the indexed data is stored. If not set, a default location is used. It is highly recommended to configure this to a persistent storage location.

You can set these properties in an `application.properties` or `application.yml` file in your `src/main/resources` directory, or by using environment variables.

**Example `application.properties`:**


server.port=8080
lucene.index.path=/path/to/lucene/index

## License

This project is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).
