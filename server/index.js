var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];
var stones = [];
var gifts = [];
var GAME_WIDTH = 500;
var GAME_HEIGHT = 500;


server.listen(9000, function() {
    console.log("Server is now running...");
});

io.on('connection', function(socket) {
if(!stones[0]){
    console.log("stones null");
   for (var i = 0; i < 10; i++) {
             stones.push(new stone(i, randomInt(0, GAME_HEIGHT), randomInt(0, GAME_WIDTH)));
   }
}
if(!gifts[0]){
    console.log("gifts null");
   for (var i = 0; i < 2; i++) {
             gifts.push(new gift(i, randomInt(0, GAME_HEIGHT), randomInt(0, GAME_WIDTH)));
   }
   socket.broadcast.emit('getGifts', gifts);
}
    console.log(socket.id + " Player Connected!");
    socket.emit('socketID', {
        id: socket.id
    });
    socket.emit('getPlayers', players);
    socket.emit('getStones', stones);
    socket.emit('getGifts', gifts);
    socket.on('playerMoved', function(data) {
        data.id = socket.id;
        socket.broadcast.emit('playerMoved', data);
        socket.emit('playerMoved', data);
        for (var i = 0; i < players.length; i++) {
            if (players[i].id == socket.id) {
                players[i].x = data.x;
                players[i].y = data.y;
            }
        }
    });
    socket.on('newPlayer', function(data) {
        console.log(socket.id + " newPlayer");
        players.push(new player(socket.id, data.x, data.y,data.playerName));
        data.id = socket.id
        socket.broadcast.emit('newPlayer', data);
        printPlayers();
    });
    socket.on('playerShoot', function(data) {
        data.id = socket.id;
        socket.broadcast.emit('playerShoot', data);
    socket.emit('playerShoot', data);
    });
       socket.on('connectionTest', function(data) {
        socket.emit('connectionTest', data);
    });
    socket.on('playerKilled', function(data) {
        console.log(socket.id + " Hit " + data.id);
        gifts.push(new gift(i, data.x, data.y));
        for (var i = 0; i < players.length; i++) {
            if (players[i].id == data.id) {
                socket.broadcast.emit('playerKilled', data);
                players.splice(i, 1);
            }
        }
        printPlayers();
    });
     socket.on('giftHit', function(data) {
        console.log(socket.id + "  giftHit " + data.id);
        for (var i = 0; i < gifts.length; i++) {
            if (gifts[i].id == data.id) {
                socket.broadcast.emit('giftHit', data);
                gifts.splice(i, 1);
            }
        }
        printGifts();
    });
    socket.on('disconnect', function() {
        console.log(socket.id + " Disconnected");
        socket.broadcast.emit('playerDisconnected', {
            id: socket.id
        });
        for (var i = 0; i < players.length; i++) {
            if (players[i].id == socket.id) {
                players.splice(i, 1);
            }
        }
        printPlayers();
    });

});

function player(id, x, y, playerName) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.playerName = playerName
}
function stone(id, x, y) {
    this.id = id;
    this.x = x;
    this.y = y;
}
function gift(id, x, y) {
    this.id = id;
    this.x = x;
    this.y = y;
}
function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min)) + min;
}
function printPlayers() {
    console.log("Players:");
    for (var i = 0; i < players.length; i++) {
        console.log("Player:" + players[i].id +" name -"+ players[i].playerName + "  x-" + players[i].x + "  y-" + players[i].y);
    }
}
function printGifts() {
    console.log("Gifts:");
    for (var i = 0; i < gifts.length; i++) {
        console.log("gift:" + gifts[i].id + "  x-" + gifts[i].x + "  y-" + gifts[i].y);
    }
}