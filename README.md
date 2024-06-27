# Codex Naturalis
**_An online multiplayer videogame of the the boardgame Codex Naturalis_**
Authors: _Samuele Allegranza, Matteo Arrigo, Lorenzo Battini, Federico Bulloni_

[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://samueleallegranza.github.io/IS24-AM13/javadoc/)

## Features
|  Requirements   | Implemented |
|:-----------------------------|:---------------------------------:|
| Complete rules | ✅ |
| TUI | ✅ |
| Socket connection | ✅ |
| RMI connection | ✅ |
| Multiple matches | ✅ |
| Disconnection resilience | ✅ |
| Chat | ✅ |
| Server persistence | ⛔ |

## Running
Check the releases page of this repository to download a multi-platform jar for both the server and the client. Please note that we do only support a version of **java 21** or higher.

In the following guide we'll use:
- `SERVER_IP`: The ip of the computer which will host the server executable
- `CLIENT_IP`: The ip of the client connecting to the server

### Server
To launch the server run the command
```
java -jar codex-server.jar --ip <SERVER_IP>
```
It is important to specify the ip in order to make RMI work correctly. If not specified, expect the server to work correctly only with Socket communication.

The server accepts the following set of arguments, which are all optional:
- `--ip <ip>`                          : Sets the IP address of the server
- `--rmi <rmi port>`                   : Sets the RMI port
- `--socket <rmi port>`                : Sets the socket port
- `--points <number of points>`        : Sets the points needed for the final phase
- `--no_requirements`                  : Disables the requirement checks
- `--no_timeout_reconnection`          : Disables the timeout for reconnection
- `--no_pings`                         : Disables the check of the clients' ping


### Client
The client can be executed with a command line interface (TUI) or with a graphical interface (GUI). Will run in GUI mode if no preference is provided.
There are two different communication modes available: Socket and RMI. Will run with Socket protocol if no preference is provided.
The server must be online, otherwise the client won't start.

To run the client in GUI + Socket configuration:
```
java -jar codex-server.jar --ip <SERVER_IP>
```

Please note that RMI connection will work if the optional argument `--client_ip <CLIENT_IP>` is specified when running the client.

The client accepts the following set of arguments, which are all optional:
- `--tui`                              : Starts the application with Text User Interface (default is with Graphical User Interface)
- `--rmi`                              : Starts the application by using rmi to connect to the server (default is with socket)
- `--skip_room <number of players>`    : Skips the room phase, with the specified number of players for the room (debug purposes)
- `--skip_init`                        : Makes the choices for the initialization phase automatic (debug purposes)
- `--skip_turns`                       : Makes the choices for the turn-based phase automatic (debug purposes)
- `--no_sounds`                      : Disables all the sounds during the game
- `--ip <ip>`                          : Sets the IP address of the server
- `--port <port>`                      : Sets the port number of the server
- `--client_ip <ip>`                   : Sets the IP address of the client
