package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public String getNickname() {
        return nickName;
    }

    private String nickName;

    public ClientHandler (Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        authentication();
                        readMessages();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        closeConnection();
                    }
                }
            }).start();
        }
        catch (IOException exception) {
            exception.printStackTrace();
            throw  new RuntimeException("Проблемы при создании обработчика");
        }
    }

    public void authentication () throws IOException {
        while (true) {
            String message = inputStream.readUTF();
            if(message.startsWith(ServerCommandConstants.AUTHORIZATION)) {
                String[] authInfo = message.split("\\s");
                String nickName = server.getAuthService().getNicknameByLoginAndPassword(authInfo[1],  authInfo[2]);
                if (nickName != null ) {
                    if (!server.isNickNameBusy(nickName)) {
                        sendMessage("/authOK" + nickName);
                        this.nickName = nickName;
                        server.broadcastMessage(nickName + " зашел в чат");
                        server.addConnectedUser(this);
                        return;
                    } else {
                        sendMessage("Учетная запись уже используется");
                    }
                } else {
                    sendMessage("Неверные логин или пароль");
                }
            }
        }
    }

    private void readMessages () throws IOException {
        while (true) {
            String messageInChat = inputStream.readUTF();
            System.out.println("от " + nickName + ": " + messageInChat);
            if (messageInChat.equals(ServerCommandConstants.SHUTDOWN)){
                return;
            }
            server.broadcastMessage(nickName + ": " + messageInChat);
        }
    }

    public void sendMessage (String message) {
        try {
            outputStream.writeUTF(message);
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }

    private void closeConnection () {
        server.disconnectUser(this);
        server.broadcastMessage(nickName + " вышел из чата");
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }
}
