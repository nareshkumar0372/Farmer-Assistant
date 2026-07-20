package com.example.farmerassistant;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmerassistant.receiver.NetworkReceiver;
import com.example.farmerassistant.service.AppMonitorService;
import com.example.farmerassistant.service.MarketPriceService;

public class MainActivity extends AppCompatActivity {

    private Button btnSell, btnMarketPrice, btnMyProducts, btnBuyerOrders;
    private Button btnOrderService, btnLogout;

    private NetworkReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSell = findViewById(R.id.btnSell);
        btnMarketPrice = findViewById(R.id.btnMarketPrice);
        btnMyProducts = findViewById(R.id.btnMyProducts);
        btnBuyerOrders = findViewById(R.id.btnBuyerOrders);
        btnOrderService = findViewById(R.id.btnOrderService);
        btnLogout = findViewById(R.id.btnLogout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    1
            );
        }

        Intent marketServiceIntent = new Intent(this, MarketPriceService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(marketServiceIntent);
        } else {
            startService(marketServiceIntent);
        }

        Intent monitorIntent = new Intent(this, AppMonitorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(monitorIntent);
        } else {
            startService(monitorIntent);
        }

        btnSell.setOnClickListener(v ->
                startActivity(new Intent(this, SellProductActivity.class)));

        btnMarketPrice.setOnClickListener(v ->
                startActivity(new Intent(this, MarketPriceActivity.class)));

        btnMyProducts.setOnClickListener(v ->
                startActivity(new Intent(this, MyProductsActivity.class)));

        btnBuyerOrders.setOnClickListener(v ->
                startActivity(new Intent(this, BuyerOrderedDetailsActivity.class)));

        btnOrderService.setOnClickListener(v ->
                startActivity(new Intent(this, OrderServiceActivity.class)));

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        networkReceiver = new NetworkReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(networkReceiver);
        } catch (Exception ignored) {
        }
    }
}