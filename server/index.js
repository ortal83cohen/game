var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];

server.listen(8080, function(){
	console.log("Server is now running...");
});

io.on('connection', function(socket){
	console.log("Player Connected!");
	socket.emit('socketID', { id: socket.id });
	socket.emit('getPlayers', players);
	socket.broadcast.emit('newPlayer', { id: socket.id });
	socket.on('playerMoved', function(data){
    		data.id = socket.id;
    		socket.broadcast.emit('playerMoved', data);
    		for(var i = 0; i < players.length; i++){
            			if(players[i].id == socket.id){
            				players[i].x =data.x;
            				players[i].y =data.y;
            			}
            		}
    	});
    	socket.on('playerSHoot', function(data){
    		data.id = socket.id;
    		socket.broadcast.emit('playerSHoot', data);

    	});
    	socket.on('playerHit', function(data){
                console.log("Player Hit");

                for(var i = 0; i < players.length; i++){
        			if(players[i].id == data.id){
        			 	socket.broadcast.emit('playerHit', data);
        				players.splice(data.id, 1);
        			}
        		}
            	});
	socket.on('disconnect', function(){
		console.log("Player Disconnected");
		socket.broadcast.emit('playerDisconnected', { id: socket.id });
		for(var i = 0; i < players.length; i++){
			if(players[i].id == socket.id){
				players.splice(i, 1);
			}
		}
	});
	players.push(new player(socket.id, 0, 0));
});

function player(id, x, y){
	this.id = id;
	this.x = x;
	this.y = y;
}