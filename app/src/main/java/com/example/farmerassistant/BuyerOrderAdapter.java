package com.example.farmerassistant;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BuyerOrderAdapter extends RecyclerView.Adapter<BuyerOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<BuyerOrderModel> orderList;

    public BuyerOrderAdapter(Context context, List<BuyerOrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_buyer_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        BuyerOrderModel order = orderList.get(position);

        holder.txtBuyerName.setText(order.getBuyerName());
        holder.txtProduct.setText("Product: " + order.getProductName());
        holder.txtQuantity.setText("Quantity: " + order.getQuantity());
        holder.txtTotal.setText("Total: ₹" + String.format("%.2f", order.getTotal()));
        holder.txtLatitude.setText("Lat: " + String.format("%.4f", order.getLatitude()));
        holder.txtLongitude.setText("Lng: " + String.format("%.4f", order.getLongitude()));

        holder.btnViewMap.setOnClickListener(v -> {
            double farmerLat = 13.0827;
            double farmerLng = 80.2707;

            double buyerLat = order.getLatitude();
            double buyerLng = order.getLongitude();

            Log.d("MAP_DEBUG", "Farmer LatLng: " + farmerLat + ", " + farmerLng);
            Log.d("MAP_DEBUG", "Buyer LatLng: " + buyerLat + ", " + buyerLng);

            if (!isValidLatitude(buyerLat) || !isValidLongitude(buyerLng)) {
                Toast.makeText(context, "Invalid buyer location", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("farmerLat", farmerLat);
            intent.putExtra("farmerLng", farmerLng);
            intent.putExtra("buyerLat", buyerLat);
            intent.putExtra("buyerLng", buyerLng);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private boolean isValidLatitude(double lat) {
        return lat >= -90 && lat <= 90;
    }

    private boolean isValidLongitude(double lng) {
        return lng >= -180 && lng <= 180;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView txtBuyerName, txtProduct, txtQuantity, txtTotal, txtLatitude, txtLongitude;
        Button btnViewMap;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtBuyerName = itemView.findViewById(R.id.txtBuyerName);
            txtProduct = itemView.findViewById(R.id.txtProduct);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtLatitude = itemView.findViewById(R.id.txtLatitude);
            txtLongitude = itemView.findViewById(R.id.txtLongitude);
            btnViewMap = itemView.findViewById(R.id.btnViewMap);
        }
    }
}