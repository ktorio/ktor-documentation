const express = require('express')
const path = require("path")
const app = express()
const port = 3000

const car = {type:"Fiat", model:"500", color:"white"};
app.get('/json', (req, res) => {
    res.json(car)
})

app.get('/file', (req, res) => {
    res.sendFile(path.join(__dirname, 'ktor_logo.png'))
})

app.get('/file-attachment', (req, res) => {
    res.download("ktor_logo.png")
})

app.get('/old', (req, res) => {
    res.redirect(301, "moved")
})

app.get('/moved', (req, res) => {
    res.send('Moved resource')
})

app.listen(port, () => {
    console.log(`Responding at http://0.0.0.0:${port}/`)
})
