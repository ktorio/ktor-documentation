const express = require('express')
const app = express()
const birds = require('./birds')
const port = 3000

// 1. Route methods
app.get('/', (req, res) => {
    res.send('GET request to the homepage')
})

app.post('/', (req, res) => {
    res.send('POST request to the homepage')
})

// 2. Group routes by paths
app.route('/book')
    .get((req, res) => {
        res.send('Get a random book')
    })
    .post((req, res) => {
        res.send('Add a book')
    })
    .put((req, res) => {
        res.send('Update the book')
    })

// 3. Group routes by file
app.use('/birds', birds)

app.listen(port, () => {
    console.log(`Responding at http://0.0.0.0:${port}/`)
})
