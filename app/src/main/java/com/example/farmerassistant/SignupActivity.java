package com.example.farmerassistant;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.farmerassistant.database.DBHelper;

public class SignupActivity extends AppCompatActivity {

    EditText etUser, etPass, etConfirmPass;
    RadioGroup roleGroup;
    RadioButton radioFarmer, radioBuyer;
    Button btnSignup;
    TextView tvGoLogin;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);

        roleGroup = findViewById(R.id.roleGroup);
        radioFarmer = findViewById(R.id.radioFarmer);
        radioBuyer = findViewById(R.id.radioBuyer);

        btnSignup = findViewById(R.id.btnSignup);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        db = new DBHelper(this);

        btnSignup.setOnClickListener(v -> registerUser());

        tvGoLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {

        String username = etUser.getText().toString().trim();
        String password = etPass.getText().toString().trim();
        String confirmPassword = etConfirmPass.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (roleGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select role", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = radioFarmer.isChecked() ? "farmer" : "buyer";

        // Insert user
        boolean inserted = db.insertUser(username, password, role);

        if (inserted) {
            Toast.makeText(this, "✅ Registration Successful", Toast.LENGTH_SHORT).show();
            finish(); // go back to login
        } else {
            Toast.makeText(this, "❌ Username already exists", Toast.LENGTH_SHORT).show();
        }
    }
}