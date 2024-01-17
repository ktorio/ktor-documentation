# RSA Keys Generation

[RSA](https://en.wikipedia.org/wiki/RSA_(cryptosystem)) is a widely used public-key crypto-system that enables secure data transmission, digital signatures, and key exchange.

RS256, part of the RSA (Rivest–Shamir–Adleman) encryption algorithm, utilizes SHA-256 for hashing and a key (usually 2048-bit, 4096-bit or higher) to secure digital communications. 

In the realm of [JSON Web Token](https://jwt.io/) authentication, RS256 plays a crucial role since the integrity and authenticity of JWTs can be verified through signature mechanisms, such as RS256, where a public/private key pair is employed. This ensures that the information contained within the token remains tamper-proof and trustworthy.

The following sections will cover how such keys are generated and used alongside the [JWT Ktor authentication](jwt.md) plugin.

You can follow along by using the [auth-jwt-rs256](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/auth-jwt-rs256/) sample project's private key,
found in `resources/application.conf`. Simply save the private key in a `.pk8` file and [skip to the second](#second-step) step.

<tip>
<p>
For production use, it is recommended that you choose a more modern alternative, such as <a href="https://en.wikipedia.org/wiki/Elliptic_Curve_Digital_Signature_Algorithm">ES256</a>, that's based on more modern, efficient, and secure cryptography. However, because RSA still has its uses and is supported by the JWT plugin, the documentation below uses RSA.
</p>
</tip>

## Generating an RSA private key

To generate a private key, you can use `OpenSSL`, `ssh-keygen` or any other valid utility. For the rest of this page, OpenSSL will be used.

<tabs>
<tab title="OpenSSL">
<code-block lang="shell">
openssl genpkey -algorithm rsa -pkeyopt rsa_keygen_bits:2048 &gt; ktor.pk8
</code-block>
</tab>
</tabs>

The [openssl genpkey](https://www.openssl.org/docs/man3.0/man1/openssl-genpkey.html) command generates a private 2048-bit key using the RSA algorithm and stores it in the specified file, here `ktor.pk8`. The content of the file is [Base64](https://en.wikipedia.org/wiki/Base64) encoded and thus, needs to be decoded before the public key can be derived.

## Deriving the public key {id="second-step"}

In order to derive the public key from your previously generated private key, you will need to perform
the following steps:

1) Decode the private key
2) Extract the public key
3) Save the public key in PEM format

This will give you the private key in `ktor.pk8`, and the public key in `ktor.spki`.

Let's see how to do this using OpenSSL:

<tabs>
<tab title="OpenSSL">
<code-block lang="shell">
base64 -d &lt; ktor.pk8 | openssl pkey -inform der -pubout | tee ktor.spki
</code-block>

* `base64 -d < ktor.pk8`: This command decodes the Base64-encoded content of the `.pk8` file specified. The -d option is used for decoding.
* `|`: Pipes the decoded output to the next command.
* `openssl pkey -inform der -pubout`: This command reads the decoded content as [DER](https://en.wikipedia.org/wiki/X.690#DER_encoding) (Distinguished Encoding Rules) format, extracts the public key, and outputs it in [PEM](https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail) (Privacy Enhanced Mail) format. The `-inform der` specifies the input format as DER, and `-pubout` indicates that the public key should be output.
* `tee ktor.spki`: The `tee` command is used to redirect the output to both the console and a file named `ktor.spki`. This file will contain the public key in PEM format.
</tab>
</tabs>

With the public key at hand, you can now derive its exponent and modulus values.

## Extracting the modulus & exponent attributes

Now that you have your key-pair, you need to extract the `e` (exponent) and `n` (modulus) attributes of your
public key, in order to use them in your `jwks.json` file. To do this using OpenSSL, you need to:

1) Read the public key from the `.spki` file you created
2) Display information about the key in a human-readable format

This can easily be done with the following command:

<tabs>
<tab title="OpenSSL">
<code-block lang="shell">
openssl pkey -in ktor.spki -pubin -noout -text
</code-block>

* `pkey`: This is the OpenSSL command-line utility for processing private and public keys.
* `-in ktor.spki`: Specifies the input file containing the public key in PEM format. In this case, the input file is ktor.spki.
* `-pubin`: Indicates that the input file contains a public key. Without this option, OpenSSL would assume that the input file contains a private key.
* `-noout`: This option prevents OpenSSL from outputting the encoded public key. The command will only display information about the public key, and the actual key won't be printed to the console.
* `-text`: Requests that OpenSSL display the textual representation of the key. This includes details such as the key type, size, and the actual key data in human-readable form.
</tab>
</tabs>

Let's see the sample output by using the key present in the official ktor [auth-jwt-rs256 sample](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-jwt-rs256).
You can find the private key in the [applciation.conf](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/auth-jwt-rs256/src/main/resources/application.conf) file.

```Shell
$ openssl pkey -in ktor.spki -pubin -noout -text
RSA Public-Key: (512 bit)
Modulus:
    00:b5:f2:5a:2e:bc:d7:20:b5:20:d5:4d:cd:d4:a5:
    7c:c8:9a:fd:d8:61:e7:e4:eb:58:65:1e:ea:5a:4d:
    4c:73:87:32:e0:91:a3:92:56:2e:a7:bc:1e:32:30:
    43:f5:fd:db:05:5a:08:b2:25:15:5f:ac:4d:71:82:
    2b:d0:87:b4:01
Exponent: 65537 (0x10001)
```
<warning>
<p>
The sample key uses 512-bit, which is not secure. We are using that only as an example! Ideally, you should
opt for 2048-bit or 4096-bit keys.
</p>
</warning>

You now have the `n` and `e` attributes needed for your `jwks.json` file, but they are in hexadecimal format. Let's
see how to convert them to decimal and encode them properly.

##  Converting and encoding the modulus & exponent attributes

We now need to convert the hexadecimal representation of the exponent and modulus to their respective [Base64URL](https://en.wikipedia.org/wiki/Base64#URL_applications) encodings.

### Exponent

Let us begin with the exponent attribute which has a hex value of `0x10001`. 

<tabs>
<tab title="OpenSSL">
<code-block lang="shell">
echo 010001 | xxd -p -r | base64 
</code-block>

* `echo 010001`: This part of the command uses the `echo` command to output the string "010001", which represents the public exponent (e) of the RSA key, to the standard output.
* `|`: The `|` character is a pipe that takes the output from the preceding command and passes it as input to the following command.
* `xxd -p -r`: This command is used to convert hexadecimal to binary. It takes the hexadecimal input and produces the corresponding binary output.
* `| base64`: This part of the command takes the binary output from the previous step and encodes it in Base64 format using the `base64` command.

<note>
<p>
Note that an even number of hex digits was used by padding an extra 0 to the left.
</p>
</note>
</tab>
</tabs>

Let's see the output of this command for the aforementioned exponent value:

```Shell
$ echo 010001 | xxd -p -r | base64
AQAB
```

The Base64URL encoded value of the exponent is `AQAB` and does not require further processing for this case. In other cases, you may need to use the `tr` command as you will see in the next step.

### Modulus

Let's perform the same process now, for the `n` attribute. This time, you will use the `tr` utility to further process the hexadecimal representation of the modulus.

<tabs>
<tab title="OpenSSL">
<code-block lang="shell">
echo "b5:f2:5a:2e:bc:d7:20:b5:20:d5:4d:cd:d4:a5:
    7c:c8:9a:fd:d8:61:e7:e4:eb:58:65:1e:ea:5a:4d:
    4c:73:87:32:e0:91:a3:92:56:2e:a7:bc:1e:32:30:
    43:f5:fd:db:05:5a:08:b2:25:15:5f:ac:4d:71:82:
    2b:d0:87:b4:01" | tr -d ": \n" | xxd -p -r | base64 | tr +/ -_ | tr -d "=\n"
</code-block>

<note>
<p>
Note that the leading 00 byte has been omitted. The leading 00 byte in the modulus is related to the ASN.1 encoding of the RSA public key. In the ASN.1 DER encoding of integers, the leading zero byte is removed if the most significant bit of the integer is 0. This is a standard part of ASN.1 encoding rules.
In the context of RSA public keys, the modulus is a big-endian integer, and when represented in DER encoding, it follows these rules. The removal of the leading zero byte is done to ensure that the integer is interpreted correctly according to the DER rules.
</p>
</note>

* `echo "b5:f2:5a:2e:bc:d7:20:b5:20:d5:4d:cd:d4:a5: \ ... "`: This part of the command echoes a multi-line hexadecimal string, representing a series of bytes. The backslashes at the end of each line indicate line continuation.
* `tr -d ": \n"`: The `tr` command is used to delete characters specified in the argument list.
Here, it removes colons, spaces, and newline characters from the hexadecimal string, making it a continuous string of hex digits.
* `xxd -p -r`: `xxd` is a utility for creating a hex dump of a binary file or converting a hex dump back to binary.
The `-p` option specifies plain hexdump without line number or ASCII character columns.
The `-r` option reverses the operation, converting hex back to binary.
* `base64`: Encodes the binary output from the previous step into Base64 format.
* `tr +/ -_`: Translates the + and / characters in the Base64 output to - and _, respectively. This is a common modification for URL-safe Base64 encoding.
* `tr -d "=\n"`: Removes any equal signs (=) and newline characters from the final Base64-encoded string.
</tab>
</tabs>

The output of the above command is:

```Shell
$ echo "b5:f2:5a:2e:bc:d7:20:b5:20:d5:4d:cd:d4:a5:
    7c:c8:9a:fd:d8:61:e7:e4:eb:58:65:1e:ea:5a:4d:
    4c:73:87:32:e0:91:a3:92:56:2e:a7:bc:1e:32:30:
    43:f5:fd:db:05:5a:08:b2:25:15:5f:ac:4d:71:82:
    2b:d0:87:b4:01" | tr -d ": \n" | xxd -p -r | base64 | tr +/ -_ | tr -d "=\n"
tfJaLrzXILUg1U3N1KV8yJr92GHn5OtYZR7qWk1Mc4cy4JGjklYup7weMjBD9f3bBVoIsiUVX6xNcYIr0Ie0AQ
```

By leveraging the `tr` command properly, the modulus field has been encoded into a Base64URL string that you can use in your `jwks.json` file.

## Populating the jwks.json file

In the previous steps, you gathered the following necessary information:

1) An RSA key-pair
2) The modulus of the RSA public key in Base64URL format
3) The exponent of the RSA public key in Base64URL format

With these at hand, you can now populate the [jwks.json](https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-key-sets) file of your ktor project. 
Simply specify the `e` and `n` values with the Base64URL encoded values you produced earlier respectively, specify a key id (in this case, you will be using the `kid` from
the sample project, but you can use any string you like) and the `kty` attribute as `RSA`:

```json
{
  "keys": [
    {
      "kty": "RSA",
      "e": "AQAB",
      "kid": "6f8856ed-9189-488f-9011-0ff4b6c08edc",
      "n":"tfJaLrzXILUg1U3N1KV8yJr92GHn5OtYZR7qWk1Mc4cy4JGjklYup7weMjBD9f3bBVoIsiUVX6xNcYIr0Ie0AQ"
    }
  ]
}
```

All that's left now, is to specify your private key so that your Ktor project can use it for authentication.

## Defining the private key

With your public key information set up, the last step is to provide your Ktor project with access to your private key.

Assuming that you have extracted your private key (that you generated at the beginning in your `.pk8` file) into an environment variable on your system, called `jwt_pk` in this case, your `resources/application.conf` file's jwt section should look similar to:

```
jwt {
  privateKey = ${jwt_pk}
  issuer = "http://0.0.0.0:8080/"
  audience = "http://0.0.0.0:8080/login"
  realm = "MyProject"
}
```

<tip>
<p>
Your private key is considered sensitive information and should not be stored directly in code. Consider using environment variables or a <a href="https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html">secret store</a> for sensitive data.
</p>
</tip>