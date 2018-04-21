package com.example.fatima.grapeapplication.callback;

import com.example.fatima.grapeapplication.model.User;

public interface LoginCallback extends ErrorHandlerInterface {
    void onLoginDone(User user);
}
