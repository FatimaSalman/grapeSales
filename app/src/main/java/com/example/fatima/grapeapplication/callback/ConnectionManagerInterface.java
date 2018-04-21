package com.example.fatima.grapeapplication.callback;

import com.example.fatima.grapeapplication.model.Offer;
import com.example.fatima.grapeapplication.model.Shop;
import com.example.fatima.grapeapplication.model.User;

public interface ConnectionManagerInterface {

    void login(User user, LoginCallback callback);

    void register(User user, RegisterCallback callback);

    void getDetails(final String token, final RegisterCallback callback);

    void updateDetails(final User user, final RegisterCallback callback);

    void addShop(final Shop shop, final InstallCallback callback);

    void getShopList(final Shop shop, final InstallCallback callback);

    void addOffer(final Offer offer, final InstallCallback callback);

    void getOfferList(final Offer offer, final InstallCallback callback);

    void getOfferDetails(final Offer offer, final OfferCallback callback);

    void updateOffer(final Offer offer, final InstallCallback callback);

    void getShopListForUser(final Shop shop, final InstallCallback callback);

    void offerList( final InstallCallback callback);
}