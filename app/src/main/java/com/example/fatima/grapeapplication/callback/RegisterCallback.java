package com.example.fatima.grapeapplication.callback;

import com.example.fatima.grapeapplication.model.User;

public interface RegisterCallback extends ErrorHandlerInterface {
    void onUserRegisterDone(User user);
}
