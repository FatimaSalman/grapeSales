package com.example.fatima.grapeapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.fatima.grapeapplication.R;
import com.example.fatima.grapeapplication.adapter.OfferAdapter;
import com.example.fatima.grapeapplication.callback.OfferCallback;
import com.example.fatima.grapeapplication.callback.OnItemClickListener;
import com.example.fatima.grapeapplication.manager.AppErrorsManager;
import com.example.fatima.grapeapplication.manager.ConnectionManager;
import com.example.fatima.grapeapplication.manager.FontManager;
import com.example.fatima.grapeapplication.model.Offer;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OfferDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private ConnectionManager connectionManager;
    private String offer_id, shop_id, user_id;
    //    private RoundedImageView shopImage;
//    private ProgressBar progressBar;
    private TextView shopNameTxt, shopAddressTxt, offerPreviousPrice, offerNextPrice;
    private SliderLayout sliderLayout;
    private ArrayList<String> imageArrayList = new ArrayList<>();
    private String imageArray;
    private Handler handler;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_offer_details);
        handler = new Handler(Looper.getMainLooper());
        connectionManager = new ConnectionManager(this);
        offer_id = getIntent().getStringExtra("offer_id");
        shop_id = getIntent().getStringExtra("shop_id");
        init();
    }

    public void init() {

        RelativeLayout backLayout = findViewById(R.id.backLayout);
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        Button backBtn = findViewById(R.id.backBtn);
        backLayout.setOnClickListener(this);
        Button orderBtn = findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

//        shopImage = findViewById(R.id.shopImage);
//        progressBar = findViewById(R.id.progressBar);
        shopNameTxt = findViewById(R.id.shopNameTxt);
        shopAddressTxt = findViewById(R.id.shopAddressTxt);
        offerPreviousPrice = findViewById(R.id.offerPreviousPrice);
        offerNextPrice = findViewById(R.id.offerNextPrice);
        sliderLayout = findViewById(R.id.slider);

        getOfferDetails();
    }

    public void getImageListSlider() {
//        imageArrayList.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < imageArrayList.size(); i++) {
                    imageArray = imageArrayList.get(i);
                    Log.e("images", FontManager.IMAGE_URL + imageArray);

                    final TextSliderView textSliderView = new TextSliderView(OfferDetailsActivity.this);
                    textSliderView
                            .image(FontManager.IMAGE_URL + imageArray)
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent i = new Intent(OfferDetailsActivity.this, ImageViewerPagerActivity.class);
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
        if (id == R.id.backLayout || id == R.id.backBtn) {
            finish();
        } else if (id == R.id.orderBtn) {
            Intent intent = new Intent(this, UserApplyFragment.class);
            intent.putExtra("offer_id", offer_id);
            intent.putExtra("shop_id", shop_id);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        }
    }

    public void getOfferDetails() {
        imageArrayList.clear();
        Offer offer = new Offer();
        offer.setId(offer_id);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        connectionManager.getOfferDetails(offer, new OfferCallback() {
            @Override
            public void onOfferDone(Offer offer1) {
                progressDialog.dismiss();
                scrollView.setVisibility(View.VISIBLE);
                shopNameTxt.setText(offer1.getOfferTitle());
                offerPreviousPrice.setText(offer1.getPreviousPrice());
                offerPreviousPrice.setPaintFlags(offerPreviousPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                offerNextPrice.setText(offer1.getNextPrice());
                shopAddressTxt.setText(offer1.getOfferBio());
                user_id = offer1.getUser_id();
//                progressBar.setVisibility(View.VISIBLE);

                try {
                    JSONArray jsonArray2 = new JSONArray(offer1.getOfferImage());
                    for (int j = 0; j < jsonArray2.length(); j++) {
                        String aa = jsonArray2.getString(j);
                        imageArrayList.add(aa);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getImageListSlider();

            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                AppErrorsManager.showErrorDialog(OfferDetailsActivity.this, error);
            }
        });

    }
}
