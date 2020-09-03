# github-embed bundle

This branch contains a minified bundle of [github-embed](https://github.com/finom/github-embed).

[Demo](http://finom.github.io/github-embed/demo.html)

```html
<!-- babel-polyfill is required -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/babel-polyfill/6.23.0/polyfill.min.js"></script>
<script src="github-embed.min.js"></script>
```

```html
<!-- do not do this on production -->
<script src="https://finom.github.io/github-embed/github-embed.min.js"></script>
```

```html
<script>
  githubEmbed('.element', settings);
</script>
```