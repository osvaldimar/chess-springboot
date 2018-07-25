var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
	var userId = $("#userId").val();
    var socket = new SockJS('/api/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/chessQueue/movements', function (greeting) {
            showMovements(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/user/chessQueue/selected', function (greeting) {
            showSelected(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/chessQueue/movements/' + userId, function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMovements() {
	var userId = $("#userId").val();
	var player = $("#player").val();
    stompClient.send("/appServerChess/move", {}, JSON.stringify({'id' : userId, 'player':player, 'content': $("#name").val()}));
}

function showMovements(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}
function showSelected(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}
function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMovements(); });
});