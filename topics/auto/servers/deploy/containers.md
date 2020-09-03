[//]: # (title: Containers)
[//]: # (caption: Containers)
[//]: # (category: servers)
[//]: # (permalink: /servers/deploy/containers.html)
[//]: # (ktor_version_review: 1.0.0)

## Docker
{ #docker}

Docker is a container engine: it allows you to pack and run applications, in a sandboxed layered
lightweight environment, with its own isolated filesystem, operating system, and resources.

You usually have to create a `Dockerfile` for monolithic services, and a `docker-compose.yml` 
when your container needs to interact with other services, like for example a database or a redis. 

First you have to create a [fat-jar file](/servers/deploy/packing/fatjar) with your application. And a `Dockerfile`, which looks like this:

{% capture docker-file %}{% include docker-sample.md %}{% endcapture %}

{% include tabbed-code.html
    tab1-title="Dockerfile" tab1-content=docker-file
    no-height="true"
%}

For deploying to Docker simply you can check out the [docker quickstart](/quickstart/quickstart/docker.html) page for full details.

### Nginx
{ #nginx}

When using Docker with multiple domains, you might want to use the 
[nginx-proxy](https://github.com/jwilder/nginx-proxy) image and the 
[letsencrypt-nginx-proxy-companion](https://github.com/JrCs/docker-letsencrypt-nginx-proxy-companion) image
to serve multiple domains/subdomains in a single machine/ip and to automatically provide HTTPS,
using let's encrypt.

After configuring the nginx-proxy and letsencrypt-nginx-proxy-companion, your docker-compose.yml file
(without additional services) might look like this:

{% capture docker-compose-yml %}
```yaml
version: '2'
services:
  web:
    build:
      context: ./
      dockerfile: Dockerfile
    expose:
      - 8080
    environment:
      - VIRTUAL_HOST=mydomain.com
      - VIRTUAL_PORT=8080
      - LETSENCRYPT_HOST=mydomain.com
      - LETSENCRYPT_EMAIL=myemail@mydomain.com
    networks:
      - reverse-proxy
    restart: always

networks:
  backend:
  reverse-proxy:
    external:
      name: reverse-proxy
```
{% endcapture %}

{% include tabbed-code.html
    tab1-title="docker-compose.yml" tab1-content=docker-compose-yml
    no-height="true"
%}

You can start it with `docker-compose up -d` and it will be restarted if the service fails or
after a system reboot.

If the DNS for the specified domain is pointing to your server and you have configured the nginx-proxy and its companion correctly,
the letsencrypt companion will contact with letsencrypt and will grab and configure the certificate automatically
for you. So you will be able to access your http-only service via: https://mydomain.com/ nginx will handle the SSL certificates
and will contact your server via plain HTTP.

## Tomcat
{ #tomcat}

You have to generate a [war file](/servers/deploy/packing/war) and put it in the Tomcat `webapps` folder.

For a complete example, check:
<https://github.com/ktorio/ktor-samples/tree/master/deployment/tomcat-war>
{ .note.example}

## Jetty
{ #jetty}

You have to generate a [war file](/servers/deploy/packing/war) and put it in the Jetty `webapps` folder.

For a complete example, check:
<https://github.com/ktorio/ktor-samples/tree/master/deployment/jetty-war>
{ .note.example}