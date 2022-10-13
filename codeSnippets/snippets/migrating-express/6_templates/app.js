const express = require('express')
const app = express()
const port = 3000

app.set('views', './views')
app.set('view engine', 'pug')

app.get('/', (req, res) => {
    res.render('index', { title: 'Hey', message: 'Hello there!' })
})

app.listen(port, () => {
    console.log(`Responding at http://0.0.0.0:${port}/`)
})
