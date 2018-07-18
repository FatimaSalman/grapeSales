package com.creatively.grapeSalesApp.grapeapplication.callback;

import com.creatively.grapeSalesApp.grapeapplication.model.User;

public interface LoginCallback extends ErrorHandlerInterface {
    void onLoginDone(User user);
}
