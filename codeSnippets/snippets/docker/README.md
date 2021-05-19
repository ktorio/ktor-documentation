# Docker

A sample Ktor project running as an application inside [Docker](https://www.docker.com/).

## Running

To build and run this application with Docker, execute the following commands:

```bash
./gradlew :docker:installDist
docker build -t my-application snippets/docker
docker run -p 8080:8080 my-application
```
 
Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.
