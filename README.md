![Build](https://github.com/username/repo/actions/workflows/maven.yml/badge.svg)
![Coverage](https://img.shields.io/codecov/c/github/username/repo)
![License](https://img.shields.io/github/license/username/repo)


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

## MCP Shim for Claude Desktop

This project includes an optional MCP shim (`mcp-shim/`) that exposes the server's REST endpoints as MCP tools over STDIO so you can use them directly from Claude Desktop.

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.6+

### 1) Run the Spring Boot server
```bash
mvn spring-boot:run
```
The API will be available at `http://localhost:8080/mcp/v1`.

### 2) Run the MCP shim
```bash
cd mcp-shim
npm install
# JSON + text output (default)
LUCENE_BASE_URL=http://localhost:8080/mcp/v1 npm start
# If your client cannot render JSON tool outputs, force text-only
MCP_FORCE_TEXT=1 LUCENE_BASE_URL=http://localhost:8080/mcp/v1 npm start
```

### 3) Configure Claude Desktop
Update `~/.claude/mcp/config.json`:
```json
{
  "mcpServers": {
    "lucene": {
      "command": "/opt/homebrew/bin/node",
      "args": [".../MCP-Lucene-Server/mcp-shim/server.js"],
      "env": {
        "LUCENE_BASE_URL": "http://localhost:8080/mcp/v1",
        "MCP_FORCE_TEXT": "1"
      }
    }
  }
}
```
Alternatively, use the wrapper script to capture shim logs to `/tmp/mcp-lucene-shim.stderr.log`:
```bash
cat > .../MCP-Lucene-Server/mcp-shim/run-shim.sh <<'SH'
#!/usr/bin/env bash
set -euo pipefail
export LUCENE_BASE_URL="${LUCENE_BASE_URL:-http://localhost:8080/mcp/v1}"
exec node .../MCP-Lucene-Server/mcp-shim/server.js \
  2> /tmp/mcp-lucene-shim.stderr.log
SH
chmod +x .../MCP-Lucene-Server/mcp-shim/run-shim.sh
```
Then set in `~/.claude/mcp/config.json`:
```json
{
  "mcpServers": {
    "lucene": {
      "command": ".../MCP-Lucene-Server/mcp-shim/run-shim.sh",
      "env": {
        "LUCENE_BASE_URL": "http://localhost:8080/mcp/v1",
        "MCP_FORCE_TEXT": "1"
      }
    }
  }
}
```

### 4) Available tools
- `lucene_status`: Get server/index status
- `lucene_upsert`: Upsert documents
- `lucene_query`: Query documents (with optional metadata filters)
- `lucene_delete`: Delete by IDs
- `lucene_list`: List documents with pagination

### 5) Example prompts for Claude Desktop
- Run `lucene_status`
- Run `lucene_list` with: `{ "limit": 10, "offset": 0 }`
- Run `lucene_upsert` with: `{"documents":[{"id":"doc-1","text":"hello world","metadata":{"lang":"en"}}]}`
- Run `lucene_query` with: `{"queries":[{"query":"hello","top_k":5}]}`
- Run `lucene_delete` with: `{ "ids": ["doc-1"] }`

### 6) Troubleshooting
- Verify the API returns JSON:
```bash
curl -i http://localhost:8080/mcp/v1/status
```
- If Claude shows "unsupported format", start the shim with text-only output:
```bash
MCP_FORCE_TEXT=1 LUCENE_BASE_URL=http://localhost:8080/mcp/v1 npm start
```
- View shim logs (when using wrapper):
```bash
tail -n +1 /tmp/mcp-lucene-shim.stderr.log
```
- Ensure the paths in your `config.json` are absolute and correct, then restart Claude Desktop.


### API Endpoints (for Curl)

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
