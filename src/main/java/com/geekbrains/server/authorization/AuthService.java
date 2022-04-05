package com.geekbrains.server.authorization;

public interface AuthService {
    void start();
    String getNicknameByLoginAndPassword (String login, String password);
    void end();
}
