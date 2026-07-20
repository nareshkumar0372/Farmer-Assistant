package com.example.farmerassistant;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.farmerassistant.database.DBHelper;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class SellProductActivity extends AppCompatActivity {

    private EditText etName, etQty, etPrice;
    private ImageView imgProduct;
    private ImageButton btnMic;
    private RadioGroup radioGroup;
    private CheckBox chkOrganic, chkBulk, chkHomeDelivery;

    private Bitmap capturedBitmap = null;
    private DBHelper dbHelper;

    private int farmerId = -1;
    private String farmerName = "";

    // ===== Launchers =====

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            openCamera();
                        } else {
                            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                        }
                    });

    private final ActivityResultLauncher<String> audioPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            startVoiceInput();
                        } else {
                            Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
                        }
                    });

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null &&
                                result.getData().getExtras() != null) {

                            Bitmap photo = (Bitmap) result.getData().getExtras().get("data");

                            if (photo != null) {
                                capturedBitmap = photo;
                                imgProduct.setImageBitmap(photo);
                                Toast.makeText(this, "Image captured", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    private final ActivityResultLauncher<Intent> voiceLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            ArrayList<String> resultList =
                                    result.getData().getStringArrayListExtra(
                                            RecognizerIntent.EXTRA_RESULTS);

                            if (resultList != null && !resultList.isEmpty()) {
                                String spokenText = resultList.get(0);
                                etName.setText(spokenText);
                                Toast.makeText(this, "Voice input added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "No speech text detected", Toast.LENGTH_SHORT).show();
                            }

                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            Toast.makeText(this, "Voice input cancelled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Speech recognition failed", Toast.LENGTH_SHORT).show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_product);

        dbHelper = new DBHelper(this);

        etName = findViewById(R.id.etProductName);
        etQty = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        imgProduct = findViewById(R.id.imgProduct);
        btnMic = findViewById(R.id.btnMic);
        radioGroup = findViewById(R.id.radioCategory);
        chkOrganic = findViewById(R.id.chkOrganic);
        chkBulk = findViewById(R.id.chkBulk);
        chkHomeDelivery = findViewById(R.id.chkHomeDelivery);

        // Get logged-in farmer details
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        farmerId = preferences.getInt("farmerId", -1);
        farmerName = preferences.getString("farmerName", "");

        if (farmerId == -1 || farmerName.isEmpty()) {
            Toast.makeText(this, "Farmer session not found. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        findViewById(R.id.btnCamera).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnMic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                startVoiceInput();
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(v -> saveProduct());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void openCamera() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Product Name");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);

        try {
            voiceLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Speech recognition not available on this device",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String qty = etQty.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();

        if (name.isEmpty() || qty.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = findViewById(selectedId);
        String category = selectedRadio.getText().toString();

        boolean isOrganic = chkOrganic.isChecked();
        boolean bulkOrder = chkBulk.isChecked();
        boolean homeDelivery = chkHomeDelivery.isChecked();

        byte[] imageBytes = null;
        if (capturedBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            imageBytes = baos.toByteArray();
        }

        double priceValue;
        try {
            priceValue = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = dbHelper.insertProduct(
                farmerId,
                farmerName,
                name,
                qty,
                priceText,
                category,
                imageBytes,
                isOrganic,
                bulkOrder,
                homeDelivery,
                priceValue,
                0.0,
                0.0
        );

        if (inserted) {
            Toast.makeText(this, "Product Saved Successfully ✅", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Error saving product ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etName.setText("");
        etQty.setText("");
        etPrice.setText("");
        radioGroup.clearCheck();
        chkOrganic.setChecked(false);
        chkBulk.setChecked(false);
        chkHomeDelivery.setChecked(false);
        imgProduct.setImageDrawable(null);
        capturedBitmap = null;
    }
}