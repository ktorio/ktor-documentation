[//]: # (title: WebRTC client)

<show-structure for="chapter" depth="2"/>
<primary-label ref="experimental"/>

<var name="artifact_name" value="ktor-client-webrtc"/>
<tldr>
    <p>
        <b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
    </p>
    <p>
        <b>Supported platforms</b>: JS/Wasm, Android
    </p>   
    <p>
        <b>Code example</b>: <a href="https://github.com/ktorio/ktor-chat/">ktor-chat</a>
    </p>
</tldr>
<link-summary>
    The WebRTC client enables real-time peer-to-peer communication in multiplatform projects.
</link-summary>

Web Real-Time Communication (WebRTC) is a set of standards and APIs for real-time, peer-to-peer communication in
browsers and native apps.

The WebRTC client in Ktor enables real-time peer-to-peer communication in multiplatform projects. With WebRTC, you can
build features such as:

- Video and voice calls
- Multiplayer games
- Collaborative applications (whiteboards, editors, etc.)
- Low-latency data exchange between clients

## Add dependencies {id="add-dependencies"}

To use `WebRtclient`, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Create a client

When creating a `WebRtcClient`, choose an engine based on your target platform:

- JS/Wasm: `JsWebRtc` – uses browser `RTCPeerConnection` and media devices.
- Android: `AndroidWebRtc` – uses `PeerConnectionFactory` and Android media APIs.

You can then provide platform-specific configuration similar to `HttpClient`. STUN/TURN servers are required for
[ICE](#ice) to work correctly. You can use existing solutions such as [coturn](https://github.com/coturn/coturn):

<tabs group="platform" id="create-webrtc-client">
<tab title="JS/Wasm" group-key="js-wasm">

```kotlin
val jsClient = WebRtcClient(JsWebRtc) {
    defaultConnectionConfig = {
        iceServers = listOf(WebRtc.IceServer("stun:stun.l.google.com:19302"))
    }
}
```

</tab>
<tab title="Android" group-key="android">

```kotlin
val androidClient = WebRtcClient(AndroidWebRtc) {
    context = appContext // Required: provide Android context
    defaultConnectionConfig = {
        iceServers = listOf(WebRtc.IceServer("stun:stun.l.google.com:19302"))
    }
}
```

</tab>
</tabs>

## Create a connection and negotiate SDP

After creating a `WebRtcClient`, the next step is to create a peer connection.
A peer connection is the core object that manages the real-time communication between two clients.

To establish a connection, WebRTC uses the Session Description Protocol (SDP). This involves three steps:

1. One peer (the caller) creates an offer.
2. The other peer (the callee) responds with an answer.
3. Both peers apply each other’s descriptions to complete the setup.

```kotlin
// Caller creates a connection and an offer
val caller = jsClient.createPeerConnection()
val offer = caller.createOffer()
caller.setLocalDescription(offer)
// send offer.sdp to the remote peer via your signaling mechanism

// Callee receives the offer and creates an answer
val callee = jsClient.createPeerConnection()
callee.setRemoteDescription(
    WebRtc.SessionDescription(WebRtc.SessionDescriptionType.OFFER, remoteOfferSdp)
)
val answer = callee.createAnswer()
callee.setLocalDescription(answer)
// send answer.sdp back to the caller via signaling

// Caller applies the answer
caller.setRemoteDescription(
    WebRtc.SessionDescription(WebRtc.SessionDescriptionType.ANSWER, remoteAnswerSdp)
)
```

## Exchange ICE candidates {id="ice"}

Once SDP negotiation is complete, peers still need to discover how to connect across networks. [Interactive Connectivity
Establishment (ICE)](https://en.wikipedia.org/wiki/Interactive_Connectivity_Establishment) allows peers to find network
paths to each other.

- Each peer gathers its own ICE candidates.
- These candidates must be sent to the other peer through your chosen signaling channel.
- Once both peers add each other’s candidates, the connection can succeed.

```kotlin
// Collect and send local candidates
scope.launch {
    caller.iceCandidates.collect { candidate ->
        // send candidate.candidate, candidate.sdpMid, candidate.sdpMLineIndex to remote peer
    }
}

// Receive and add remote candidates
callee.addIceCandidate(WebRtc.IceCandidate(candidateString, sdpMid, sdpMLineIndex))

// Optionally wait until all candidates are gathered
callee.awaitIceGatheringComplete()
```

> Ktor does not provide signaling. Use WebSockets, HTTP, or another transport to exchange offers, answers, and
> ICE candidates.
> 
{style="note"}

## Use a data channel

WebRTC supports data channels, which let peers exchange arbitrary messages. This is useful for chat, multiplayer games,
collaborative tools, or any low-latency messaging between clients.

### Creating a channel

To create a channel on one side, use the `.createDataChannel()` method:

```kotlin
val channel = caller.createDataChannel("chat")
```

You can then listen for data channel events on the other side:

```kotlin
scope.launch {
    callee.dataChannelEvents.collect { event ->
        when (event) {
            is DataChannelEvent.Open -> println("Channel opened: ${event.channel}")
            is DataChannelEvent.Closed -> println("Channel closed")
            else -> {}
        }
    }
}
```

### Sending and receiving messages

Channels use a `Channel`-like API, familiar to Kotlin developers:

```kotlin
// Send a message
scope.launch { channel.send("hello") }

// Receive messages
scope.launch { println("received: " + channel.receiveText()) }
```

## Add and observe media tracks

In addition to data channels, WebRTC supports media tracks for audio and video. This allows you to build applications
such as video calls or screen sharing.

### Creating local tracks

You can request audio or video tracks from local devices (microphone, camera):

```kotlin
val audioConstraints = WebRtcMedia.AudioTrackConstraints(
  echoCancellation = true
)
val videoConstraints = WebRtcMedia.VideoTrackConstraints(
  width = 1280,
  height = 720
)
val audio = rtcClient.createAudioTrack(audioConstraints)
val video = rtcClient.createVideoTrack(videoConstraints)

val pc = jsClient.createPeerConnection()
pc.addTrack(audio)
pc.addTrack(video)
```

On the web, this uses `navigator.mediaDevices.getUserMedia`. On Android, it uses the Camera2 API and you must request
microphone/camera permissions manually.

### Receiving remote tracks

You can also listen for remote media tracks:

```kotlin
scope.launch {
    pc.trackEvents.collect { event ->
        when (event) {
            is TrackEvent.Add -> println("Remote track added: ${event.track.id}")
            is TrackEvent.Remove -> println("Remote track removed: ${event.track.id}")
        }
    }
}
```

## Limitations

The WebRTC client is experimental and has the following limitations:

- Signaling is not included. You need to implement your own signaling (for example, with WebSockets or HTTP).
- Supported platforms are JavaScript/Wasm and Android. iOS, JVM desktop, and Kotlin/Native support are planned in future
  releases.
- Permissions must be handled by your application. Browsers prompt users for microphone and camera access, while
  Android requires runtime permission requests.
- Only basic audio and video tracks are supported. Screen sharing, device selection, simulcast, and advanced RTP
  features are not yet available.
- Connection statistics are available but differ across platforms and do not follow a unified schema.