package com.example.farmerassistant;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private MapView map;
    private double farmerLat;
    private double farmerLng;
    private double buyerLat;
    private double buyerLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        farmerLat = getIntent().getDoubleExtra("farmerLat", 13.0827);
        farmerLng = getIntent().getDoubleExtra("farmerLng", 80.2707);
        buyerLat = getIntent().getDoubleExtra("buyerLat", 0.0);
        buyerLng = getIntent().getDoubleExtra("buyerLng", 0.0);

        Log.d("MAP_DEBUG", "Farmer = " + farmerLat + ", " + farmerLng);
        Log.d("MAP_DEBUG", "Buyer  = " + buyerLat + ", " + buyerLng);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);

        loadMapSafely();
    }

    private void loadMapSafely() {
        try {
            if (!isValidLatitude(farmerLat) || !isValidLongitude(farmerLng)) {
                Toast.makeText(this, "Invalid farmer location", Toast.LENGTH_SHORT).show();
                return;
            }

            GeoPoint farmerPoint = new GeoPoint(farmerLat, farmerLng);

            Marker farmerMarker = new Marker(map);
            farmerMarker.setPosition(farmerPoint);
            farmerMarker.setTitle("Farmer Location");
            farmerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(farmerMarker);

            boolean isBuyerValid = isValidLatitude(buyerLat)
                    && isValidLongitude(buyerLng)
                    && !(buyerLat == 0.0 && buyerLng == 0.0);

            if (isBuyerValid) {
                GeoPoint buyerPoint = new GeoPoint(buyerLat, buyerLng);

                Marker buyerMarker = new Marker(map);
                buyerMarker.setPosition(buyerPoint);
                buyerMarker.setTitle("Buyer Location");
                buyerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(buyerMarker);

                Polyline line = new Polyline();
                List<GeoPoint> points = new ArrayList<>();
                points.add(farmerPoint);
                points.add(buyerPoint);
                line.setPoints(points);
                line.setWidth(5f);
                map.getOverlays().add(line);

                map.post(() -> {
                    try {
                        BoundingBox box = BoundingBox.fromGeoPoints(points);
                        map.zoomToBoundingBox(box, true, 120);
                        map.invalidate();
                    } catch (Exception e) {
                        Log.e("MAP_DEBUG", "BoundingBox error: " + e.getMessage(), e);
                        map.getController().setZoom(14.0);
                        map.getController().setCenter(farmerPoint);
                    }
                });

            } else {
                map.post(() -> {
                    map.getController().setZoom(15.0);
                    map.getController().setCenter(farmerPoint);
                    map.invalidate();
                });
                Toast.makeText(this, "Buyer location not available", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("MAP_DEBUG", "Map loading failed: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading map", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidLatitude(double lat) {
        return lat >= -90 && lat <= 90;
    }

    private boolean isValidLongitude(double lng) {
        return lng >= -180 && lng <= 180;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }
}