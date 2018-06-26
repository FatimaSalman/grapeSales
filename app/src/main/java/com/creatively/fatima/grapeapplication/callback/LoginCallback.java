package com.creatively.fatima.grapeapplication.callback;

import com.creatively.fatima.grapeapplication.model.User;

public interface LoginCallback extends ErrorHandlerInterface {
    void onLoginDone(User user);
}
