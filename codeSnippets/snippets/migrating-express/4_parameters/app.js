const express = require('express')
const app = express()
const querystring = require('querystring')
const port = 3000

app.get('/user/:login', (req, res) => {
    if (req.params['login'] === 'admin') {
        res.send('You are logged in as Admin')
    } else {
        res.send('You are logged in as Guest')
    }
})

app.get('/products', (req, res) => {
    if (req.query['price'] === 'asc') {
        res.send('Products from the lowest price to the highest')
    }
})

app.listen(port, () => {
    console.log(`Responding at http://0.0.0.0:${port}/`)
})
