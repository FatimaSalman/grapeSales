package com.example.fatima.grapeapplication.callback;

import com.example.fatima.grapeapplication.model.Offer;
import com.example.fatima.grapeapplication.model.User;

public interface OfferCallback extends ErrorHandlerInterface {
    void onOfferDone(Offer user);
}
