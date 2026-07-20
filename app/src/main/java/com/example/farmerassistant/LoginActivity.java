package com.example.farmerassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmerassistant.database.DBHelper;
import com.example.farmerassistant.service.AppMonitorService;

public class LoginActivity extends AppCompatActivity {

    EditText etUser, etPass;
    RadioGroup roleGroup;
    RadioButton radioFarmer, radioBuyer;
    Button btnLogin;
    TextView tvGoSignup;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        roleGroup = findViewById(R.id.roleGroup);
        radioFarmer = findViewById(R.id.radioFarmer);
        radioBuyer = findViewById(R.id.radioBuyer);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoSignup = findViewById(R.id.tvGoSignup);

        db = new DBHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());

        tvGoSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }

    private void loginUser() {
        String username = etUser.getText().toString().trim();
        String password = etPass.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (roleGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select role", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedRole = radioFarmer.isChecked() ? "farmer" : "buyer";

        Cursor cursor = db.loginUser(username, password);

        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(this, "❌ Invalid Username or Password", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ID));
        String usernameFromDB = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USERNAME));
        String roleFromDB = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ROLE));
        cursor.close();

        if (!roleFromDB.equals(selectedRole)) {
            Toast.makeText(this, "❌ Role mismatch", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "✅ Login Success", Toast.LENGTH_SHORT).show();

        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("userId", userId);
        editor.putString("username", usernameFromDB);
        editor.putString("role", roleFromDB);

        if (roleFromDB.equals("buyer")) {
            editor.putInt("buyerId", userId);
            editor.putString("buyerName", usernameFromDB);
        }

        if (roleFromDB.equals("farmer")) {
            editor.putInt("farmerId", userId);
            editor.putString("farmerName", usernameFromDB);
        }

        editor.apply();

        if (roleFromDB.equals("farmer")) {

            Intent serviceIntent = new Intent(this, AppMonitorService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

            startActivity(new Intent(this, MainActivity.class));

        } else {
            startActivity(new Intent(this, BuyerDashboardActivity.class));
        }

        finish();
    }
}