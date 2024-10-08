<div align="center">
    <img src="librefit-web/static/favicon-128x128.png">
    <p>
        <b>LibreFit:</b>
        Your OpenSource Calorie Tracker
    </p>
</div>

---

![GitHub Release](https://img.shields.io/github/v/release/tohuwabohu-io/librefit) ![Coverage](https://raw.githubusercontent.com/tohuwabohu-io/librefit/badges/jacoco.svg)

LibreFit is my hobby project that allows you to calculate your TDEE (Total Daily Energy Expenditure) based on your body
metrics, create goals for weight loss or weight gain from that and encourages you to track your daily calorie intake.
Based in your input, you will see easy to understand charts to keep on track with your goal - all that with privacy
first in mind.

* [Issue Tracker](https://github.com/tohuwabohu-io/librefit/issues)
* [Discussions](https://github.com/tohuwabohu-io/librefit/discussions)

## Introduction

LibreFit was created after tracking my caloric intake with some Excel and Google Sheets over a year. Out of boredom I
decided to try a new stack I've never done anything productive with, I quite enjoyed the journey and thus this project
was born.

### Dependencies

- Gradle 8.7
- Quarkus 3.11.2
- Quinoa 2.3.6
- Kotlin 2.0.0
- SvelteKit 2.5.2
- Skeleton 2.9.0 
- Postgres 15.2

## Structure

The project is structured as a [gradle monorepo](https://docs.gradle.org/current/userguide/structuring_software_products_details.html).

```
librefit
|- librefit-api
|- librefit-service
|- librefit-web
```

The build order is: `librefit-service` -> `librefit-api` -> `librefit-web`.

### librefit-api
A subpackage that ingests the OpenAPI spec genereated by Quarkus and produces [JSDoc](https://jsdoc.app/) descriptions 
with a homebrew generator that follows absolutely no standards which I'm both ashamed and proud of.

To generate fresh code from the spec, use

```shell
./gradlew librefit-api:ApiCodegen
```

### librefit-service

The service part of the application. For dev purpose, it expects a running Postgres instance on `http://localhost:5432`.
If you have a working docker installation, run

```shell
docker-compose up -d
```

or provide an own instance. 


To generate the latest OpenAPI spec, run

```shell
./gradlew librefit-service:buildDependents
```

This will only work with passing integration tests ;)

To start the service, run

```shell
./gradlew librefit-service:quarkusDev
```

For more details, see [librefit-service/README.md](https://github.com/tohuwabohu-io/librefit/blob/main/librefit-service/README.md).

### librefit-web

By default, librefit-service handles vite to serve directly from quarkus. If you prefer to run the modules separately,
set `%dev.quarkus.quinoa=false` in `application.properties`. 

You can either provide a node installation yourself or let gradle handle it for you. Gradle will download a [node 21.7.0](https://nodejs.org/en/blog/release/v21.7.0) 
environment for this project to use.

Start dev server with gradle:
```shell
./gradlew librefit-web:npm_run_dev
```

Start dev server with local npm installation:
```shell
cd librefit-web && npm run dev
```

For more commands, please see [librefit-web/package.json](https://github.com/tohuwabohu-io/librefit/blob/main/librefit-web/package.json) 
and also [librefit-web/README.md](https://github.com/tohuwabohu-io/librefit/blob/main/librefit-web/README.md).

## Attribution
The avatars were created with hotpot: https://hotpot.ai/art-generator

## Contribute

If you have any suggestions, impediments or things that absolutely annoy you, please refer to the issue tracker linked
at the top of this document. When you are interested in participating in development, feel free to contact me.

