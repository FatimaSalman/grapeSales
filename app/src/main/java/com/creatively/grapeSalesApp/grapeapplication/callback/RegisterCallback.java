package com.creatively.grapeSalesApp.grapeapplication.callback;

import com.creatively.grapeSalesApp.grapeapplication.model.User;

public interface RegisterCallback extends ErrorHandlerInterface {
    void onUserRegisterDone(User user);
}
