<template>
    <div>
        <h1>WebSocket Chat</h1>
        <div>
            <input type="text" v-model="userMessage" placeholder="Введите сообщение" />
            <button @click="sendMessage">Отправить</button>
        </div>
        <div>
            <h2>Сообщения:</h2>
            <ul>
                <!-- Изменен порядок отображения через reverse() -->
                <li v-for="(msg, index) in [...messages].reverse()" :key="index">
                    {{ msg }}
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import { Client } from '@stomp/stompjs';

export default {
    data() {
        return {
            stompClient: null,
            userMessage: '',
            messages: [],
            isConnected: false
        };
    },
    methods: {
        sendMessage() {
            if (this.stompClient && this.stompClient.connected) {
                this.stompClient.publish({
                    destination: '/app/hello',
                    body: JSON.stringify({ message: this.userMessage }),
                    headers: { 'content-type': 'application/json' }
                });
                this.userMessage = '';
            } else {
                console.error("Connection not established! Trying to reconnect...");
                this.connectWebSocket();
                setTimeout(() => this.sendMessage(), 1000);
            }
        },
        connectWebSocket() {
            this.stompClient = new Client({
                brokerURL: 'ws://localhost:8080/ws',
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
                debug: (str) => console.log('STOMP:', str),
                
                connectHeaders: {
                    'accept-version': '1.2',
                    'heart-beat': '10000,10000'
                },

                onConnect: (frame) => {
                    this.isConnected = true;
                    console.log('Connected:', frame);
                    this.stompClient.subscribe('/topic/greetings', (message) => {
                        console.log('Received:', message);
                        // Новые сообщения добавляются в конец массива
                        this.messages.push(message.body);
                    });
                },
                onDisconnect: () => {
                    this.isConnected = false;
                    console.log("Disconnected");
                }
            });

            this.stompClient.activate();
        }
    },
    mounted() {
        this.connectWebSocket();
    },
    beforeUnmount() {
        if (this.stompClient) {
            this.stompClient.deactivate();
        }
    }
};
</script>


<style>
h1 {
    font-size: 2rem;
    margin-bottom: 1rem;
}

input {
    padding: 0.5rem;
    margin-right: 1rem;
}

button {
    padding: 0.5rem 1rem;
    background-color: #007bff;
    color: #fff;
    border: none;
    cursor: pointer;
}

button:hover {
    background-color: #0056b3;
}

ul {
    list-style: none;
    padding: 0;
}

li {
    margin: 0.5rem 0;
    background-color: #f1f1f1;
    padding: 0.5rem;
    border-radius: 5px;
}
</style>
