'use strict';

var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var chatName = document.querySelector('#chatName');

var chatForm =  document.querySelector('#myTabs')

var stompClient = null;
var username = null;



function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    if (!stompClient) {
        username = document.querySelector('#name').textContent.trim();
        if (username) {
            var socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, onConnected, onError);
        }
        event.preventDefault();
    }

    var messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        var chatMessage = {
            author: username,
            text: messageInput.value,
            room: chatName.textContent.trim(),
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('dd');

    messageElement.classList.add('chat-message');

    var usernameElement = document.createElement('dt');
    var usernameText = document.createTextNode(message.author);
    usernameElement.appendChild(usernameText);
    messageElement.appendChild(usernameElement);


    var textElement = document.createElement('dd');
    var messageText = document.createTextNode(message.text);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
}

function func(event) {
    stompClient.disconnect();
    stompClient = false;
}

messageForm.addEventListener('submit', sendMessage, true);

chatForm.addEventListener('click', func, true)
document.querySelector('#send').click();