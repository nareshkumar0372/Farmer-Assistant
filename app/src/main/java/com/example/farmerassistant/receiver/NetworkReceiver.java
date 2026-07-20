package com.example.farmerassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent i){
        Toast.makeText(c,"Network changed",Toast.LENGTH_SHORT).show();
    }
}
