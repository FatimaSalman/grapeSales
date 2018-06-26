package com.creatively.fatima.grapeapplication.callback;

import com.creatively.fatima.grapeapplication.model.User;

public interface RegisterCallback extends ErrorHandlerInterface {
    void onUserRegisterDone(User user);
}
