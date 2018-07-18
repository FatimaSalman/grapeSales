package com.creatively.grapeSalesApp.grapeapplication.callback;

import com.creatively.grapeSalesApp.grapeapplication.model.Offer;
import com.creatively.grapeSalesApp.grapeapplication.model.User;

public interface OfferCallback extends ErrorHandlerInterface {
    void onOfferDone(Offer user);
}
