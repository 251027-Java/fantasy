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

To quickly get started, run the following commands:

```sh
just db-create # create and start the database
just run # start the server
```

This will launch a PostgreSQL database in a docker container. If you'd like to connect to this database elsewhere, you can use the following connection string: `postgresql://postgres:secret@localhost:5432/postgres`.

In addition, the server will be launched and can be accessed at `http://localhost:8080`. While the server is running, you can access the documentation of our endpoints at `http://localhost:8080/api`.

If you want to learn more about what you can do with our backend, feel free to read the other sections.

## Running

Ensure the database is running prior to running the program. See [Database management](#database-management) for more information on working with databases in this project.

Once the database has started, use the following command to start a development server:

```sh
just run
```

The backend will be accessible through `http://localhost:8080`. While running, documentation of our endpoints will be located at `http://localhost:8080/api`.

### Related running commands

To launch with the debugger:

```sh
just debug
```

To trigger hot reload during development:

```sh
just compile
just refresh # an alias for compile
```

To launch a production server:

```sh
just prod
```

## Database management

A PostgreSQL database in a docker container is used for our backend. Once created and started, you can utilize the following connection string if necessary: `postgresql://postgres:secret@localhost:5432/postgres`. 

To view the DDL initialization of the database, view the file, `scripts/init.sql`. See [DDL generation](#ddl-generation) for more information on this file.

### Creation

Create and start a fresh database with no initial data:

```sh
just db-create
```

Create and start a database with imported data:

```sh
just db-create-with <filepath>
```

### DDL generation

The tables within the database are defined using JPA entities. To verify integrity and structure of the database, a DDL script containing the table definitions and constraints can be generated and used when setting up the database, especially for a production environment.

The following command will generate the DDL script at `scripts/init.sql`:

```sh
just ddl
```

### Exportation

Export the data from the database:

```sh
just db-export [filepath]
```

### Start

Start the database:

```sh
just db-start
```

### Stop

Stop the database:

```sh
just db-stop
```

### Destroy/delete

Destroy the database:

```sh
just db-destroy
```

## Testing

Run all tests:

```sh
just test
```

## Building

Generate a jar located in the `target/` directory:

```sh
just build
```