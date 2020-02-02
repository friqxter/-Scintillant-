
var express = require('express');
var app = express();
var bodyParser = require('body-parser');
const dateTime = require('date-time');
var MongoClient = require('mongodb').MongoClient;
var url = "mongodb://localhost:27017/";
var time = dateTime();
// var reload = require('express-reload');
// var path = __dirname + '/app.js'

app.get('/', function (req, res) {
res.send("Hello from Server");
})
app.use(bodyParser.urlencoded({ extended: false }));
// app.use(reload(path));
app.post('/', function(req, res) {
res.send('Got the temp data, thanks..!!');
console.log(JSON.stringify(req.body)+" "+time);
MongoClient.connect(url, { useUnifiedTopology: true },function(err, db) {
 if (err) throw err;
 var dbo = db.db("smartIndiaHackathon");
 var myobj = req.body;
 dbo.collection("soldiersInfo").insertOne(myobj, function(err, res) {
   if (err) throw err;
  
   db.close();
 });
});});
var server = app.listen(8018, function () {
var host = server.address().address;
var port = server.address().port;
console.log("Example server listening at localhost:%s", host, port) });
