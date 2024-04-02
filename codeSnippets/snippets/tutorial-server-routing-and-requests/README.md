# Task Application

A Task Application built with Ktor by following the steps explained in
the [Handle requests and generate responses](https://ktor.io/docs/handle-requests-and-generate-responses.html) tutorial.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Run

To run the application, execute the following command in the repository's root directory:

```bash
./gradlew :tutorial-server-routing-and-requests:run
```
Then, you can navigate to the following URLs:
- [http://0.0.0.0:8080/task-ui/task-form.html](http://0.0.0.0:8080/task-ui/task-form.html) to add a new task.
- [http://0.0.0.0:8080/tasks](http://0.0.0.0:8080/tasks) to see all tasks displayed in a table.
- [http://0.0.0.0:8080/tasks/byPriority/Medium](http://0.0.0.0:8080/tasks/byPriority/Medium) to see only those tasks
  that have a "Medium" priority. Replace `Medium` in the URL with another value to get different results.
- [http://0.0.0.0:8080/tasks/byName/cleaning](http://0.0.0.0:8080/tasks/byName/cleaning) to display only the task that
  has a name "cleaning". Change `cleaning` in the URL with another value to filter the results by name.