package com.example.farmerassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmerassistant.database.DBHelper;

import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Order> orderList;
    private OrderAdapter adapter;
    private ImageButton btnBack;
    private DBHelper dbHelper;

    private int buyerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        listView = findViewById(R.id.listOrders);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DBHelper(this);

        // Get logged-in buyer id
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        buyerId = preferences.getInt("buyerId", -1);

        if (buyerId == -1) {
            Toast.makeText(this, "Buyer session not found. Please login again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MyOrdersActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Back button
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(MyOrdersActivity.this, BuyerDashboardActivity.class));
            finish();
        });

        loadOrders();
    }

    private void loadOrders() {
        orderList = new ArrayList<>();

        Cursor cursor = dbHelper.getOrdersByBuyer(buyerId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_ID));

                String productName = cursor.getString(
                        cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_PRODUCT_NAME));

                int quantity = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_QUANTITY));

                double basePrice = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_BASE_PRICE));

                double finalPrice = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_FINAL_PRICE));

                boolean isOrganic = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_ORGANIC)) == 1;

                boolean bulkOrder = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_BULK)) == 1;

                boolean homeDelivery = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_DELIVERY)) == 1;

                orderList.add(new Order(
                        id,
                        productName,
                        quantity,
                        basePrice,
                        finalPrice,
                        isOrganic,
                        bulkOrder,
                        homeDelivery
                ));

            } while (cursor.moveToNext());

            cursor.close();
        }

        if (orderList.isEmpty()) {
            Toast.makeText(this, "No orders found", Toast.LENGTH_SHORT).show();
        }

        adapter = new OrderAdapter(this, orderList);
        listView.setAdapter(adapter);
    }
}