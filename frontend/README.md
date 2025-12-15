# Fantasy frontend

The frontend for our fantasy football web application.

## Prequisites

We require the following tools to run our frontend:

- [NodeJS v24](https://nodejs.org/en)

## Quick start

To get started, run the following commands:

```sh
npm ci # install dependencies
npm start # start the frontend
```

This will serve the frontend on `http://localhost:4200`.

## Wireframes

View our wireframes [here](./wireframes.pdf).

## Running

Before running, ensure that you've installed dependencies:

```sh
npm ci
```

Once you have the `node_modules/` directory, you can run the server:

```sh
npm start
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Building

To build the project run:

```sh
npm run build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Testing

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```sh
npm test
```
