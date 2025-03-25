<template>
  <!-- Шаблон остаётся без изменений -->
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
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default {
  data() {
    return {
      stompClient: null,
      userMessage: '',
      messages: [],
    };
  },
  methods: {
    sendMessage() {
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.send(
          '/app/hello',
          {},
          JSON.stringify({ message: this.userMessage })
        );
        this.userMessage = '';
      } else {
        console.error('STOMP connection is not established.');
      }
    },
    connectWebSocket() {
      const socket = new SockJS('http://localhost:8080/ws');
      this.stompClient = Stomp.over(socket);

      // Добавляем обработку ошибок подключения
      this.stompClient.onStompError = (frame) => {
        console.error('STOMP protocol error:', frame.headers.message);
      };

      this.stompClient.connect(
        {},
        (frame) => {
          console.log('Connected:', frame);
          this.stompClient.subscribe('/topic/greetings', (message) => {
            try {
              // Проверяем тип содержимого
              if (message.headers['content-type']?.includes('application/json')) {
                this.messages.push(JSON.parse(message.body).message);
              } else {
                // Обрабатываем обычный текст
                this.messages.push(message.body);
              }
            } catch (error) {
              console.error('Error parsing message:', error);
              this.messages.push(`Ошибка: Некорректное сообщение - ${message.body}`);
            }
          });
        },
        (error) => {
          console.error('Connection error:', error);
          setTimeout(this.connectWebSocket, 5000); // Переподключение через 5 сек
        }
      );
    },
  },
  mounted() {
    this.connectWebSocket();
  },
  beforeUnmount() {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
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
