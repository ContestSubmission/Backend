# ContestSubmission - Backend

This project uses Quarkus, the Supersonic Subatomic Java Framework.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```
To view information about the application, access the DevUI at http://localhost:8080/q/dev/.

## Configuration
Since quarkus is quite shitty when it comes to configuration files, we have to use either environment variables or
a .yml file. For development, the latter is recommended.

Create the `src/main/resources/application.yml` file and configure away! Check the
[config-yaml](https://quarkus.io/guides/config-yaml) extension for more information. Both the `application.yml` file
and the profile-specific ones are gitignored, so you can safely add your own configuration without worrying about
accidentally committing it.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

Finally, for docker, you can build the image with:
```shell script
./gradlew imageBuild
```

## Creating a native executable

You can create a native executable using:
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/contestsubmission-backend-0.0.1-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.
