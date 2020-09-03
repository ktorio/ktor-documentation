[//]: # (title: Generator)
[//]: # (caption: Generate a Ktor project)
[//]: # (category: quickstart)
[//]: # (permalink: /quickstart/generator.html)
[//]: # (skip_pdf: true)
[//]: # (redirect_from: redirect_from)
[//]: # (- /quickstart/quickstart/generator.html: - /quickstart/quickstart/generator.html)
[//]: # (ktor_version_review: 1.0.0)

<!--<https://ktor.io/start>-->

**NOTE: You can also use the [Ktor IntelliJ plugin](/quickstart/quickstart/intellij-idea.html) instead.** This page can also be accessed at [start.ktor.io](https://start.ktor.io/).

<div id="generator_id"></div>

<script type="text/javascript">
window.addEventListener('popstate', function(event) {
    const iframe = document.getElementById('iframe_generator');
    if (iframe) {
        iframe.contentWindow.postMessage({type: "updateHash", value: window.location.hash}, "*")
    }
});
window.addEventListener('message', function(event) {
    if (event.data && event.data.type === "updateHash") {
        history.pushState({}, "", window.location.pathname + "#" + event.data.value.replace(/^#/, ''));
    }
});
document.getElementById('generator_id').innerHTML = '<iframe id="iframe_generator" src="{{ site.ktor_init_tools_url }}' + location.hash.replace(/"/g, '\\"') + '" style="border:1px solid #343a40;width:100%;height:574px;"></iframe>';
</script>