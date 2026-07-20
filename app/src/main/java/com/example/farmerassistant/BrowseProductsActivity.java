package com.example.farmerassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmerassistant.database.DBHelper;

import java.util.ArrayList;
import java.util.Locale;

public class BrowseProductsActivity extends AppCompatActivity {

    Spinner spinner;
    TextView tvDetails;
    Button btnOrder;
    ImageButton btnSpeak, btnBack;
    CheckBox chkVegetable, chkFruit, chkCereals;

    DBHelper dbHelper;

    // ================= Product Data =================
    ArrayList<Integer> allProductIds = new ArrayList<>();
    ArrayList<Integer> allFarmerIds = new ArrayList<>();
    ArrayList<String> allFarmerNames = new ArrayList<>();

    ArrayList<String> allProductNames = new ArrayList<>();
    ArrayList<String> allProductDetails = new ArrayList<>();
    ArrayList<String> allProductCategories = new ArrayList<>();
    ArrayList<Double> allProductPrices = new ArrayList<>();
    ArrayList<Integer> allProductQuantities = new ArrayList<>();
    ArrayList<Boolean> allOrganic = new ArrayList<>();
    ArrayList<Boolean> allBulk = new ArrayList<>();
    ArrayList<Boolean> allHomeDelivery = new ArrayList<>();

    ArrayList<Integer> filteredProductIds = new ArrayList<>();
    ArrayList<Integer> filteredFarmerIds = new ArrayList<>();
    ArrayList<String> filteredFarmerNames = new ArrayList<>();

    ArrayList<String> filteredNames = new ArrayList<>();
    ArrayList<String> filteredDetails = new ArrayList<>();
    ArrayList<Double> filteredPrices = new ArrayList<>();
    ArrayList<Integer> filteredQuantities = new ArrayList<>();
    ArrayList<Boolean> filteredOrganic = new ArrayList<>();
    ArrayList<Boolean> filteredBulk = new ArrayList<>();
    ArrayList<Boolean> filteredHomeDelivery = new ArrayList<>();

    TextToSpeech tts;

    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "FarmerPrefs";
    private static final String KEY_LAST_FILTERS = "lastFilters";
    private static final String KEY_LAST_PRODUCT = "lastProduct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_products);

        dbHelper = new DBHelper(this);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Initialize Views
        spinner = findViewById(R.id.spinnerProducts);
        tvDetails = findViewById(R.id.tvDetails);
        btnOrder = findViewById(R.id.btnOrder);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnBack = findViewById(R.id.btnBack);

        chkVegetable = findViewById(R.id.chkVegetable);
        chkFruit = findViewById(R.id.chkFruit);
        chkCereals = findViewById(R.id.chkCereals);

        // Load products from SQLite DB
        loadProducts();

        if (allProductNames.isEmpty()) {
            Toast.makeText(this, "No products available", Toast.LENGTH_SHORT).show();
        }

        // Restore last saved filters
        String savedFilters = sharedPreferences.getString(KEY_LAST_FILTERS, "");
        chkVegetable.setChecked(savedFilters.contains("Vegetable"));
        chkFruit.setChecked(savedFilters.contains("Fruit"));
        chkCereals.setChecked(savedFilters.contains("Cereals"));

        // Apply filters after restoring checkboxes
        applyFilters();

        // Restore last selected product
        String lastProduct = sharedPreferences.getString(KEY_LAST_PRODUCT, "");
        if (!lastProduct.isEmpty() && filteredNames.contains(lastProduct)) {
            spinner.setSelection(filteredNames.indexOf(lastProduct));
        }

        // Spinner listener
        spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent,
                                       android.view.View view,
                                       int position,
                                       long id) {
                if (!filteredDetails.isEmpty() && position < filteredDetails.size()) {
                    tvDetails.setText(filteredDetails.get(position));
                    savePreferences();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                tvDetails.setText("");
            }
        });

        // Initialize TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.getDefault());
            }
        });

        // Speak Button
        btnSpeak.setOnClickListener(v -> {
            String text = tvDetails.getText().toString();
            if (!text.isEmpty()) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Toast.makeText(this, "No product selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Order Button
        btnOrder.setOnClickListener(v -> {
            int position = spinner.getSelectedItemPosition();

            if (position == Spinner.INVALID_POSITION || filteredDetails.isEmpty()) {
                Toast.makeText(this, "Please select a product", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, OrderConfirmActivity.class);
            intent.putExtra("productId", filteredProductIds.get(position));
            intent.putExtra("productName", filteredNames.get(position));
            intent.putExtra("quantity", filteredQuantities.get(position));
            intent.putExtra("basePrice", filteredPrices.get(position));
            intent.putExtra("isOrganic", filteredOrganic.get(position));
            intent.putExtra("bulkOrder", filteredBulk.get(position));
            intent.putExtra("homeDelivery", filteredHomeDelivery.get(position));

            // Multi-user support
            intent.putExtra("farmerId", filteredFarmerIds.get(position));
            intent.putExtra("farmerName", filteredFarmerNames.get(position));

            startActivity(intent);
        });

        // Back button
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Filter CheckBox listeners
        chkVegetable.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
        chkFruit.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
        chkCereals.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
    }

    // ================= LOAD PRODUCTS =================
    private void loadProducts() {
        Cursor cursor = dbHelper.getAllProducts();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int productId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID));
                int farmerId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_PRODUCT_FARMER_ID));
                String farmerName = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PRODUCT_FARMER_USERNAME));

                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NAME));

                String quantityText = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_QUANTITY));
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityText);
                } catch (NumberFormatException e) {
                    quantity = 0;
                }

                String priceText = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PRICE));
                double price;
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    price = 0.0;
                }

                String category = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CATEGORY));
                boolean isOrganic = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ORGANIC)) == 1;
                boolean bulkOrder = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_BULK)) == 1;
                boolean homeDelivery = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_DELIVERY)) == 1;

                allProductIds.add(productId);
                allFarmerIds.add(farmerId);
                allFarmerNames.add(farmerName);

                allProductNames.add(name);
                allProductQuantities.add(quantity);
                allProductPrices.add(price);
                allProductCategories.add(category);
                allOrganic.add(isOrganic);
                allBulk.add(bulkOrder);
                allHomeDelivery.add(homeDelivery);

                allProductDetails.add(
                        "Product: " + name +
                                "\nFarmer: " + farmerName +
                                "\nCategory: " + category +
                                "\nAvailable Quantity: " + quantity + " kg" +
                                "\nPrice: ₹" + price +
                                "\nOrganic: " + (isOrganic ? "Yes" : "No") +
                                "\nBulk Order: " + (bulkOrder ? "Yes" : "No") +
                                "\nHome Delivery: " + (homeDelivery ? "Yes" : "No")
                );

            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    // ================= APPLY FILTERS =================
    private void applyFilters() {
        filteredProductIds.clear();
        filteredFarmerIds.clear();
        filteredFarmerNames.clear();

        filteredNames.clear();
        filteredDetails.clear();
        filteredPrices.clear();
        filteredQuantities.clear();
        filteredOrganic.clear();
        filteredBulk.clear();
        filteredHomeDelivery.clear();

        boolean veg = chkVegetable.isChecked();
        boolean fruit = chkFruit.isChecked();
        boolean cereals = chkCereals.isChecked();

        for (int i = 0; i < allProductNames.size(); i++) {
            String category = allProductCategories.get(i);

            boolean matches =
                    (veg && category.equalsIgnoreCase("Vegetable")) ||
                            (fruit && category.equalsIgnoreCase("Fruit")) ||
                            (cereals && category.equalsIgnoreCase("Cereals")) ||
                            (!veg && !fruit && !cereals);

            if (matches) {
                filteredProductIds.add(allProductIds.get(i));
                filteredFarmerIds.add(allFarmerIds.get(i));
                filteredFarmerNames.add(allFarmerNames.get(i));

                filteredNames.add(allProductNames.get(i));
                filteredDetails.add(allProductDetails.get(i));
                filteredPrices.add(allProductPrices.get(i));
                filteredQuantities.add(allProductQuantities.get(i));
                filteredOrganic.add(allOrganic.get(i));
                filteredBulk.add(allBulk.get(i));
                filteredHomeDelivery.add(allHomeDelivery.get(i));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                filteredNames
        );

        spinner.setAdapter(adapter);

        if (!filteredDetails.isEmpty()) {
            tvDetails.setText(filteredDetails.get(0));
        } else {
            tvDetails.setText("No products available for selected category.");
        }

        savePreferences();
    }

    // ================= SAVE PREFERENCES =================
    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String selectedFilters = "";
        if (chkVegetable.isChecked()) selectedFilters += "Vegetable,";
        if (chkFruit.isChecked()) selectedFilters += "Fruit,";
        if (chkCereals.isChecked()) selectedFilters += "Cereals,";

        editor.putString(KEY_LAST_FILTERS, selectedFilters);

        int selectedPos = spinner.getSelectedItemPosition();
        if (selectedPos != Spinner.INVALID_POSITION && !filteredNames.isEmpty()) {
            editor.putString(KEY_LAST_PRODUCT, filteredNames.get(selectedPos));
        }

        editor.apply();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}