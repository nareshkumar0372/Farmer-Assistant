package com.example.farmerassistant;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MarketPriceActivity extends AppCompatActivity {

    private ListView listView;
    private ImageButton btnBack;
    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_price);

        listView = findViewById(R.id.listMarketPrice);
        btnBack = findViewById(R.id.btnBack);
        tvDate = findViewById(R.id.tvDate);

        setCurrentDate();
        loadMarketPrices();

        btnBack.setOnClickListener(v -> finish());
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));
    }

    private void loadMarketPrices() {

        ArrayList<String> marketPrices = new ArrayList<>();

        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Vegetables
        marketPrices.add("VEGETABLES");
        marketPrices.add("Tomato - ₹" + getDailyPrice(20, day, 3) + " per kg");
        marketPrices.add("Potato - ₹" + getDailyPrice(18, day, 2) + " per kg");
        marketPrices.add("Onion - ₹" + getDailyPrice(25, day, 4) + " per kg");
        marketPrices.add("Carrot - ₹" + getDailyPrice(30, day, 3) + " per kg");
        marketPrices.add("Cabbage - ₹" + getDailyPrice(15, day, 2) + " per kg");
        marketPrices.add("Brinjal - ₹" + getDailyPrice(22, day, 3) + " per kg");
        marketPrices.add("Cauliflower - ₹" + getDailyPrice(28, day, 4) + " per kg");
        marketPrices.add("Spinach - ₹" + getDailyPrice(10, day, 1) + " per bunch");
        marketPrices.add("Green Chilli - ₹" + getDailyPrice(40, day, 5) + " per kg");
        marketPrices.add("Lady Finger - ₹" + getDailyPrice(26, day, 3) + " per kg");
        marketPrices.add("Pumpkin - ₹" + getDailyPrice(18, day, 2) + " per kg");
        marketPrices.add("Beans - ₹" + getDailyPrice(35, day, 4) + " per kg");

        // Fruits
        marketPrices.add("FRUITS");
        marketPrices.add("Apple - ₹" + getDailyPrice(120, day, 5) + " per kg");
        marketPrices.add("Banana - ₹" + getDailyPrice(40, day, 3) + " per dozen");
        marketPrices.add("Mango - ₹" + getDailyPrice(150, day, 8) + " per kg");
        marketPrices.add("Orange - ₹" + getDailyPrice(80, day, 4) + " per kg");
        marketPrices.add("Grapes - ₹" + getDailyPrice(90, day, 5) + " per kg");
        marketPrices.add("Papaya - ₹" + getDailyPrice(50, day, 3) + " per kg");
        marketPrices.add("Pomegranate - ₹" + getDailyPrice(140, day, 6) + " per kg");
        marketPrices.add("Guava - ₹" + getDailyPrice(60, day, 4) + " per kg");

        // Cereals & Grains
        marketPrices.add("CEREALS & GRAINS");
        marketPrices.add("Wheat - ₹" + getDailyPrice(28, day, 2) + " per kg");
        marketPrices.add("Rice - ₹" + getDailyPrice(35, day, 3) + " per kg");
        marketPrices.add("Maize - ₹" + getDailyPrice(22, day, 2) + " per kg");
        marketPrices.add("Barley - ₹" + getDailyPrice(30, day, 2) + " per kg");
        marketPrices.add("Millet - ₹" + getDailyPrice(32, day, 3) + " per kg");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                marketPrices
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);

                String item = getItem(position);

                if (item.equals("VEGETABLES") ||
                        item.equals("FRUITS") ||
                        item.equals("CEREALS & GRAINS")) {

                    tv.setTextSize(18);
                    tv.setTypeface(null, Typeface.BOLD);
                } else {
                    tv.setTextSize(15);
                    tv.setTypeface(null, Typeface.NORMAL);
                }

                return view;
            }
        };

        listView.setAdapter(adapter);
    }

    private int getDailyPrice(int basePrice, int day, int variation) {
        int change = (day % (variation * 2 + 1)) - variation;
        return basePrice + change;
    }
}