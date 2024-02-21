# A RESTful API that generates JSON

A sample project created in
the [Create RESTful APIs that generate JSON](https://ktor.io/docs/create-restful-apis.html)
tutorial.

> This sample is part of the [codeSnippets](../../README.md) Gradle project.

## Run

To run the sample, execute the following command in a repository's root directory:

```bash
./gradlew :tutorial-server-restful-api:run
```

Navigate to the following URLs:

- [http://0.0.0.0:8080/tasks](http://0.0.0.0:8080/tasks) to see a JSON output of all tasks.
- [http://0.0.0.0:8080/tasks/byPriority/Medium](http://0.0.0.0:8080/tasks/byPriority/Medium) to filter tasks by "Medium"
  priority.
- [http://0.0.0.0:8080/static/index.html](http://0.0.0.0:8080/static/index.html) to view the tasks in HTML via
  JavaScript

You can test the endpoints by sending HTTP requests defined in the **REST Task Manager.http** file.