# Upload a File

A sample Ktor project showing how to use the [receiveMultipart](https://ktor.io/docs/requests.html#form_data) function to upload a file sent as a part of a multipart request.


## Running
To upload a file, follow the steps below:
1. Open a terminal and run a sample:
   ```bash
   ./gradlew :upload-file:run
   ```
1. Open the [post](post.http) file and run a request using an [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html). As an alternative, you can send the same `POST` request using cURL:
   ```cURL
   curl -X POST --location "http://localhost:8080/post" \
    -H "Content-Type: multipart/form-data; boundary=WebAppBoundary" \
    -F "description=Ktor logo;type=text/plain" \
    -F "image=@/Users/jetbrains/IdeaProjects/ktor-documentation/codeSnippets/snippets/upload-file/ktor_logo.png;filename=ktor_logo.png;type=image/png"
   ```
   > Note that you might need to change a path to `ktor_logo.png` here.
1. Open the [uploads](uploads) folder and make sure `ktor_logo.png` is uploaded successfully.

