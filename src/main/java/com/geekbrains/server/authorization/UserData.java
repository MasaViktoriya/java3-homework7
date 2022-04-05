package com.geekbrains.server.authorization;

public class UserData {
    private final String login;
    private final String password;
    private final String nickname;

    public UserData(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}
