package com.creatively.grapeSalesApp.grapeapplication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;
import com.creatively.grapeSalesApp.grapeapplication.callback.InstallCallback;
import com.creatively.grapeSalesApp.grapeapplication.manager.AppErrorsManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.ConnectionManager;
import com.creatively.grapeSalesApp.grapeapplication.manager.FontManager;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class OrderUserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ConnectionManager connectionManager;
    private String order_id, offer_id, rating, shop_id;
    private TextView offerNameTxt, shopNameTxt, locationTxt, noteTxt, offerPreviousPrice,
            offerNextPrice, statusTxt, typeTxt;
    private SliderLayout sliderLayout;
    private ArrayList<String> imageArrayList = new ArrayList<>();
    private String imageArray;
    private Handler handler;
    private ScrollView scrollView;
    private Button cancelBtn;
    private RelativeLayout typeLayout;
    private int totalRatingOffer = 0, totalRatingShop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_order_user_details);
        handler = new Handler(Looper.getMainLooper());
        connectionManager = new ConnectionManager(this);
        order_id = getIntent().getStringExtra("order_id");
        String process = getIntent().getStringExtra("process");
        rating = getIntent().getStringExtra("rating");
        init();

        if (process != null) {
            if (TextUtils.equals(process, "deliveryDetails")) {
                openDialogDelivery();
            }
        }
    }

    public void init() {

        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);
        cancelBtn = findViewById(R.id.cancelBtn);
        typeLayout = findViewById(R.id.typeLayout);
        cancelBtn.setOnClickListener(this);

        offerNameTxt = findViewById(R.id.offerNameTxt);
        shopNameTxt = findViewById(R.id.shopNameTxt);
        locationTxt = findViewById(R.id.locationTxt);
        noteTxt = findViewById(R.id.noteTxt);
        statusTxt = findViewById(R.id.statusTxt);
        typeTxt = findViewById(R.id.typeTxt);
        offerPreviousPrice = findViewById(R.id.offerPreviousPrice);
        offerNextPrice = findViewById(R.id.offerNextPrice);
        sliderLayout = findViewById(R.id.slider);

        getOrderDetails(order_id);
    }

    public void getImageListSlider() {
//        imageArrayList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < imageArrayList.size(); i++) {
                    imageArray = imageArrayList.get(i);

                    final TextSliderView textSliderView = new TextSliderView(OrderUserDetailsActivity.this);
                    textSliderView
                            .image(FontManager.IMAGE_URL + imageArray)
                            .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent i = new Intent(OrderUserDetailsActivity.this, ImageViewerPagerActivity.class);
                                    i.putStringArrayListExtra("IMAGES", imageArrayList);
                                    i.putExtra("SELECTED", slider.getBundle().getInt("position"));
                                    startActivity(i);
                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putSerializable("extra", imageArrayList.get(i));
                    textSliderView.getBundle()
                            .putInt("position", i);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sliderLayout.addSlider(textSliderView);
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sliderLayout.setCurrentPosition(0, true);
                        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
                        sliderLayout.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
                        sliderLayout.setCustomAnimation(new DescriptionAnimation());
                        sliderLayout.setDuration(4000);
                    }
                });

            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backLayout) {
            finish();
        } else if (id == R.id.cancelBtn) {
            AppErrorsManager.showSuccessDialog(OrderUserDetailsActivity.this,
                    "إلغاء الطلب", "هل أنت متأكد من إلغاء هذا الطلب؟", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cancleOrder(order_id);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        }
    }

    public void getOrderDetails(String order_id) {
        imageArrayList.clear();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        connectionManager.orderDetails(order_id, new InstallCallback() {
            @Override
            public void onStatusDone(String reslut) {

                try {
                    JSONObject jsonObject = new JSONObject(reslut);
//                    String id = jsonObject.getString("id");
//                    String username = jsonObject.getString("username");
//                    String mobile = jsonObject.getString("mobile");
                    offer_id = jsonObject.getString("offer_id");
                    shop_id = jsonObject.getString("shop_id");
                    String address = jsonObject.getString("address");
                    String note = jsonObject.getString("note");
                    String status = jsonObject.getString("status");
                    String delivery_type = jsonObject.getString("delivery_type");

                    if (TextUtils.equals(delivery_type, "null")) {
                        typeLayout.setVisibility(View.GONE);
                    } else {
                        typeLayout.setVisibility(View.VISIBLE);
                        typeTxt.setText(delivery_type);
                    }
                    if (TextUtils.equals(status, "0")) {
                        cancelBtn.setVisibility(View.VISIBLE);
                    } else if (TextUtils.equals(status, "1") || TextUtils.equals(status, "2")
                            || TextUtils.equals(status, "3") || TextUtils.equals(status, "4")
                            || TextUtils.equals(status, "5")) {
                        cancelBtn.setVisibility(View.GONE);
                    }
                    if (TextUtils.equals(status, "0")) {
                        statusTxt.setText("قيد الانتظار");
                    } else if (TextUtils.equals(status, "1")) {
                        statusTxt.setText("قيد التجهيز");
                    } else if (TextUtils.equals(status, "2")) {
                        statusTxt.setText("تم الغاء الطلب");
                    } else if (TextUtils.equals(status, "3")) {
                        statusTxt.setText("تم رفض الطلب");
                    } else if (TextUtils.equals(status, "4")) {
                        statusTxt.setText("قيد التوصيل");
                    } else if (TextUtils.equals(status, "5")) {
                        statusTxt.setText("اكتمال الطلب");
                    }
                    if (rating != null) {
                        if (TextUtils.equals(rating, "rating")) {
                            openDialogRating();
                        }
                    }
                    String shop = jsonObject.getString("shop");
                    JSONObject shopObject = new JSONObject(shop);
                    String shop_name = shopObject.getString("shop_name");
                    String offer = jsonObject.getString("offer");
                    JSONObject offerObject = new JSONObject(offer);
                    String offer_name = offerObject.getString("offer_name");
                    String befor_discount = offerObject.getString("befor_discount");
                    String after_discount = offerObject.getString("after_discount");
                    String offer_image = offerObject.getString("offer_image");
                    JSONArray jsonArray2 = new JSONArray(offer_image);
                    for (int j = 0; j < jsonArray2.length(); j++) {
                        String aa = jsonArray2.getString(j);
                        imageArrayList.add(aa);
                    }
                    offerNameTxt.setText(offer_name);
                    locationTxt.setText(address);
                    if (TextUtils.equals(note, "null"))
                        noteTxt.setText("لا يوجد ملاحظات");
                    else
                        noteTxt.setText(note);
                    shopNameTxt.setText(shop_name);
                    offerPreviousPrice.setText(befor_discount);
                    offerPreviousPrice.setPaintFlags(offerPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    offerNextPrice.setText(after_discount);
                    progressDialog.dismiss();
                    scrollView.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getImageListSlider();

            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(OrderUserDetailsActivity.this, error);
            }
        });

    }

    public void cancleOrder(final String order_id) {
        imageArrayList.clear();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        connectionManager.cancleOrder(order_id, new InstallCallback() {
            @Override
            public void onStatusDone(String result) {
                progressDialog.dismiss();
                AppErrorsManager.showSuccessDialog(OrderUserDetailsActivity.this, result, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getOrderDetails(order_id);
                    }
                });
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(OrderUserDetailsActivity.this, error);
            }
        });
    }

    public void openDialogDelivery() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_delivery_type_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(dialogView);
        dialogView.findViewById(R.id.storeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(OrderUserDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
                connectionManager.deliveryType(order_id, getString(R.string.devlivery_from_store), new InstallCallback() {
                    @Override
                    public void onStatusDone(String status) {
                        progressDialog.dismiss();

                        AppErrorsManager.showSuccessDialog(OrderUserDetailsActivity.this, status, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getOrderDetails(order_id);
                                deleteDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        AppErrorsManager.showErrorDialog(OrderUserDetailsActivity.this, error);
                    }
                });
            }
        });
        dialogView.findViewById(R.id.serviceBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(OrderUserDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
                connectionManager.deliveryType(order_id, getString(R.string.use_delivery_service), new InstallCallback() {
                    @Override
                    public void onStatusDone(String status) {
                        progressDialog.dismiss();

                        AppErrorsManager.showSuccessDialog(OrderUserDetailsActivity.this, status, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getOrderDetails(order_id);
                                deleteDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        AppErrorsManager.showErrorDialog(OrderUserDetailsActivity.this, error);
                    }
                });
            }
        });
        deleteDialog.show();
    }

    public void openDialogRating() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View dialogView = factory.inflate(R.layout.select_rating_dialog, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
        deleteDialog.setView(dialogView);

        RatingBar ratingOffer = dialogView.findViewById(R.id.ratingOffer);
        ratingOffer.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                totalRatingOffer = (int) v;
            }
        });
        RatingBar ratingShop = dialogView.findViewById(R.id.ratingShop);
        ratingShop.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                totalRatingShop = (int) v;
            }
        });

        dialogView.findViewById(R.id.ratingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(OrderUserDetailsActivity.this);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
                connectionManager.ratingOfferShop(offer_id, String.valueOf(totalRatingOffer), shop_id, String.valueOf(totalRatingShop), new InstallCallback() {
                    @Override
                    public void onStatusDone(String status) {
                        progressDialog.dismiss();
                        AppErrorsManager.showSuccessDialog(OrderUserDetailsActivity.this, status, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        AppErrorsManager.showErrorDialog(OrderUserDetailsActivity.this, error);
                    }
                });
            }
        });
        deleteDialog.show();
    }


}
