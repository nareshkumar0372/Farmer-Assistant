package com.example.farmerassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyProductsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private int farmerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Get logged-in farmer id
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        farmerId = preferences.getInt("farmerId", -1);

        if (farmerId == -1) {
            Toast.makeText(this, "Farmer session not found. Please login again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (savedInstanceState == null) {
            ProductListFragment fragment = new ProductListFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("farmerId", farmerId);
            fragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.productFragmentContainer, fragment)
                    .commit();
        }
    }
}