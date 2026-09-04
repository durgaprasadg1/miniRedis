# MiniRedis

A lightweight, in-memory Redis-inspired key-value server implemented in Java 21. MiniRedis provides a TCP-based command interface, multiple data structures, key expiration, concurrent client handling, and a comprehensive automated test suite.

> **Project status:** Functional and tested.
> **Tests:** 107 passed, 0 failed, 0 errors.
> **Build:** Maven `BUILD SUCCESS`.

## Features

- TCP server using Java sockets
- Line-based text protocol
- Multiple concurrent client connections
- Fixed-size thread pool for client handling
- Thread-safe shared storage
- Lazy and active key expiration
- TTL management
- String, List, Set, and Hash data types
- Command dispatcher and handler architecture
- Type validation with `WRONGTYPE` responses
- Unit, integration, and concurrency tests

## Tech Stack

| Technology | Usage |
|---|---|
| Java 21 | Application implementation |
| Maven | Build and dependency management |
| JUnit | Automated testing |
| TCP / Java Sockets | Client-server communication |
| `ConcurrentHashMap` | Shared top-level storage |
| `ExecutorService` | Concurrent client handling |
| `ScheduledExecutorService` | Active TTL expiration |

## Architecture

```text
                         Client
                           │
                           │ TCP
                           ▼
                ┌─────────────────────┐
                │   MiniRedisServer   │
                │                     │
                │  ServerSocket       │
                │  Thread Pool        │
                └──────────┬──────────┘
                           │
                           ▼
                     ┌───────────┐
                     │  Decoder  │
                     └─────┬─────┘
                           │
                           ▼
                  ┌────────────────┐
                  │ CommandParser  │
                  └───────┬────────┘
                          │
                          ▼
                    ┌───────────┐
                    │  Command  │
                    └─────┬─────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │CommandDispatcher │
                 └────────┬─────────┘
                          │
                          ▼
                  ┌────────────────┐
                  │ CommandHandler │
                  └───────┬────────┘
                          │
                          ▼
                 ┌─────────────────┐
                 │  RedisStorage   │
                 │                 │
                 │ Concurrent Map  │
                 │ TTL             │
                 │ String/List     │
                 │ Set/Hash        │
                 └─────────────────┘
                          │
                          ▼
                     ┌───────────┐
                     │  Encoder  │
                     └─────┬─────┘
                           │
                           ▼
                         Client
```

### Separation of Responsibilities

**`MiniRedisServer`**
- Accepts TCP connections
- Creates client tasks
- Reads and writes network data
- Does not contain command-specific business logic

**`Decoder`**
- Reads line-based requests
- Converts requests into `Command` objects

**`CommandParser`**
- Extracts command name and arguments

**`CommandDispatcher`**
- Maps command names to handlers
- Routes commands without implementing command logic

**Command Handlers**
- Validate arguments
- Execute command-specific behavior
- Convert storage errors into client responses

**`RedisStorage`**
- Maintains shared in-memory state
- Handles data structures
- Handles TTL
- Provides synchronization for compound/mutable operations

**`Encoder`**
- Converts server responses into newline-delimited protocol responses

## Supported Commands

MiniRedis currently supports **30 commands**.

### Basic Operations

| Command | Description |
|---|---|
| `PING` | Check server availability |
| `SET` | Store a string value |
| `GET` | Retrieve a string value |
| `DEL` | Delete a key |
| `EXISTS` | Check whether a key exists |
| `SIZE` | Return the number of live keys |

```text
PING
PONG

SET name ram
OK

GET name
ram

EXISTS name
1

SIZE
1

DEL name
1
```

### String Operations

| Command | Description |
|---|---|
| `INCR` | Increment an integer string by 1 |
| `DECR` | Decrement an integer string by 1 |
| `INCRBY` | Increment an integer string by a specified amount |
| `DECRBY` | Decrement an integer string by a specified amount |

```text
SET counter 10
OK

INCR counter
11

DECR counter
10

INCRBY counter 5
15

DECRBY counter 3
12
```

### TTL / Expiration

| Command | Description |
|---|---|
| `EXPIRE` | Set expiration time in seconds |
| `TTL` | Get remaining TTL |
| `PERSIST` | Remove expiration from a key |

```text
SET session abc
OK

EXPIRE session 10
1

TTL session
9

PERSIST session
1

TTL session
-1
```

TTL semantics:

```text
-1 → key exists but has no expiration
-2 → key does not exist
positive value → remaining TTL in seconds
```

MiniRedis uses both:
- **Lazy expiration**: expired keys are detected when accessed.
- **Active expiration**: a background task checks for expired keys every second.

### Optional SET Expiration

MiniRedis also supports:

```text
SET <key> <value> <seconds>
```

Example:

```text
SET temporary hello 3
OK

GET temporary
hello

TTL temporary
3
```

After expiration:

```text
GET temporary
(nil)
```

This is an extension to the basic `SET` syntax rather than a separate command.

### List Operations

| Command | Description |
|---|---|
| `LPUSH` | Insert at the left side |
| `RPUSH` | Insert at the right side |
| `LLEN` | Return list length |
| `LPOP` | Remove from the left |
| `RPOP` | Remove from the right |
| `LRANGE` | Return a range of list elements |

```text
LPUSH nums 10
1

LPUSH nums 20
2

RPUSH nums 30
3

LRANGE nums 0 -1
20 10 30

LPOP nums
20

RPOP nums
30
```

Negative indexes are supported by `LRANGE`.

### Set Operations

| Command | Description |
|---|---|
| `SADD` | Add a member |
| `SREM` | Remove a member |
| `SISMEMBER` | Check membership |
| `SMEMBERS` | Return all members |
| `SCARD` | Return set cardinality |

```text
SADD users ram
1

SADD users shyam
1

SADD users ram
0

SISMEMBER users ram
1

SISMEMBER users abc
0

SCARD users
2

SREM users ram
1
```

Sets are unordered, so the order returned by `SMEMBERS` is not guaranteed.

### Hash Operations

| Command | Description |
|---|---|
| `HSET` | Set a hash field |
| `HGET` | Get a hash field |
| `HDEL` | Delete a hash field |
| `HEXISTS` | Check whether a field exists |
| `HLEN` | Return number of fields |
| `HGETALL` | Return all fields and values |

```text
HSET user name ram
1

HSET user age 20
1

HGET user name
ram

HGET user age
20

HEXISTS user name
1

HLEN user
2

HGETALL user
name ram age 20
```

`HSET` returns:
- `1` when the field is newly created
- `0` when an existing field is updated

## Data Types

MiniRedis supports:

```text
STRING
LIST
SET
HASH
```

Each stored key is represented internally by an `Entry` containing:

```text
DataType
Value
Expiration timestamp
```

## WRONGTYPE Handling

A key cannot be used as different incompatible data types.

```text
SET test hello
OK

SADD test ram
WRONGTYPE

LPUSH test ram
WRONGTYPE

HSET test name ram
WRONGTYPE

GET test
hello
```

## Error Handling

Examples:

```text
GET
ERR wrong number of arguments
```

```text
INCR counter
ERR value is not an integer
```

```text
UNKNOWN
ERR unknown command
```

## Concurrency

MiniRedis supports multiple clients concurrently.

The server uses a fixed thread pool:

```java
Executors.newFixedThreadPool(4)
```

The shared storage uses:

```java
ConcurrentHashMap
```

Compound operations such as increments and mutable collection operations use synchronization where required.

The concurrency test verifies that concurrent increments produce:

```text
Expected: 100000
Actual:   100000
```

The project also contains a separate concurrency experiment demonstrating the race condition that can occur without synchronization.

## Project Structure

```text
mini-redis/
│
├── pom.xml
├── README.md
│
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── miniredis/
    │               ├── Main.java
    │               │
    │               ├── command/
    │               │   ├── Command.java
    │               │   ├── CommandDispatcher.java
    │               │   ├── CommandHandler.java
    │               │   ├── CommandParser.java
    │               │   └── handler/
    │               │       ├── PingHandler.java
    │               │       ├── SetHandler.java
    │               │       ├── GetHandler.java
    │               │       ├── DeleteHandler.java
    │               │       ├── ExistsHandler.java
    │               │       ├── SizeHandler.java
    │               │       ├── IncrementHandler.java
    │               │       ├── DecrementHandler.java
    │               │       ├── IncrementByHandler.java
    │               │       ├── DecrementByHandler.java
    │               │       ├── ExpireHandler.java
    │               │       ├── TtlHandler.java
    │               │       ├── PersistHandler.java
    │               │       ├── LpushHandler.java
    │               │       ├── RpushHandler.java
    │               │       ├── LlenHandler.java
    │               │       ├── LpopHandler.java
    │               │       ├── RpopHandler.java
    │               │       ├── LrangeHandler.java
    │               │       ├── SaddHandler.java
    │               │       ├── SremHandler.java
    │               │       ├── SismemberHandler.java
    │               │       ├── SmembersHandler.java
    │               │       ├── ScardHandler.java
    │               │       ├── HsetHandler.java
    │               │       ├── HgetHandler.java
    │               │       ├── HdelHandler.java
    │               │       ├── HexistsHandler.java
    │               │       ├── HlenHandler.java
    │               │       └── HgetallHandler.java
    │               │
    │               ├── protocol/
    │               │   ├── Decoder.java
    │               │   └── Encoder.java
    │               │
    │               ├── server/
    │               │   └── MiniRedisServer.java
    │               │
    │               └── storage/
    │                   ├── DataType.java
    │                   ├── Entry.java
    │                   └── RedisStorage.java
    │
    └── test/
        └── java/
            └── com/
                └── miniredis/
                    ├── command/
                    ├── concurrency/
                    ├── integration/
                    ├── protocol/
                    └── storage/
```

## Requirements

- Java 21 or later
- Maven

Verify:

```powershell
java -version
mvn -version
```

## Build

From the project root:

```powershell
mvn clean package
```

Expected result:

```text
BUILD SUCCESS
```

## Run

The current `Main` class starts MiniRedis with:

```text
Host: localhost
Port: 6379
Worker threads: 4
```

Run `Main.java` from your IDE.

If the Maven configuration produces an executable JAR:

```powershell
java -jar target/<jar-name>.jar
```

Successful startup:

```text
Mini Redis server started on : 6379
```

## Testing

Run the complete automated test suite:

```powershell
mvn clean test
```

Current verified result:

```text
Tests run: 107
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

The test suite covers:

- Command parsing
- Command dispatching
- Storage operations
- Data structures
- TTL and expiration
- Error handling
- TCP integration
- Multiple clients
- Concurrent operations
- Decoder behavior

## Manual Testing

After starting the server, connect to:

```text
localhost:6379
```

and execute commands such as:

```text
PING
SET name ram
GET name
LPUSH nums 10
SADD users ram
HSET user name ram
EXPIRE user 10
TTL user
```

Responses are newline-delimited.

## Design Decisions

### In-memory storage

MiniRedis stores data in memory to keep the implementation focused on core key-value database concepts.

### ConcurrentHashMap

The top-level key-to-entry mapping uses `ConcurrentHashMap` to support concurrent client access and safe interaction with the expiration task.

### Synchronization

Operations involving read-modify-write behavior or mutable collection state use synchronization where required to avoid lost updates and inconsistent state.

### Handler-based command architecture

Each command has its own handler implementing the common `CommandHandler` interface. This keeps command logic separated and makes the dispatcher independent of individual command implementations.

### Active + lazy expiration

Using both mechanisms provides practical expiration behavior:
- Lazy expiration handles expired keys when they are accessed.
- Active expiration removes expired entries periodically even when they are not accessed.

### Scope

The project intentionally focuses on a limited Redis-inspired feature set rather than attempting to implement the complete Redis protocol or all Redis data types.

## Limitations

MiniRedis is an educational Redis-inspired implementation and is not intended to replace Redis in production environments.

Current limitations include:

- Data is lost when the server stops.
- No persistence to disk.
- No authentication or authorization.
- No replication or clustering.
- No transactions.
- No Redis RESP protocol compatibility.
- No complete Redis command set.
- `SET` currently uses a whitespace-delimited command format, so values containing arbitrary spaces are not supported as a single value.

## Verification Summary

The implementation has been verified with:

```text
Automated tests       107 passed
Failures              0
Errors                0
Skipped               0
Maven package         BUILD SUCCESS
Manual smoke tests    Passed
Multi-client tests    Passed
Concurrent INCR       Passed
TTL tests             Passed
List tests            Passed
Set tests             Passed
Hash tests            Passed
```

## License

This project is intended as an educational/academic implementation.
