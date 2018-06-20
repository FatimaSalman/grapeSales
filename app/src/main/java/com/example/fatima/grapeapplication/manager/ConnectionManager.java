package com.example.fatima.grapeapplication.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.callback.ConnectionManagerInterface;
import com.example.fatima.grapeapplication.callback.InstallCallback;
import com.example.fatima.grapeapplication.callback.InternetAvailableCallback;
import com.example.fatima.grapeapplication.callback.LoginCallback;
import com.example.fatima.grapeapplication.callback.OfferCallback;
import com.example.fatima.grapeapplication.callback.RegisterCallback;
import com.example.fatima.grapeapplication.model.Images;
import com.example.fatima.grapeapplication.model.Offer;
import com.example.fatima.grapeapplication.model.Order;
import com.example.fatima.grapeapplication.model.Shop;
import com.example.fatima.grapeapplication.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class ConnectionManager implements ConnectionManagerInterface {

//    private static int LOAD_LIMIT = 10;

    private Context mContext;
    private ExecutorService mExecutorService;
    private Handler handler;

    public ConnectionManager(Context context) {
        this.mContext = context;
        mExecutorService = Executors.newFixedThreadPool(2);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void login(final User user, final LoginCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                            .addFormDataPart("full_name", user.getFull_name())
                                            .addFormDataPart("password", user.getPassword())
                                            .addFormDataPart("fcm_token", user.getFcm_token());
                                    RequestBody requestBody = formBody.build();
                                    final OkHttpClient client = new OkHttpClient();
                                    okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                            + "login").post(requestBody).build();
                                    final okhttp3.Response response;
                                    try {
                                        response = client.newCall(request).execute();
                                        String response_data = response.body().string();
                                        Log.e("aaa", response_data);
                                        if (response_data != null) {
                                            try {
                                                final JSONObject jsonObject = new JSONObject(response_data);
                                                if (jsonObject != null) {
                                                    if (callback != null) {
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    if (jsonObject.has("error")) {
                                                                        String error = jsonObject.getString("error");
                                                                        callback.onError(error);
                                                                    } else if (jsonObject.has("success")) {
                                                                        String success = jsonObject.getString("success");
                                                                        JSONObject successJson = new JSONObject(success);
                                                                        User user = new User();
                                                                        user.setToken(successJson.getString("token"));
                                                                        user.setType(successJson.getString("type"));
                                                                        user.setId(successJson.getString("id"));
                                                                        user.setIs_active(successJson.getString("is_active"));
                                                                        callback.onLoginDone(user);
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                    handler.post(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                callback.onError(mContext.getString(R.string.no_internet_connection));
                                                            }
                                                        });
                                                    }

                                                } else {
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            } catch (Exception e) {
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                                    }
                                                });
                                            }
                                        } else {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    callback.onError(mContext.getString(R.string.no_internet_connection));
                                                }
                                            });

                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                callback.onError(mContext.getString(R.string.no_internet_connection));
                                            }
                                        });
                                    }
                                }
                            }).start();

                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void register(final User user, final RegisterCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("full_name", user.getFull_name())
                                    .addFormDataPart("password", user.getPassword())
                                    .addFormDataPart("phone", user.getPhone())
                                    .addFormDataPart("type", user.getType())
                                    .addFormDataPart("fcm_token", user.getFcm_token())
                                    .addFormDataPart("is_active", "0");
                            RequestBody requestBody = formBody.build();
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "register").post(requestBody).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("error")) {
                                        if (callback != null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String error = jsonObject.getString("error");
                                                        callback.onError(error);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                callback.onError(mContext.getString(R.string.no_internet_connection));
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        }
                                    } else if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    User user = new User();
                                                    user.setToken(successJson.getString("token"));
                                                    user.setType(successJson.getString("type"));
                                                    user.setId(successJson.getString("id"));
                                                    user.setIs_active(successJson.getString("is_active"));
                                                    callback.onUserRegisterDone(user);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void getDetails(final String token, final RegisterCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "user-details").get()
                                    .header("Authorization", "Bearer " + token).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    String user_info = successJson.getString("user_info");
                                                    JSONObject infoJson = new JSONObject(user_info);
                                                    User user = new User();
                                                    user.setFull_name(infoJson.getString("full_name"));
                                                    user.setType(infoJson.getString("type"));
                                                    user.setPhone(infoJson.getString("phone"));
                                                    user.setIs_active(infoJson.getString("is_active"));
                                                    callback.onUserRegisterDone(user);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void updateDetails(final User user, final RegisterCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("full_name", user.getFull_name())
                                    .addFormDataPart("phone", user.getPhone());
                            if (!TextUtils.isEmpty(user.getPassword()) || user.getPassword() != null) {
                                formBody.addFormDataPart("password", user.getPassword());
                            }
                            RequestBody requestBody = formBody.build();
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "update-profile").post(requestBody)
                                    .header("Authorization", "Bearer " + user.getToken()).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("error")) {
                                        if (callback != null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String error = jsonObject.getString("error");
                                                        callback.onError(error);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                callback.onError(mContext.getString(R.string.no_internet_connection));
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        }
                                    } else if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    User user = new User();
                                                    user.setType(successJson.getString("type"));
                                                    user.setId(successJson.getString("id"));
                                                    user.setFull_name(successJson.getString("full_name"));
                                                    user.setPhone(successJson.getString("phone"));
                                                    callback.onUserRegisterDone(user);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void addShop(final Shop shop, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("shop_name", shop.getName())
                                    .addFormDataPart("shop_address", shop.getAddress())
                                    .addFormDataPart("shop_phone", shop.getShop_phone())
                                    .addFormDataPart("user_id", shop.getUser_id())
                                    .addFormDataPart("record_no", shop.getRecord_no())
                                    .addFormDataPart("shop_image", shop.getImageFile().getName(),
                                            RequestBody.create(MediaType.parse("image/jpeg"), shop.getImageFile()))
                                    .addFormDataPart("shop_bio", shop.getShop_bio())
                                    .addFormDataPart("category_name", shop.getCategory_name())
                                    .addFormDataPart("city_name", shop.getCity_name());

                            RequestBody requestBody = formBody.build();
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "addShop").post(requestBody).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("error")) {
                                        if (callback != null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String error = jsonObject.getString("error");
                                                        callback.onError(error);
                                                    } catch (final JSONException e) {
                                                        e.printStackTrace();
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                callback.onError(e.getMessage());
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        }
                                    } else if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    callback.onStatusDone(success);
                                                } catch (final JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(e.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void getShopList(final Shop shop, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "listShop?user_id=" + shop.getUser_id() + "&category_name="
                                    + shop.getCategory_name() + "&city_name=" + shop.getCity_name()).get()
                                    .build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    String shop = successJson.getString("shop");
                                                    callback.onStatusDone(shop);
                                                } catch (final JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(e.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void addOffer(final Offer offer, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("offer_name", offer.getOfferTitle())
                                    .addFormDataPart("offer_bio", offer.getOfferBio())
                                    .addFormDataPart("befor_discount", offer.getPreviousPrice())
                                    .addFormDataPart("after_discount", offer.getNextPrice())
                                    .addFormDataPart("shop_id", offer.getShop_id());
//                                    .addFormDataPart("offer_image", offer.getImageFile().getName(),
//                                            RequestBody.create(MediaType.parse("image/jpeg"), offer.getImageFile()));
                            for (Images images : offer.getImagesList()) {
                                formBody.addFormDataPart("offer_image[]", images.getImageFile().getName(),
                                        RequestBody.create(MediaType.parse("image/jpeg"), images.getImageFile()));
                            }

                            RequestBody requestBody = formBody.build();
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "addOffer").post(requestBody).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("error")) {
                                        if (callback != null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String error = jsonObject.getString("error");
                                                        callback.onError(error);
                                                    } catch (final JSONException e) {
                                                        e.printStackTrace();
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                callback.onError(e.getMessage());
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        }
                                    } else if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    callback.onStatusDone(success);
                                                } catch (final JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(e.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void getOfferList(final Offer offer, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "listOffer/" + offer.getShop_id()).get()
                                    .build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    String shop = successJson.getString("offer");
                                                    callback.onStatusDone(shop);
                                                } catch (final JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(e.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void getOfferDetails(final Offer offer, final OfferCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "offer_details/" + offer.getId()).get()
                                    .build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    String id = successJson.getString("id");
                                                    String offer_name = successJson.getString("offer_name");
                                                    String offer_bio = successJson.getString("offer_bio");
                                                    String befor_discount = successJson.getString("befor_discount");
                                                    String after_discount = successJson.getString("after_discount");
                                                    String offer_image = successJson.getString("offer_image");
                                                    String shop_id = successJson.getString("shop_id");
                                                    String shop = successJson.getString("shop");
                                                    JSONObject shopObject = new JSONObject(shop);
                                                    String user_id = shopObject.getString("user_id");
                                                    Offer offer1 = new Offer(id, offer_name,
                                                            offer_bio, befor_discount, user_id,
                                                            after_discount, offer_image, shop_id);
                                                    callback.onOfferDone(offer1);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void updateOffer(final Offer offer, final InstallCallback callback) {
        final JSONArray jsonArray1 = new JSONArray();
        for (Images images : offer.getImagesList()) {
            String imageName = images.getImages();
            jsonArray1.put(imageName);
        }
        Log.e("offerImage", offer.getImagesList() + "");
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("offer_name", offer.getOfferTitle())
                                    .addFormDataPart("offer_bio", offer.getOfferBio())
                                    .addFormDataPart("befor_discount", offer.getPreviousPrice())
                                    .addFormDataPart("after_discount", offer.getNextPrice());

//                            if (jsonArray1 != null)
//                                formBody.addFormDataPart("old_image", jsonArray1 + "");
//
                            for (Images images : offer.getImagesList()) {
                                if (images.getImageFile() != null) {
                                    Log.e("file", images.getImageFile() + "");
                                    formBody.addFormDataPart("offer_image[]", images.getImageFile().getName(),
                                            RequestBody.create(MediaType.parse("image/jpeg"), images.getImageFile()));
                                }
                            }
//                            if (offer.getImageFile() != null)
//                                formBody.addFormDataPart("offer_image", offer.getImageFile().getName(),
//                                        RequestBody.create(MediaType.parse("image/jpeg"), offer.getImageFile()));

                            RequestBody requestBody = formBody.build();
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "update_offer/" + offer.getId()).post(requestBody).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("error")) {
                                        if (callback != null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String error = jsonObject.getString("error");
                                                        callback.onError(error);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                callback.onError(mContext.getString(R.string.no_internet_connection));
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        }
                                    } else if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    callback.onStatusDone(success);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void applyOrder(final Order order, final InstallCallback callback) {

        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("shop_id", order.getShop_id())
                                    .addFormDataPart("offer_id", order.getOffer_id())
                                    .addFormDataPart("user_id", order.getUser_id())
                                    .addFormDataPart("username", order.getUserName())
                                    .addFormDataPart("mobile", order.getMobile())
                                    .addFormDataPart("address", order.getAddress())
                                    .addFormDataPart("note", order.getNote());

                            RequestBody requestBody = formBody.build();
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "add_order").post(requestBody).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("error")) {
                                        if (callback != null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String error = jsonObject.getString("error");
                                                        callback.onError(error);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                callback.onError(mContext.getString(R.string.no_internet_connection));
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        }
                                    } else if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    callback.onStatusDone(success);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void getShopListForUser(final Shop shop, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "listShopUser?category_name=" + shop.getCategory_name()
                                    + "&city_name=" + shop.getCity_name()).get()
                                    .build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    String shop = successJson.getString("shop");
                                                    callback.onStatusDone(shop);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void offerList(final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "listOffers").get().build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    String shop = successJson.getString("offer");
                                                    callback.onStatusDone(shop);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void allShopList(final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "listAllShop").get().build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    String shop = successJson.getString("shop");
                                                    callback.onStatusDone(shop);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void getShopSearch(final String city_name, final String category_name, final String shop_name, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            String url = FontManager.URL
                                    + "searchShops?shop_name=" + shop_name + "&category_name="
                                    + category_name + "&city_name=" + city_name;
                            Log.e("url", url);
                            okhttp3.Request request = new okhttp3.Request.Builder().url(url).get()
                                    .build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    callback.onStatusDone(success);
                                                } catch (final JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(e.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void getOfferSearch(final String offer_name, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "searchOffers?offer_name=" + offer_name).get()
                                    .build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    callback.onStatusDone(success);
                                                } catch (final JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(e.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void addFcmToken(final String fcm_token, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            MultipartBody.Builder formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("fcm_token", fcm_token);

                            RequestBody requestBody = formBody.build();
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "fcm_token").post(requestBody).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("error")) {
                                        if (callback != null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String error = jsonObject.getString("error");
                                                        callback.onError(error);
                                                    } catch (final JSONException e) {
                                                        e.printStackTrace();
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                callback.onError(e.getMessage());
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        }
                                    } else if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    callback.onStatusDone(success);
                                                } catch (final JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(e.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (final Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(e.getMessage());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void allOrderList(final String token, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "listOrders").get()
                                    .header("Authorization", "Bearer " + token).build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    JSONObject successJson = new JSONObject(success);
                                                    String order = successJson.getString("order");
                                                    callback.onStatusDone(order);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void orderDetails(final String order_id, final InstallCallback callback) {
        InternetConnectionUtils.isInternetAvailable(mContext.getApplicationContext(), new InternetAvailableCallback() {
            @Override
            public void onInternetAvailable(boolean isAvailable) {
                if (isAvailable) {
                    mExecutorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            final OkHttpClient client = new OkHttpClient();
                            okhttp3.Request request = new okhttp3.Request.Builder().url(FontManager.URL
                                    + "order_details/" + order_id).get().build();
                            final okhttp3.Response response;
                            try {
                                response = client.newCall(request).execute();
                                String response_data = response.body().string();
                                Log.e("aaa", response_data);
                                if (response_data != null) {
                                    final JSONObject jsonObject = new JSONObject(response_data);
                                    if (jsonObject.has("success")) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    String success = jsonObject.getString("success");
                                                    callback.onStatusDone(success);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onError(mContext.getString(R.string.no_internet_connection));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onError(mContext.getString(R.string.no_internet_connection));
                                    }
                                });
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(mContext.getString(R.string.no_internet_connection));
                        }
                    });

                }
            }
        });
    }
}
