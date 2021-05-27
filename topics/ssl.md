[//]: # (title: SSL)

<microformat>
<var name="example_name" value="ssl"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<include src="lib.xml" include-id="outdated_warning"/>

You can buy a certificate and configure Ktor to use it,
**or** you can use Let's Encrypt to automatically get a **free certificate** to serve `https://` and `wss://` requests
with Ktor.
In this page you will discover how to do it, by either configuring Ktor to directly serve the SSL certificate
for a single domain or by using Docker with nginx to serve different applications in different containers on
a single machine easily.

## Option1: With Ktor serving SSL directly
{id="ktor"}

### Configuring an `A` register pointing to the machine

First, you have to configure your domain or subdomain to point to the IP of the machine that
you are going to use for the certificate. You have to put the public IP of the machine here.
If that machine is behind routers, you will need to configure the router to DMZ the machine with the host,
or to redirect at least the port 80 (HTTP) to that machine, and later you will probably want to configure the
port 443 (HTTPS) too.

>Let's Encrypt will always access the PORT 80 of your public IP, even if you configure Ktor to bind to another port,
>you have to configure your routes to redirect the port 80 to the correct local IP and port of the machine
>hosting ktor.
>
{type="note"}

### Generating a certificate

The Ktor server must not be running, and you have to execute the following command
(changing `my.example.com`, `root@example.com` and `8889`).

This command will start a HTTP web server in the specified port (that must be available as port 80 in the
public network, or you can forward ports in your router to 80:8889, and the domain must point to your public IP),
it will then request a challenge, expose the `/.well-known/acme-challenge/file` with the proper content, generate a 
domain private key, and retrieve the certificate chain:  

```text
export DOMAIN=my.example.com
export EMAIL=root@example.com
export PORT=8889
export ALIAS=myalias
certbot certonly -n -d $DOMAIN --email "$EMAIL" --agree-tos --standalone --preferred-challenges http --http-01-port $PORT
```


❌ Error output sample:

```text
Saving debug log to /var/log/letsencrypt/letsencrypt.log
Plugins selected: Authenticator standalone, Installer None
Obtaining a new certificate
Performing the following challenges:
http-01 challenge for my.example.com
Waiting for verification...
Cleaning up challenges
Failed authorization procedure. my.example.com (http-01): urn:acme:error:connection :: The server could not connect to the client to verify the domain :: Fetching http://my.example.com/.well-known/acme-challenge/j-BJXA5ZGXdJuZhTByL4B95XBpiaGjZsm8JdCcA3Vr4: Timeout during connect (likely firewall problem)

IMPORTANT NOTES:
 - The following errors were reported by the server:

   Domain: my.example.com
   Type:   connection
   Detail: Fetching
   http://my.example.com/.well-known/acme-challenge/j-BJXA5ZGXdJuZhTByL4B9zXBp3aGjZsm8JdCcA3Vr4:
   Timeout during connect (likely firewall problem)

   To fix these errors, please make sure that your domain name was
   entered correctly and the DNS A/AAAA record(s) for that domain
   contain(s) the right IP address. Additionally, please check that
   your computer has a publicly routable IP address and that no
   firewalls are preventing the server from communicating with the
   client. If you're using the webroot plugin, you should also verify
   that you are serving files from the webroot path you provided.
 - Your account credentials have been saved in your Certbot
   configuration directory at /etc/letsencrypt. You should make a
   secure backup of this folder now. This configuration directory will
   also contain certificates and private keys obtained by Certbot so
   making regular backups of this folder is ideal.
```
{ .error}


✅ Working output sample:

```text
Saving debug log to /var/log/letsencrypt/letsencrypt.log
Plugins selected: Authenticator standalone, Installer None
Obtaining a new certificate
Performing the following challenges:
http-01 challenge for my.example.com
Waiting for verification...
Cleaning up challenges

IMPORTANT NOTES:
 - Congratulations! Your certificate and chain have been saved at:
   /etc/letsencrypt/live/my.example.com/fullchain.pem
   Your key file has been saved at:
   /etc/letsencrypt/live/my.example.com/privkey.pem
   Your cert will expire on 2018-09-27. To obtain a new or tweaked
   version of this certificate in the future, simply run certbot
   again. To non-interactively renew *all* of your certificates, run
   "certbot renew"
 - If you like Certbot, please consider supporting our work by:

   Donating to ISRG / Let's Encrypt:   https://letsencrypt.org/donate
   Donating to EFF:                    https://eff.org/donate-le
```
{ .success}




### Converting the private key and certificate for Ktor

Now you have to convert the private key and certificates written by `certbot` to a format that Ktor understands.

The chain and private keys are stored in `/etc/letsencrypt/live/$DOMAIN` as `fullchain.pem` and `privkey.pem`.

```text
openssl pkcs12 -export -out /etc/letsencrypt/live/$DOMAIN/keystore.p12 -inkey /etc/letsencrypt/live/$DOMAIN/privkey.pem -in /etc/letsencrypt/live/$DOMAIN/fullchain.pem -name $ALIAS
```

This will request a password for the export (you need to provide one for the next step to work):

```text
Enter Export Password: mypassword
Verifying - Enter Export Password: mypassword
```

With th p12 file, we can use the `keytool` cli to generate a JKS file: 

```text
keytool -importkeystore -alias $ALIAS -destkeystore /etc/letsencrypt/live/$DOMAIN/keystore.jks -srcstoretype PKCS12 -srckeystore /etc/letsencrypt/live/$DOMAIN/keystore.p12
```

### Configuring Ktor to use the generated JKS

Now you have to update your `application.conf` HOCON file, to configure the SSL port, the keyStore, alias, and passwords.
You have to set the correct values for your specific case: 

```groovy
ktor {
    deployment {
        port = 8889
        port = ${?PORT}
        sslPort = 8890
        sslPort = ${?PORT_SSL}
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
    security {
        ssl {
            keyStore = /etc/letsencrypt/live/mydomain.com/keystore.jks
            keyAlias = myalias
            keyStorePassword = mypassword
            privateKeyPassword = mypassword
        }
    }
}
```

If everything went well, Ktor should be listening on port 8889 in HTTP and listening on port 8890 in HTTPS.

## Option2: With Docker and Nginx as reverse proxy
{id="docker"}

When using Docker with multiple domains, you might want to use the [nginx-proxy] image and the [letsencrypt-nginx-proxy-companion]
image to serve multiple domains/subdomains on a single machine/ip and to automatically provide HTTPS, using Let’s Encrypt.

In this case you create a container with NGINX, potentially listening to port `80` and `443`, an internal network
accessible only between containers so nginx can connect and reverse proxy your websites (including websockets),
and a NGINX companion handling the domain certificates by introspecting the configured Docker containers. 

[nginx-proxy]: https://github.com/jwilder/nginx-proxy
[letsencrypt-nginx-proxy-companion]: https://github.com/JrCs/docker-letsencrypt-nginx-proxy-companion

### Creating a internal docker network

The first step is to create a bridge network that we will use so nginx can connect to other containers
to reverse proxy a user's HTTP, HTTPS, WS, and WSS requests:

```bash
docker network create --driver bridge reverse-proxy
```

### Creating an Nginx container

Now we have to create a container running NGINX doing the reverse proxy:

```bash
docker rm -f nginx
docker run -d -p 80:80 -p 443:443 \
	--name=nginx \
	--restart=always \
	--network=reverse-proxy \
	-v /home/virtual/nginx/certs:/etc/nginx/certs:ro \
	-v /home/virtual/nginx/conf.d:/etc/nginx/conf.d \
	-v /home/virtual/nginx/vhost.d:/etc/nginx/vhost.d \
	-v /home/virtual/nginx/html:/usr/share/nginx/html \
	-v /var/run/docker.sock:/tmp/docker.sock:ro \
	-e NGINX_PROXY_CONTAINER=nginx \
	--label com.github.jrcs.letsencrypt_nginx_proxy_companion.nginx_proxy=true \
	jwilder/nginx-proxy
```

* `--restart=always` so the docker daemon restarts the container when the machine is restarted.
* `--network=reverse-proxy` so NGINX is in that network and can connect to other containers in the same network.
* `-v certs:ro` this volume will be shared with the letsencrypt-companion to access the certificates per domain.
* `-v conf, vhost` so this configuration is persistent and accessible from outside in the case you have to do some tweaks.
* `-v /var/run/docker.sock` this allows this image to get notified / introspect about new containers running in the daemon.
* `-e --label` used by the companion by identify this image as NGINX.

You can adjust `/home/virtual/nginx*` paths to the path you prefer.

### Creating a Nginx Let's Encrypt companion container

With the nginx-proxy container, now we can create a companion container,
that will request and renew certificates:

```bash
docker rm -f nginx-letsencrypt
docker run -d \
    --name nginx-letsencrypt \
    --restart=always \
    --network=reverse-proxy \
    --volumes-from nginx \
    -v /home/virtual/nginx/certs:/etc/nginx/certs:rw \
    -v /var/run/docker.sock:/var/run/docker.sock:ro \
    jrcs/letsencrypt-nginx-proxy-companion
```

* `--restart=always` as the NGINX image, to restart on boot.
* `--network=reverse-proxy` it need to be on the same network as the NGINX proxy container to communicate with it.
* `--volumes-from nginx` it makes accessible the same volumes as the NGINX container so it can write the `.well-known` challenge inside `/usr/share/nginx/html`.
* `-v certs:rw` it requires write access to write the private key and certificates to be available from NGINX.
* `-v /var/run/docker.sock` requires access to docker events and introspection to determine which certificates to request.

### Creating a service

Now we have NGINX and Let's Encrypt companion configured so they will automatically reverse-proxy your websites and
request and serve certificates for them based on the environment variables `VIRTUAL_HOST`, `VIRTUAL_PORT` and `LETSENCRYPT_HOST`, `LETSENCRYPT_EMAIL`.

Using docker-compose, you can create a `docker-compose.yml` file (without additional services) that could look like this:

#### `docker-compose.yml`

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
      name: reverse-proxyv
```

#### `Dockerfile`

You can find more information about [how to deploy a docker and the Dockerfile](docker.md).

### Simplified overview

```nomnoml
#direction: right
#.internet: fill=#fee
#.network: fill=#efe
#.http: fill=#6f6
#.ssl: fill=#6af

[<internet>Internet]

[<http>Nginx
|port=80 (HTTP, WS)
|port=443 (HTTPS and WSS)
|TLS certs per domain
|VIRTUAL_HOST
|VIRTUAL_PORT
]

[App
|[port=8080 HTTP & WS]
|[<http>VIRTUAL_HOST=myhost.com]
|[<http>VIRTUAL_PORT=8080]
|[<ssl>LETSENCRYPT_HOST=myhost.com]
|[<ssl>LETSENCRYPT_EMAIL=email@myhost.com]
]

[<ssl>Let's Encrypt companion
|LETSENCRYPT_HOST
|LETSENCRYPT_EMAIL]

[Docker
|port=80,443
]

[Let's Encrypt] <- cert request [Let's Encrypt companion]

[App] -:> [reverse-proxy]

[<network>reverse-proxy|network]
[Nginx] <- [reverse-proxy]

[Internet] <- port 80, 443[Docker]
[Docker] <- [Nginx]

[Let's Encrypt companion] <-> [Nginx]
```

### The XForwardedHeaderSupport plugin

In this case you are using nginx acting as reverse-proxy for your requests. If you want to get information about the original requests,
instead of the proxied nginx request, you will have to use the [XForwardedHeaderSupport](forward-headers.md) plugin.