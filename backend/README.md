# Fantasy backend

The backend for our fantasy football web application.

## Prequisites

We require the following tools to run our backend:

- [just](https://just.systems/man/en/)
- [Docker](https://docs.docker.com/get-started/get-docker/)
- [Java 25](https://adoptium.net/temurin/releases/?version=25&os=any&arch=any)

## `just` commands

We use [just](https://just.systems/man/en/) as our command runner for ease of use. The following sections will reference our recipes defined in our [justfile](./justfile). 

To view all available recipes:

```sh
just -l
```

## Quick start

To get started, populate the `.env` file in the backend directory with the following variables:

```
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
JWT_SECRET=your-secret-key
JWT_EXPIRATION_TIME_MILI=your-expiration-time
FRONTEND_BASE_URL=your-frontend-base-url
```

Then run the following commands:

```sh
just db-create # create and start the database
just run # start the server
```

This will launch a PostgreSQL database in a docker container. If you'd like to connect to this database elsewhere, you can use the following connection string: `postgresql://postgres:secret@localhost:5432/dev`.

In addition, the server will be launched and can be accessed at `http://localhost:8080`. While the server is running, you can view our endpoints at `http://localhost:8080/swagger-ui.html`.

If you want to learn more about what you can do with our backend, feel free to read the other sections.

## Running

Ensure the database is running prior to running the program. See [Database management](#database-management) for more information on working with databases in this project.

Once the database has started, use the following command to start a development server:

```sh
just run
```

The backend will be accessible through `http://localhost:8080` and endpoints can be viewed via Swagger UI at `http://localhost:8080/swagger-ui.html`.

With a development server, the tables in the database are automatically updated to match existing entities found in the `dev.revature.fantasy` package. 

### Production server

Unlike a development server, the production server does not automatically generate tables. In addition, it uses a separate database with the connection string: `postgresql://postgres:secret@localhost:5432/prod`. 

```sh
just prod
```

### Related running commands

To launch with the debugger:

```sh
just debug
```

To trigger hot reload while running:

```sh
just compile
just refresh # an alias for compile
```

## Endpoints

The OpenApi specification of our API is located [here](./openapi.json). 

Alternatively, it can be viewed while the server is running:
- **OpenApi**: `http://localhost:8080/v3/api-docs`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

## Database management

A PostgreSQL database in a docker container is used for our backend. Once created and started, you can utilize the following connection strings if necessary: 
- **Development**: `postgresql://postgres:secret@localhost:5432/dev` 
- **Production**: `postgresql://postgres:secret@localhost:5432/prod` 

To view the DDL initialization of the production database, view the file, `scripts/schema.sql`. See [DDL generation](#ddl-generation) for more information on this file.

### Creation

Create and start a docker container with a dev database (no tables) and prod database (with initial tables):

```sh
just db-create
```

To create and start a database with initial data, run:

```sh
just db-create-empty
just db-import <dbname> <filepath>
```

#### Example

Creating a new container with initial data for the `dev` database from a file called `dev.data`:

```sh
just db-create-empty
just db-import dev dev.data
```

### DDL generation

The tables within the database are defined using JPA entities. To verify integrity and structure of the database, a DDL script containing the table definitions and constraints can be generated and used when setting up the database, especially for a production environment.

The following command will generate the DDL script at `scripts/schema.sql`:

```sh
just ddl
```

### Exportation

Export the data from the database:

```sh
just db-export <dbname>
```

#### Example

Exporting data from the `prod` database:

```sh
just db-export prod
```

### Start

Start the database container:

```sh
just db-start
```

### Stop

Stop the database container:

```sh
just db-stop
```

### Destroy/delete

Destroy the database container:

```sh
just db-destroy
```

## Testing

Run all tests:

```sh
just test
```

## Building

Generate a jar of the server located in the `target/` directory:

```sh
just build
```