# Docker

A sample Ktor project running as an application inside [Docker](https://www.docker.com/).

## Running

To run this sample without Docker, execute the following command in a repository's root directory:

```bash
./gradlew :docker:run
```

To build and run this application with Docker, execute the following commands:

```bash
./gradlew :docker:build
docker build -t ktor-docker-sample-application snippets/docker
docker run -m512M --cpus 2 -it -p 8080:8080 --rm ktor-docker-sample-application
```
 
Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page. 

