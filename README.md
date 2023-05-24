# Who is undercover?  :shipit:
[![Deploy Project to App Engine](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/actions/workflows/main.yml/badge.svg)](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/actions/workflows/main.yml)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.4.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring WebSocket](https://img.shields.io/badge/Spring%20WebSocket-latest-blue.svg)](https://spring.io/projects/spring-websocket) 


## Table of Content

- [Introduction](#introduction)
- [Built With](#built-with)
- [High-level components](#main-components)
- [Deployment](#deployment)
- [Illustrations](#illustrations)
- [Roadmap](#roadmap)
- [Authors & Acknowledgments](#authors--acknowledgments)
- [License](#license)

## Introduction
"Who's Undercover" is a very popular social party game from China that usually requires 4 or more players to play together. The game aims to find out the "Undercover" characters hidden among all players through description and reasoning.

## Built With
* [React](https://react.dev/) - Frontend JavaScript library developed by facebook
* [Spring](https://spring.io/projects/spring-framework) - Framework that enables running JVM
* [Gradle](https://gradle.org/) - Build automation tool
* [STOMP](https://stomp-js.github.io/stomp-websocket/) - Text agreement for configuring WebSocket connections 
* [Spring WebSocket ](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket) - Real-time interactive applications
* [SockJS](https://github.com/sockjs) - API compatible with WebSocket
* [Datamuse API](https://www.datamuse.com/api/) - Datamuse Api to get synonyms

## Main Components

#### User
The [User](https://github.com/sopra-fs23-group-10/sopra-fs23-group-10-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity/User.java) JPA entity, the most important part of the whole game is user, they are the main participants of the game, this class determines their identity information and winning information in the game, these information play an important role in the whole game

### IntelliJ
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs23` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```

### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

## Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing
Have a look here: https://www.baeldung.com/spring-boot-testing
# debug
