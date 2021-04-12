# Upload a File

A sample Ktor project showing how to use the [receiveMultipart](https://ktor.io/docs/requests.html#form_data) function to upload a file sent as a part of a multipart request.


## Running
To upload a file, follow the steps below:
1. Open a terminal and run a sample:
   ```bash
   ./gradlew :upload-file:run
   ```
1. Open the [post](post.http) file and run a request using an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html).
1. Open the [uploads](uploads) folder and make sure `ktor_logo.png` is uploaded successfully.

