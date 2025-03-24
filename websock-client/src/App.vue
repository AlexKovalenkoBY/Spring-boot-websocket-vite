<template>
  <div>
    <h1>WebSocket Chat</h1>
    <div>
      <input
        type="text"
        v-model="userMessage"
        placeholder="Введите сообщение"
      />
      <button @click="sendMessage">Отправить</button>
    </div>
    <div>
      <h2>Сообщения:</h2>
      <ul>
        <li v-for="(msg, index) in messages" :key="index">{{ msg }}</li>
      </ul>
    </div>
  </div>
</template>

<script>
import * as Stomp from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default {
  data() {
    return {
      stompClient: null,
      userMessage: '',
      messages: [], // Массив для хранения полученных сообщений
    };
  },
  methods: {
    sendMessage() {
      if (this.stompClient && this.userMessage.trim() !== '') {
        // Отправляем сообщение на сервер (Spring Boot обработает его в /app/hello)
        this.stompClient.send('/app/hello', {}, this.userMessage);
        this.userMessage = ''; // Очищаем поле ввода
      }
    },
    connectWebSocket() {
      // Указываем адрес подключения
      const socket = new SockJS('/ws');

      this.stompClient = Stomp.Stomp.over(socket); // Передаём SockJS объект

      // Подключаемся к серверу
      this.stompClient.connect({}, (frame) => {
        console.log('Connected:', frame);

        // Подписываемся на топик
        this.stompClient.subscribe('/topic/greetings', (message) => {
          this.messages.push(message.body);
        });
      }, (error) => {
        console.error('Connection error:', error);
      });
    },
  },
  mounted() {
    // Подключаем WebSocket при загрузке компонента
    this.connectWebSocket();
  },
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
