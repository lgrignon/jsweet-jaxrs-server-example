# JSweet + JAX-RS / Jersey + Knockout JS example: rate your coffee shops [SERVER]

This project holds:
1) a server for the [JSweet Ionic client example](https://github.com/lgrignon/jsweet-cordova-ionic-example) using a JAX-RS (Jersey) web api
2) an example of a web interface written in JSweet + Knockout

## Pre-requisits

The following steps require administrator rights

* Ensure maven command line (mvn) is in PATH by typing `mvn -version`, if command not found, download apache maven 3.3.x and include its bin folder in PATH, then try again
* Ensure node packet manager (npm) is in PATH by typing `npm -v`, if command not found, download and install NodeJS, then try again

## Compiling

Compile JSweet client code using
```
mvn clean generate-sources -P client 
```

JavaScript files are generated in `src/main/webapp/js/app`, and candies are generated in `src/main/webapp/js/candies`

## Running

Deploy on a glassfish server and browse context root. Persistence unit configuration is not mandatory since web api use mocks. It is just a template :)


