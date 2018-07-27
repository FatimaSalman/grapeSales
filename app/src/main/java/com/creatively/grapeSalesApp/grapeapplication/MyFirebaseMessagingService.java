package com.creatively.grapeSalesApp.grapeapplication;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.creatively.grapeSalesApp.grapeapplication.activity.MainActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.OfferDetailsActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.OrderDetailsActivity;
import com.creatively.grapeSalesApp.grapeapplication.activity.OrderUserDetailsActivity;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppPreferences;
import com.creatively.grapeSalesApp.grapeapplication.manager.Config;
import com.creatively.grapeSalesApp.grapeapplication.manager.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
            // check for image attachment
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("message", message);

            showNotificationMessage(getApplicationContext(), getString(R.string.app_name), message, "", resultIntent);

        } else {
            // app is in background, show the notification in notification tray
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("message", message);

            showNotificationMessage(getApplicationContext(), getString(R.string.app_name), message, "", resultIntent);

        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("a_data");
            String title = data.getString("title");
            String message = data.getString("message");
            String timestamp = data.getString("timestamp");
            JSONObject timeJson = new JSONObject(timestamp);
            String date = timeJson.getString("date");
            if (data.has("offer_id")) {
                Log.e("dataaaa", "dataaaa");
                if (data.has("status")) {
                    String status = data.getString("status");
                    String id = data.getString("id");
                    Log.e("status", status);
                    if (TextUtils.equals(status, "1") || TextUtils.equals(status, "3")|| TextUtils.equals(status, "5")) {
                        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                            // app is in foreground, broadcast the push message
                            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                            pushNotification.putExtra("message", message);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                            // play notification sound
                            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                            notificationUtils.playNotificationSound();
                            // check for image attachment
                            Intent resultIntent = new Intent(getApplicationContext(), OrderUserDetailsActivity.class);
                            if (TextUtils.equals(status, "5")){
                                resultIntent.putExtra("rating", "rating");
                            }
                            resultIntent.putExtra("order_id", id);
                            showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);

                        } else {
                            // app is in background, show the notification in notification tray
                            Intent resultIntent = new Intent(getApplicationContext(), OrderUserDetailsActivity.class);
                            resultIntent.putExtra("order_id", id);
                            if (TextUtils.equals(status, "5")){
                                resultIntent.putExtra("rating", "rating");
                            }
                            // check for image attachment
                            showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);
                        }
                    } else if (TextUtils.equals(status, "2")) {
                        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                            // app is in foreground, broadcast the push message
                            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                            pushNotification.putExtra("message", message);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                            // play notification sound
                            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                            notificationUtils.playNotificationSound();
                            // check for image attachment
                            Intent resultIntent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                            resultIntent.putExtra("order_id", id);
                            showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);

                        } else {
                            // app is in background, show the notification in notification tray
                            Intent resultIntent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                            resultIntent.putExtra("order_id", id);

                            // check for image attachment
                            showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);
                        }
                    } else if (TextUtils.equals(status, "4")) {
                        if (data.has("delivery_type")) {
                            String delivery_type = data.getString("delivery_type");
                            if (TextUtils.equals(delivery_type, "null")) {
                                if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                                    // app is in foreground, broadcast the push message
                                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                                    pushNotification.putExtra("message", message);
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                                    // play notification sound
                                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                                    notificationUtils.playNotificationSound();
                                    // check for image attachment
                                    Intent resultIntent = new Intent(getApplicationContext(), OrderUserDetailsActivity.class);
                                    resultIntent.putExtra("order_id", id);
                                    resultIntent.putExtra("process", "deliveryDetails");
                                    showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);

                                } else {
                                    // app is in background, show the notification in notification tray
                                    Intent resultIntent = new Intent(getApplicationContext(), OrderUserDetailsActivity.class);
                                    resultIntent.putExtra("order_id", id);
                                    resultIntent.putExtra("process", "deliveryDetails");
                                    // check for image attachment
                                    showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);
                                }
                            } else {
                                if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                                    // app is in foreground, broadcast the push message
                                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                                    pushNotification.putExtra("message", message);
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                                    // play notification sound
                                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                                    notificationUtils.playNotificationSound();
                                    // check for image attachment
                                    Intent resultIntent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                                    resultIntent.putExtra("order_id", id);
                                    showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);

                                } else {
                                    // app is in background, show the notification in notification tray
                                    Intent resultIntent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                                    resultIntent.putExtra("order_id", id);
                                    // check for image attachment
                                    showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);
                                }
                            }
                        }
                    }
                } else {
                    String offer_id = data.getString("offer_id");
                    if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        // app is in foreground, broadcast the push message
                        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("message", message);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                        // play notification sound
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                        // check for image attachment
                        Intent resultIntent = new Intent(getApplicationContext(), OfferDetailsActivity.class);
                        resultIntent.putExtra("offer_id", offer_id);
                        resultIntent.putExtra("message", message);
                        showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);

                    } else {
                        // app is in background, show the notification in notification tray
                        Intent resultIntent = new Intent(getApplicationContext(), OfferDetailsActivity.class);
                        resultIntent.putExtra("offer_id", offer_id);
                        resultIntent.putExtra("message", message);

                        // check for image attachment
                        showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);
                    }
                }
            } else if (data.has("order_id")) {
                String order_id = data.getString("order_id");
                if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                    // check for image attachment
                    Intent resultIntent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                    resultIntent.putExtra("order_id", order_id);
                    showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);

                } else {
                    // app is in background, show the notification in notification tray
                    Intent resultIntent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
                    resultIntent.putExtra("order_id", order_id);

                    // check for image attachment
                    showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);
                }
            } else {
                if (data.has("is_active")) {
                    AppPreferences.saveString(this, "active", data.getString("is_active"));
                }
                if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                    // check for image attachment
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("is_active", "active");
                    showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);

                } else {
                    // app is in background, show the notification in notification tray
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    resultIntent.putExtra("is_active", "active");
                    // check for image attachment
                    showNotificationMessage(getApplicationContext(), title, message, date, resultIntent);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}