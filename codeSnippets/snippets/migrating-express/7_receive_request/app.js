const express = require('express')
const bodyParser = require('body-parser')
const fs = require('fs')
const multer = require('multer')
const app = express()
const port = 3000

app.post('/text', bodyParser.text(), (req, res) => {
    let text = req.body
    res.send(text)
})

app.post('/json', bodyParser.json(), (req, res) => {
    let car = req.body
    res.send(car)
})

app.post('/urlencoded', bodyParser.urlencoded({extended: true}), (req, res) => {
    let user = req.body
    res.send(`The ${user["username"]} account is created`)
})

app.post('/raw', bodyParser.raw({type: () => true}), (req, res) => {
    let rawBody = req.body
    fs.createWriteStream('./uploads/ktor_logo.png').write(rawBody)
    res.send('A file is uploaded')
})

const storage = multer.diskStorage({
    destination: './uploads/',
    filename: function (req, file, cb) {
        cb(null, file.originalname);
    }
})
const upload = multer({storage: storage});
app.post('/multipart', upload.single('image'), function (req, res, next) {
    let fileDescription = req.body["description"]
    let fileName = req.file.filename
    res.send(`${fileDescription} is uploaded to uploads/${fileName}`)
})

app.listen(port, () => {
    console.log(`Responding at http://0.0.0.0:${port}/`)
})
