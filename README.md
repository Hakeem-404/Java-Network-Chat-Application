# Java Network Chat Application

A client-server chat application developed for the COMP1549 Advanced Programming module at the University of Greenwich. This application demonstrates advanced Java programming concepts including concurrent programming, networking, design patterns, and GUI development.

## Overview

This application is a robust chat system that enables multiple clients to connect to a server and exchange messages. It features both public and private messaging capabilities, with a graphical user interface for the client. The system implements the Observer design pattern for communication between components.

## Features

- **Multi-client support**: Connect multiple clients to a single server
- **Public messaging**: Send messages to all connected clients
- **Private messaging**: Direct messages to specific clients using the format `@username message`
- **Coordinator role**: First client to connect becomes the coordinator, with role transfer if they disconnect
- **GUI interface**: User-friendly interface with separate panels for public and private messages
- **Unique client identification**: Each client receives a unique ID upon connection
- **JUnit testing**: Comprehensive test suite for both client and server components

## Architecture

### Design Patterns

- **Observer Pattern**: Implemented through the `Observer` and `Subject` interfaces to enable loose coupling between components and facilitate event-driven communication
- **Client-Server Architecture**: Separation of client and server functionality for scalability

### Components

- **Server**: Manages client connections and message routing
- **ClientHandler**: Handles communication with individual clients
- **Client**: Provides user interface and connection to the server
- **Observer/Subject**: Interfaces implementing the Observer design pattern

## Technologies

- Java SE
- Java Swing (GUI)
- Java Networking (Sockets)
- Java Concurrency (Threads)
- JUnit (Testing)

## Classes

### Server
The server component manages client connections and acts as the message broker. Key features:
- Thread pool for handling multiple clients
- Implementation of the Subject interface for observer pattern
- Port 3000 for client connections

### Client
The client application provides the user interface and handles communication with the server. Key features:
- Split-pane UI with separate panels for public and private messages
- Connection dialog for server address and port
- Unique client ID generation
- Private messaging with @username syntax

### ClientHandler
Handles the communication between the server and an individual client. Key features:
- Implements the Observer interface
- Manages the coordinator role
- Routes messages between clients

### Subject & Observer
Interfaces implementing the Observer design pattern to facilitate communication between components.

## Running the Application

### Requirements
- Java Development Kit (JDK) 8 or higher
- Java Runtime Environment (JRE)

### Starting the Server
1. Compile the application
2. Run the Server class
```
java codexx.Server
```
The server will listen on port 3000 by default.

### Starting a Client
1. Compile the application
2. Run the Client class
```
java codexx.Client
```
3. Enter the server address and port when prompted
4. Choose a screen name when prompted

## Testing

The application includes JUnit tests for both Client and Server components. Run the tests to verify functionality:

```
java org.junit.runner.JUnitCore codexx.test.ServerTest codexx.test.ClientTest
```

## Commands

- Regular text - sends a public message to all clients
- `@username message` - sends a private message to the specified user
- `members` - displays a list of all online members
- `/quit` - disconnects from the server

## Project Structure

```
codexx/
├── Client.java - Client application with GUI
├── ClientHandler.java - Handles client communication with server
├── Observer.java - Observer interface for the observer pattern
├── Server.java - Chat server implementation
├── Subject.java - Subject interface for the observer pattern
└── test/
    ├── ClientTest.java - Tests for the Client class
    └── ServerTest.java - Tests for the Server class
```

## Academic Context

This project was developed as part of the COMP1549 Advanced Programming module at the University of Greenwich, which focuses on:
- Professional techniques for code and design reuse
- Advanced coding techniques (threads, generic classes)
- Use of professional tools (unit testing, version control)

## Future Enhancements

Potential improvements for future versions:
- End-to-end encryption for private messages
- File sharing capabilities
- User authentication
- Message persistence
- Custom user profiles
- Message formatting options
- Read receipts for private messages
- Mobile client application

## License

This project is for educational purposes as part of coursework at the University of Greenwich.

## Author

Hakeem Kasali
COMP1549 Advanced Programming
University of Greenwich
