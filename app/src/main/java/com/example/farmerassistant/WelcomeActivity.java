package com.example.farmerassistant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    Button btnLogin;
    TextView tvDateTime;

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnLogin = findViewById(R.id.btnLogin);
        tvDateTime = findViewById(R.id.tvDateTime);

        // Login button click
        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class)));

        // Live Date & Time updater
        runnable = new Runnable() {
            @Override
            public void run() {

                String currentDateTime = new SimpleDateFormat(
                        "EEEE, dd MMM yyyy  |  hh:mm:ss a",
                        Locale.getDefault()
                ).format(new Date());

                tvDateTime.setText(currentDateTime);

                handler.postDelayed(this, 1000); // update every 1 second
            }
        };

        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}