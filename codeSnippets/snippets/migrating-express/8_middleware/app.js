const express = require('express')
const app = express()
const port = 3000

const requestLogging = function (req, res, next) {
    let scheme = req.protocol
    let host = req.headers.host
    let url = req.url
    console.log(`Request URL: ${scheme}://${host}${url}`)
    next()
}

app.use(requestLogging)

app.get('/', (req, res) => {
    res.send('Hello World!')
})

app.get('/about', (req, res) => {
    res.send('About page')
})

app.listen(port, () => {
    console.log(`Responding at http://0.0.0.0:${port}/`)
})
