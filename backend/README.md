# Fantasy Backend

The backend for our fantasy football web application.

## Prequisites

We require the following tools to run our backend:

- [just](https://just.systems/man/en/)
- [Docker](https://docs.docker.com/get-started/get-docker/)
- [Java 25](https://adoptium.net/temurin/releases/?version=25&os=any&arch=any)

## `just` Commands

We use [just](https://just.systems/man/en/) as our command runner for ease of use. The following sections will reference our recipes defined in our [justfile](./justfile). 

To view all available recipes:

```sh
just -l
```

## Running

Ensure the database is running prior to running the program. See [Database Management](#database-management) for more information on working with databases in this project.

Once the database has started, use the following command to start the backend:

```sh
just run
```

To launch with the debugger:

```sh
just debug
```

To trigger hot reload during development:

```sh
just compile
just refresh # an alias for compile
```

## Database Management

### Creation

Create and start a fresh database with no initial data:

```sh
just db-create
```

Create and start a database with imported data:

```sh
just db-create-with <filepath>
```

### Exportation

Export the data from the database:

```sh
just db-export [filepath]
```

### Start

Start the database:

```sh
just start
```

### Stop

Stop the database:

```sh
just stop
```

### Destroy/Delete

Destroy the database:

```sh
just destroy
```

## Testing

Run all tests:

```sh
just test
```

## Building

Generate a jar located in the `target` directory:

```sh
just build
```