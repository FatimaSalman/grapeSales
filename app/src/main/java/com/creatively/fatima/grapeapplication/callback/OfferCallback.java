package com.creatively.fatima.grapeapplication.callback;

import com.creatively.fatima.grapeapplication.model.Offer;
import com.creatively.fatima.grapeapplication.model.User;

public interface OfferCallback extends ErrorHandlerInterface {
    void onOfferDone(Offer user);
}
