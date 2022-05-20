# AWS Elastic Beanstalk

A sample Ktor project that can be deployed on [AWS Elastic Beanstalk](https://ktor.io/docs/elastic-beanstalk.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample locally, execute the following command in a repository's root directory:

```
./gradlew :aws-elastic-beanstalk:run
```
 
Then, navigate to [http://localhost:5000/](http://localhost:5000/) to see the sample home page.


## Deploying
To deploy this application to AWS Elastic Beanstalk, generate a fat JAR first with the following command:

```
./gradlew :aws-elastic-beanstalk:shadowJar
```

Then, deploy the generated `build/libs/aws-elastic-beanstalk-1.0-SNAPSHOT-all.jar` file as described in [Deploy an application](https://ktor.io/docs/elastic-beanstalk.html#deploy-app).