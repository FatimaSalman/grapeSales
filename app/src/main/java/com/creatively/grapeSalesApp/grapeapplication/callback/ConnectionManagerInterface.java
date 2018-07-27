package com.creatively.grapeSalesApp.grapeapplication.callback;

import com.creatively.grapeSalesApp.grapeapplication.model.Offer;
import com.creatively.grapeSalesApp.grapeapplication.model.Order;
import com.creatively.grapeSalesApp.grapeapplication.model.Shop;
import com.creatively.grapeSalesApp.grapeapplication.model.User;

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

    void offerList(final InstallCallback callback);

    void allShopList(final InstallCallback callback);

    void getShopSearch(final String city_name, final String category_name, final String shop_name, final InstallCallback callback);

    void getOfferSearch(final String offer_name, final InstallCallback callback);

    void applyOrder(final Order order, final String token, final InstallCallback callback);

    void addFcmToken(final String fcm_token, final InstallCallback callback);

    void allOrderList(final String user_id, final InstallCallback callback);

    void orderDetails(final String order_id, final InstallCallback callback);

    void getOfferStoreSearch(final String id, final String offer_name, final InstallCallback callback);

    void ratingOffer(final String offer_id, final String rating, final InstallCallback callback);

    void ratingNumber(final String offer_id, final InstallCallback callback);

    void deleteShop(final String shop_id, final InstallCallback callback);

    void deleteOffer(final String offer_id, final InstallCallback callback);

    void updateShop(final Shop shop, final InstallCallback callback);

    void allUserOrderList(final String token, final InstallCallback callback);

    void processDoneOrder(final String order_id, final InstallCallback callback);

    void cancleOrder(final String order_id, final InstallCallback callback);

    void rejectOrder(final String order_id, final InstallCallback callback);

    void acceptOrder(final String order_id, final InstallCallback callback);

    void completedOrder(final String order_id, final InstallCallback callback);

    void deliveryType(final String order_id, final String type, final InstallCallback callback);

    void ratingOfferShop(final String offer_id, final String rating_offer,
                         final String shop_id, final String rating_shop, final InstallCallback callback);

    void getShopDetails(final String shop_id, final InstallCallback callback);
}