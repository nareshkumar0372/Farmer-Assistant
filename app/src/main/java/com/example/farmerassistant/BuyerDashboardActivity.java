package com.example.farmerassistant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class BuyerDashboardActivity extends AppCompatActivity {

    Button btnBrowseProducts, btnViewMarket, btnMyOrders, btnBackLogin;
    RatingBar appRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_dashboard);

        // Initialize buttons
        btnBrowseProducts = findViewById(R.id.btnBrowseProducts);
        btnViewMarket = findViewById(R.id.btnViewMarket);
        btnMyOrders = findViewById(R.id.btnMyOrders);
        btnBackLogin = findViewById(R.id.btnBackLogin);
        appRating = findViewById(R.id.appRating);

        // ✅ Open Browse Products
        btnBrowseProducts.setOnClickListener(v ->
                startActivity(new Intent(this, BrowseProductsActivity.class)));

        // ✅ Open Market Price
        btnViewMarket.setOnClickListener(v ->
                startActivity(new Intent(this, MarketPriceActivity.class)));

        // ✅ Open My Orders
        btnMyOrders.setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));

        // ✅ Back to Login
        btnBackLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        // ⭐ Allow float ratings (make sure stepSize="0.5" in XML)
        appRating.setStepSize(0.5f);

        // ⭐ Rating Listener
        appRating.setOnRatingBarChangeListener((ratingBar, value, fromUser) -> {

            if (fromUser) {

                String message;

                // If whole number
                if (value % 1 == 0) {
                    message = "Thanks for rating: " + (int) value;
                } else {
                    // Format float properly (e.g., 4.5)
                    DecimalFormat df = new DecimalFormat("#.#");
                    message = "Thanks for rating: " + df.format(value);
                }

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}