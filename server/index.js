var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];

server.listen(9000, function() {
    console.log("Server is now running...");
});

io.on('connection', function(socket) {
    console.log(socket.id + " Player Connected!");
    socket.emit('socketID', {
        id: socket.id
    });
    socket.emit('getPlayers', players);
    socket.on('playerMoved', function(data) {
        data.id = socket.id;
        socket.broadcast.emit('playerMoved', data);
        for (var i = 0; i < players.length; i++) {
            if (players[i].id == socket.id) {
                players[i].x = data.x;
                players[i].y = data.y;
            }
        }
    });
    socket.on('newPlayer', function(data) {
        console.log(socket.id + " newPlayer");
        players.push(new player(socket.id, data.x, data.y));
        data.id = socket.id
        socket.broadcast.emit('newPlayer', data);
        printPlayers();
    });
    socket.on('playerShoot', function(data) {
        data.id = socket.id;
        socket.broadcast.emit('playerShoot', data);

    });
    socket.on('playerHit', function(data) {
        console.log(socket.id + " Hit " + data.id);

        for (var i = 0; i < players.length; i++) {
            if (players[i].id == data.id) {
                socket.broadcast.emit('playerHit', data);
                players.splice(i, 1);
            }
        }
        printPlayers();
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

function player(id, x, y) {
    this.id = id;
    this.x = x;
    this.y = y;
}

function printPlayers() {
    console.log("Players:");
    for (var i = 0; i < players.length; i++) {
        console.log("Player:" + players[i].id + "  x-" + players[i].x + "  y-" + players[i].y);
    }
}