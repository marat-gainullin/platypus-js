# Platypus.js

[![Build Status](https://travis-ci.org/marat-gainullin/platypus-js.svg?branch=master)](https://travis-ci.org/marat-gainullin/platypus-js)

The Platypus.js is a platform for rapid development of enterprise-level JavaScript applications.

Platypus.js is a server side JavaScript platform designed on top of JavaEE 7+ technologies and Nashorn JavaScript engine.

Platypus.js is a client side development platform as well including HTML 5 browser and Java SE clients.

Platypus.js comprises both asynchronous IO model and reasonable use of multithreading with only few intensively used threads.

Platypus.js provides a developer with libraries needed to develop complete and ready for market applications.

In general, two programming languages are used while developing Platypus.js applications:
* JavaScript (ECMAScript 5 and ECMAScript 6 partial) for application logic implementation both on server side and client side.
* Sql for accessing relational databases.

Applications creation, editing, deployment, debugging and maintenance all may be performed using the NetBeans IDE with Platypus.js plugins.

The Platypus.js plugins for NetBeans IDE include the following development tools:

* Database structure visual editor.
* Sql queries visual editor.
* JavaScript code editor.
* ORM configuration visual editor.
* User interface forms visual editor.

## Features
The platform offers a set of features, making development process extremely productive:
* AMD JavaScript modules loader on server side, in browser and in Java SE Platypus.js client.
* Server side JavaScript modules within IoC paradigm with LPC (Local procedure calls) asynchronous interactions.
* Parallel execution scheme of JavaScript code using standard Java thread pools.
* JavaScript modules automatic global dependencies resolving as well as AMD dependencies resolving.
* Sql queries with named parameters and Sql clauses re-use.
* ORM (Object-Relation Mapping) for JavaScript with automatic inter-entities references resolving. 
* Cross-platform Java SE as well as HTML5 browser UI, sharing the same code and the layout.
* The UI widgets which directly interact with the data model, allowing implementation of a CRUD (Create-Read-Update-Delete) user interface with visual configuration and without or just a little coding.
* Built-in security support, including users authentication and authorization, constraints of access to application resources.

## Build
The following commands will clone git repository and build Platypus.js from source
```
git clone https://github.com/marat-gainullin/platypus-js.git
cd platypus-js
gradlew build
```

## Test
The following command will run all tests for Platypus.js
```
gradlew test
```

## Run demos
There are some sample Platypus.js applications, showing how to build and run Platypus.js applications.
They are in the following repositories:
https://github.com/altsoft/UiDemo
https://github.com/altsoft/pethotel
https://github.com/altsoft/WebSocketDemo