# IOSR [![Build Status](https://travis-ci.org/lgajewski/IOSR.svg?branch=master)](https://travis-ci.org/lgajewski/IOSR)

Inżynieria Oprogramowania Systemów Rozproszonych

### Prerequisites

Run docker compose to create and start Rabbit MQ container for advanced messaging.
```
docker-compose up
```

### Running an application

Below script runs three nodes with web application that allows to control them.
To configure each node successfully you need to pass two parameters: identifier and number of nodes.

```
./gradlew assemble
java -jar node/build/libs/raft-algorithm-node-0.0.1.jar 1 3
java -jar node/build/libs/raft-algorithm-node-0.0.1.jar 2 3
java -jar node/build/libs/raft-algorithm-node-0.0.1.jar 3 3
java -jar web/build/libs/raft-algorithm-web-0.0.1.jar
```


### Important Gradle tasks
 
 ```
Application tasks
-----------------
bootRun - Run the project with support for auto-detecting main class and reloading static resources

Build tasks
-----------
assemble - Assembles the outputs of this project.
build - Assembles and tests this project.
clean - Deletes the build directory.

Verification tasks
------------------
check - Runs all checks.
jacocoTestCoverageVerification - Verifies code coverage metrics based on specified rules for the test task.
jacocoTestReport - Generates code coverage report for the test task.
test - Runs the unit tests.
```


### Project structure

The project is divided into two Gradle submodules:
 
 - node - that is a single server in distributed environment, having access to simple database
 - web - web application that allows to control and manage multiple nodes
 
```
├── node
│   ├── build
│   │   ├── libs
│   │   │   ├── raft-algorithm-node-0.0.1.jar
│   ├── build.gradle
│   └── src
│       ├── main
│       └── test
├── settings.gradle
└── web
    ├── build
    │   ├── libs
    │   │   ├── raft-algorithm-web-0.0.1.jar
    ├── build.gradle
    └── src
        ├── main
        └── test
```