import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'


const handlers = []
var stompClient = null

export function connect() {
    const socket = new SockJS('/chat-websocket')
    stompClient = Stomp.over(socket)
    stompClient.connect({}, frame => {
        setConnected(true)
        console.log('Connected: ' + frame)
        stompClient.subscribe('/topic/chat', message => {
            handlers.forEach(handler => handler(JSON.parse(message.body)))
        })
    })
}

export function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect()
    }
    console.log("Disconnected")
}

export function sendMessage(message) {
    stompClient.send("/app/send", {}, JSON.stringify(message))
}

export function addHandler(handler) {
    handlers.push(handler)
}
