package com.epam.camp.bff.api.service;

public interface AuthenticationService {
    String login(String username, String password);

    String login();

    String getToken();
}
