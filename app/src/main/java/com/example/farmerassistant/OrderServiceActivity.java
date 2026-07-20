package com.example.farmerassistant;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmerassistant.service.OrderReminderService;

public class OrderServiceActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnStartOrderService, btnStopOrderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_service);

        btnBack = findViewById(R.id.btnBack);
        btnStartOrderService = findViewById(R.id.btnStartOrderService);
        btnStopOrderService = findViewById(R.id.btnStopOrderService);

        btnBack.setOnClickListener(v -> finish());

        btnStartOrderService.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, OrderReminderService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

            Toast.makeText(this, "Order Reminder Service Started", Toast.LENGTH_SHORT).show();
        });

        btnStopOrderService.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, OrderReminderService.class);
            stopService(serviceIntent);

            Toast.makeText(this, "Order Reminder Service Stopped", Toast.LENGTH_SHORT).show();
        });
    }
}