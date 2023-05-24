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
The [User](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity/User.java) JPA entity, the most important part of the whole game is user, they are the main participants of the game, this class determines their identity information and winning information in the game, these information play an important role in the whole game

#### UserController
The [UserController](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/UserController.java) is mainly responsible for processing the RESTful request from the client related to user, and directing it to the correct service function in the server side


#### Room
The [Room](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity/Room.java) JPA entity, another important part is mainly responsible for storing the information we need to use in the game process, such as the player list in the room, and the process status of the game, etc., as an important part of controlling our game information

#### RoomService
The [RoomService](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service/RoomService.java) contains the main logic function that controls the overall process of the game. It fully cooperates with [RoomController](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/RoomController.java) and [ChatService](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service/ChatService.java) to precisely control the correct operation of the game, including the assignment of words, identities and voting, etc.

#### WebSocketConfig
The [WebSocketConfig](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/config/WebSocketConfig.java) is a Spring Boot configuration class, which is used to set the relevant configuration of WebSocket and STOMP message proxy. After configuration, the client can connect to "/ws" through WebSocket, and send and receive messages through STOMP protocol to realize real-time communication

## Deployment

### 1. Local Deployment
#### Clone Repository
Clone the client-repository onto your local machine with the help of [Git](https://git-scm.com/downloads).
```bash 
git clone https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-server.git
```

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

#### Test

```bash
./gradlew test
```

### 2. Remote Deployment
In this project, we applied Google Cloud to realize our remote deployment. You can set up Google Cloud App Engine to deploy synchronously. When the content of Github is updated, it will be automatically checked and remotely deployed to the cloud

For more inforamtion please refer to [Google Cloud Deployment](https://cloud.google.com/deploy/docs).


## Illustrition
The main user flow(s) please refer to our [client](https://github.com/sopra-fs23-group-01/sopra-fs23-group-01-client) side.


## Roadmap
New developers who want to contribute to your projec could add:
- More game modes, such as more themes
- Add kicking function to the room
- Add friend function, friends in the game can play a game together

## Authors & Acknowledgments
### Authors
* **Zihan Liu** - [zihanltesla](https://github.com/zihanltesla)
* **Han Yang** - [Haaaan1](https://github.com/Haaaan1)
* **Yixuan Zhou** - [yixuan-zhou-uzh](https://github.com/yixuan-zhou-uzh)
* **Zehao Zhang** - [Zehao-Zhang](https://github.com/Zehao-Zhang)

## Testing
Have a look here: https://www.baeldung.com/spring-boot-testing
# debug
