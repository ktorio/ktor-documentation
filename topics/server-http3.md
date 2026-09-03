[//]: # (title: HTTP/3)

<show-structure for="chapter" depth="2"/>
<primary-label ref="experimental"/>

<tldr>
<var name="example_name" value="http3-netty"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to enable and configure experimental HTTP/3 support with the Netty server engine in Ktor.
</link-summary>

[HTTP/3](https://www.rfc-editor.org/rfc/rfc9114.html) is an HTTP protocol that runs over [QUIC](https://www.rfc-editor.org/rfc/rfc9000.html)
instead of TCP.

Ktor provides experimental HTTP/3 support with the [Netty server engine](server-engines.md#).

> HTTP/3 support uses an experimental Ktor API. To use it, opt in to `ExperimentalKtorApi`.
> 
{style="note"}

## Enable HTTP/3 {id="ssl"}

HTTP/3 always uses TLS, so you need to configure at least one [SSL connector](server-ssl.md).

To enable HTTP/3, call the `enableHttp3()` function in the Netty engine configuration:

```kotlin
```
{src="snippets/http3-netty/src/main/kotlin/com/example/Application.kt" include-lines="14-33,35,48-52"}

For each SSL connector, Ktor binds an HTTP/3 endpoint to the same host and port over UDP. HTTP/1.1 and HTTP/2 continue
to use TCP on that port.

> For more information about configuring certificates and SSL connectors, see [](server-ssl.md).
> 
{style="tip"}

## Configure HTTP/3 {id="enable"}

To customize HTTP/3 and QUIC behavior, use the available options inside the `enableHttp3()` configuration block:

```kotlin
```
{src="snippets/http3-netty/src/main/kotlin/com/example/Application.kt" include-lines="37-47"}

The following options are available:

<deflist type="full">
<def>
<title><code>quicTokenHandler</code></title>

Defaults to `null`.

Specifies a `QuicTokenHandler` for QUIC address validation. Use `HmacQuicTokenHandler` to enable HMAC-based Retry tokens.
</def>
<def>
<title><code>quicMaxIdleTimeout</code></title>

Defaults to `30.seconds`.

Specifies how long a QUIC connection can remain idle before it is closed. Must be greater than `0`.
</def>
<def>
<title><code>quicInitialMaxData</code></title>

Defaults to `10_000_000`.

Specifies the initial value of the connection's maximum data limit. Must be greater than `0`.
</def>
<def>
<title><code>quicInitialMaxStreamDataBidirectionalLocal</code></title>

Defaults to `1_000_000`.

Specifies the initial flow-control limit for locally initiated bidirectional streams. Must be greater than `0`.
</def>
<def>
<title><code>quicInitialMaxStreamDataBidirectionalRemote</code></title>

Defaults to `1_000_000`.

Specifies the initial flow-control limit for remotely initiated bidirectional streams. Must be greater than `0`.
</def>
<def>
<title><code>quicInitialMaxStreamsBidirectional</code></title>

Defaults to `100`.

Specifies the initial number of bidirectional streams that can be opened. Must be greater than `0`.
</def>
<def>
<title><code>udpSocketCount</code></title>

Defaults to `1`.

Specifies the number of UDP sockets for the HTTP/3 endpoint. Values greater than `1` require platform support for `SO_REUSEPORT`.
</def>
<def>
<title><code>udpReceiveBufferSize</code></title>

Defaults to `0`.

Specifies the UDP receive buffer (`SO_RCVBUF`) size in bytes. `0` uses the operating system default.
</def>
<def>
<title><code>udpSendBufferSize</code></title>

Defaults to `0`.

Specifies the UDP send buffer (`SO_SNDBUF`) size in bytes. `0` uses the operating system default.
</def>
</deflist>

These options apply only to HTTP/3 connections and don't affect HTTP/1.1 or HTTP/2.

## Configure the QUIC server codec {id="quic-server-codec"}

For advanced Netty configuration, use the `configureQuicServerCodec` option to customize the underlying
`QuicServerCodecBuilder`:

```kotlin
enableHttp3 {
    configureQuicServerCodec = {
        // Configure the Netty QUIC server codec.
    }
}
```

Use this option only when you need low-level QUIC transport configuration.