var app = require('express')();
var server = require('http').createServer(app);
var webRTC = require('webrtc.io').listen(server);

var port = process.env.PORT;
server.listen(port);

console.log('Starting app on port ' + port);

app.get('/', function(req, res) {
  res.sendfile(__dirname + '/index.html');
});

app.get('/style.css', function(req, res) {
  res.sendfile(__dirname + '/style.css');
});

app.get('/webrtc.io.js', function(req, res) {
  res.sendfile(__dirname + '/webrtc.io.js');
});

app.get('/client.js', function(req, res) {
  res.sendfile(__dirname + '/client.js');
});

webRTC.rtc.on('connect', function(rtc) {
  //Client connected
  console.log('rtc connect');
});

webRTC.rtc.on('send answer', function(rtc) {
  //answer sent
  console.log('rtc send answer');
});

webRTC.rtc.on('disconnect', function(rtc) {
  //Client disconnect 
  console.log('rtc disconnect');
});

webRTC.rtc.on('chat_msg', function(data, socket) {
  var roomList = webRTC.rtc.rooms[data.room] || [];

  for (var i = 0; i < roomList.length; i++) {
    var socketId = roomList[i];

    if (socketId !== socket.id) {
      var soc = webRTC.rtc.getSocket(socketId);

      if (soc) {
        soc.send(JSON.stringify({
          "eventName": "receive_chat_msg",
          "data": {
            "messages": data.messages,
            "color": data.color
          }
        }), function(error) {
          if (error) {
            console.log(error);
          }
        });
      }
    }
  }
});
