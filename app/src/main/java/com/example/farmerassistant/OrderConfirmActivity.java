package com.example.farmerassistant;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.farmerassistant.database.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class OrderConfirmActivity extends AppCompatActivity {

    TextView tvProductDetails;
    EditText etQuantity;
    Button btnConfirmOrder;
    ImageButton btnBack;

    int productId;
    String productName;
    int originalQuantity;
    double basePrice;
    boolean isOrganic;
    boolean bulkOrder;
    boolean homeDelivery;

    int farmerId;
    String farmerName;

    DBHelper dbHelper;

    // ================= LOCATION =================
    private FusedLocationProviderClient fusedLocationClient;
    private double buyerLat = 0.0;
    private double buyerLng = 0.0;

    // Example farmer location
    private double farmerLat = 13.0827;
    private double farmerLng = 80.2707;

    private double distanceKm = 0.0;
    private double distanceDeliveryCharge = 0.0;

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        Boolean fineGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                        if (fineGranted != null && fineGranted) {
                            getUserLocation();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        tvProductDetails = findViewById(R.id.tvProductDetails);
        etQuantity = findViewById(R.id.etQuantity);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DBHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Receive product info
        productId = getIntent().getIntExtra("productId", -1);
        productName = getIntent().getStringExtra("productName");
        originalQuantity = getIntent().getIntExtra("quantity", 0);
        basePrice = getIntent().getDoubleExtra("basePrice", 0.0);
        isOrganic = getIntent().getBooleanExtra("isOrganic", false);
        bulkOrder = getIntent().getBooleanExtra("bulkOrder", false);
        homeDelivery = getIntent().getBooleanExtra("homeDelivery", false);

        // Receive farmer info
        farmerId = getIntent().getIntExtra("farmerId", -1);
        farmerName = getIntent().getStringExtra("farmerName");

        if (productId == -1 || farmerId == -1 || productName == null || farmerName == null) {
            Toast.makeText(this, "Invalid product or farmer data", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvProductDetails.setText(
                "Product: " + productName +
                        "\nFarmer: " + farmerName +
                        "\nAvailable Quantity: " + originalQuantity + " kg" +
                        "\nBase Price: ₹" + basePrice +
                        "\nOrganic: " + (isOrganic ? "Yes" : "No") +
                        "\nBulk Order: " + (bulkOrder ? "Yes" : "No") +
                        "\nHome Delivery: " + (homeDelivery ? "Yes" : "No")
        );

        checkLocationPermission();

        btnConfirmOrder.setOnClickListener(v -> showConfirmationDialog());
        btnBack.setOnClickListener(v -> finish());
    }

    // ================= LOCATION =================

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            getUserLocation();
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        buyerLat = location.getLatitude();
                        buyerLng = location.getLongitude();
                        calculateDistance();
                    }
                });
    }

    private void calculateDistance() {
        Location farmer = new Location("Farmer");
        farmer.setLatitude(farmerLat);
        farmer.setLongitude(farmerLng);

        Location buyer = new Location("Buyer");
        buyer.setLatitude(buyerLat);
        buyer.setLongitude(buyerLng);

        float meters = farmer.distanceTo(buyer);
        distanceKm = meters / 1000.0;

        if (distanceKm <= 5) {
            distanceDeliveryCharge = 20;
        } else if (distanceKm <= 10) {
            distanceDeliveryCharge = 40;
        } else {
            distanceDeliveryCharge = 60;
        }
    }

    // ================= CONFIRMATION =================

    private void showConfirmationDialog() {
        String quantityStr = etQuantity.getText().toString().trim();

        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int orderQuantity;
        try {
            orderQuantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderQuantity <= 0) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderQuantity > originalQuantity) {
            Toast.makeText(this, "Ordered quantity exceeds available stock", Toast.LENGTH_SHORT).show();
            return;
        }

        double finalPrice = basePrice * orderQuantity;

        if (isOrganic) finalPrice *= 1.15;
        if (bulkOrder && orderQuantity >= 50) finalPrice *= 0.95;
        if (homeDelivery) finalPrice += 50;

        finalPrice += distanceDeliveryCharge;

        String message =
                "Product: " + productName +
                        "\nFarmer: " + farmerName +
                        "\nOrder Quantity: " + orderQuantity + " kg" +
                        "\nDistance: " + String.format("%.2f", distanceKm) + " km" +
                        "\nDelivery Charge: ₹" + distanceDeliveryCharge +
                        "\nTotal Price: ₹" + String.format("%.2f", finalPrice);

        final int finalOrderQuantity = orderQuantity;
        final double finalOrderPrice = finalPrice;

        new AlertDialog.Builder(this)
                .setTitle("🛒 Confirm Your Order")
                .setMessage(message)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Confirm Order", (dialog, which) -> {
                    boolean inserted = saveOrderToDB(finalOrderQuantity, finalOrderPrice);

                    if (inserted) {
                        Toast.makeText(this,
                                "✅ Order Successfully Placed!",
                                Toast.LENGTH_LONG).show();

                        // Optional: open external map app
                        // openMap();

                        finish();
                    } else {
                        Toast.makeText(this,
                                "❌ Failed to place order",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    // ================= SAVE ORDER =================

    private boolean saveOrderToDB(int orderQuantity, double finalPrice) {

        SharedPreferences preferences =
                getSharedPreferences("UserData", MODE_PRIVATE);

        int buyerId = preferences.getInt("buyerId", -1);
        String buyerName = preferences.getString("buyerName", "Unknown");

        if (buyerId == -1) {
            Toast.makeText(this, "Buyer session not found. Please login again.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Save in orders table + Firebase
        boolean orderInserted = dbHelper.insertOrder(
                buyerId,
                buyerName,
                farmerId,
                farmerName,
                productId,
                productName,
                orderQuantity,
                basePrice,
                finalPrice,
                isOrganic,
                bulkOrder,
                homeDelivery,
                buyerLat,
                buyerLng
        );

        if (!orderInserted) return false;

        // Save in ordered detail table + Firebase
        boolean detailInserted = dbHelper.insertOrderedDetail(
                buyerId,
                buyerName,
                farmerId,
                farmerName,
                productId,
                productName,
                orderQuantity,
                finalPrice,
                buyerLat,
                buyerLng
        );

        if (!detailInserted) return false;

        // Update product quantity in SQLite
        int newQuantity = originalQuantity - orderQuantity;
        if (newQuantity < 0) newQuantity = 0;

        ContentValues updateProduct = new ContentValues();
        updateProduct.put(DBHelper.COL_QUANTITY, String.valueOf(newQuantity));

        int rowsAffected = dbHelper.getWritableDatabase().update(
                DBHelper.TABLE_PRODUCTS,
                updateProduct,
                DBHelper.COL_ID + "=?",
                new String[]{String.valueOf(productId)}
        );

        return rowsAffected > 0;
    }

    // ================= OPTIONAL MAP =================
    private void openMap() {
        String uri = "https://www.google.com/maps/dir/?api=1&origin="
                + farmerLat + "," + farmerLng
                + "&destination=" + buyerLat + "," + buyerLng
                + "&travelmode=driving";

        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Google Maps not available", Toast.LENGTH_SHORT).show();
        }
    }
}