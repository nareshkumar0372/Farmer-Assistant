package com.example.farmerassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;        // ✅ ADD THIS
import android.widget.Toast;

public class ProductAddedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("PRODUCT_ADDED".equals(intent.getAction())) {

            String name = intent.getStringExtra("product");

            // ✅ LOG MESSAGE
            Log.d("RECEIVER_TEST", "Product added broadcast received");

            Toast.makeText(context,
                    "Receiver: Product Added → " + name,
                    Toast.LENGTH_LONG).show();
        }
    }
}