package com.example.farmerassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmerassistant.database.DBHelper;

import java.util.ArrayList;

public class BuyerOrderedDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private ImageButton btnBack;
    private DBHelper dbHelper;

    private ArrayList<BuyerOrderModel> orderList;
    private BuyerOrderAdapter adapter;

    private int farmerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_ordered_details);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DBHelper(this);
        orderList = new ArrayList<>();

        // Get logged-in farmer id
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        farmerId = preferences.getInt("farmerId", -1);

        if (farmerId == -1) {
            Toast.makeText(this, "Farmer session not found. Please login again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        recyclerOrders.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new BuyerOrderAdapter(this, orderList);
        recyclerOrders.setAdapter(adapter);

        loadOrdersFromDatabase();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadOrdersFromDatabase() {
        orderList.clear();

        Cursor cursor = dbHelper.getOrdersByFarmer(farmerId);

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No Orders Available", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {

            String buyerName = cursor.getString(
                    cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_BUYER_NAME));

            String product = cursor.getString(
                    cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_PRODUCT_NAME));

            int quantity = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_QUANTITY));

            double total = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_FINAL_PRICE));

            double latitude = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_LAT));

            double longitude = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_LNG));

            Log.d("DB_COORDS", "Buyer: " + buyerName + " -> Lat: " + latitude + ", Lng: " + longitude);

            orderList.add(new BuyerOrderModel(
                    buyerName,
                    product,
                    quantity,
                    total,
                    latitude,
                    longitude
            ));
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}