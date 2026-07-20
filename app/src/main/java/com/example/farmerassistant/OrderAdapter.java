package com.example.farmerassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerassistant.database.DBHelper;

import java.util.List;

public class OrderAdapter extends ArrayAdapter<Order> {

    private final Context context;
    private final List<Order> orderList;
    private final DBHelper dbHelper;

    public OrderAdapter(Context context, List<Order> orders) {
        super(context, 0, orders);
        this.context = context;
        this.orderList = orders;
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_order, parent, false);
        }

        Order order = orderList.get(position);

        TextView tvProductName = convertView.findViewById(R.id.tvProductName);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        TextView tvOptions = convertView.findViewById(R.id.tvOptions);
        Button btnCancel = convertView.findViewById(R.id.btnCancelOrder);

        tvProductName.setText(order.getProductName());
        tvQuantity.setText("Quantity: " + order.getQuantity() + " kg");
        tvPrice.setText("Total Price: ₹" + String.format("%.2f", order.getFinalPrice()));

        String options = "Organic: " + (order.isOrganic() ? "Yes" : "No") +
                "\nBulk: " + (order.isBulkOrder() ? "Yes" : "No") +
                "\nHome Delivery: " + (order.isHomeDelivery() ? "Yes" : "No");

        tvOptions.setText(options);

        btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Cancel Order")
                    .setMessage("Are you sure you want to cancel this order?")
                    .setPositiveButton("Yes", (dialog, which) -> cancelOrder(order, position))
                    .setNegativeButton("No", null)
                    .show();
        });

        return convertView;
    }

    private void cancelOrder(Order order, int position) {
        SQLiteDatabase readDb = dbHelper.getReadableDatabase();
        SQLiteDatabase writeDb = dbHelper.getWritableDatabase();

        int productId = -1;
        int orderedQty = order.getQuantity();

        Cursor orderCursor = null;
        Cursor productCursor = null;
        Cursor detailCursor = null;

        try {
            // 1. Get productId from orders table using orderId
            orderCursor = readDb.rawQuery(
                    "SELECT " + DBHelper.COL_ORDER_PRODUCT_ID +
                            " FROM " + DBHelper.TABLE_ORDERS +
                            " WHERE " + DBHelper.COL_ORDER_ID + "=?",
                    new String[]{String.valueOf(order.getId())}
            );

            if (orderCursor.moveToFirst()) {
                productId = orderCursor.getInt(
                        orderCursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_PRODUCT_ID)
                );
            }

            if (productId == -1) {
                Toast.makeText(context, "Unable to restore stock", Toast.LENGTH_LONG).show();
                return;
            }

            // 2. Restore correct product quantity using productId
            productCursor = readDb.rawQuery(
                    "SELECT " + DBHelper.COL_QUANTITY +
                            " FROM " + DBHelper.TABLE_PRODUCTS +
                            " WHERE " + DBHelper.COL_ID + "=?",
                    new String[]{String.valueOf(productId)}
            );

            if (productCursor.moveToFirst()) {
                String currentQtyText = productCursor.getString(
                        productCursor.getColumnIndexOrThrow(DBHelper.COL_QUANTITY)
                );

                int currentQty;
                try {
                    currentQty = Integer.parseInt(currentQtyText);
                } catch (NumberFormatException e) {
                    currentQty = 0;
                }

                int restoredQty = currentQty + orderedQty;

                writeDb.execSQL(
                        "UPDATE " + DBHelper.TABLE_PRODUCTS +
                                " SET " + DBHelper.COL_QUANTITY + "=? WHERE " + DBHelper.COL_ID + "=?",
                        new Object[]{String.valueOf(restoredQty), productId}
                );
            }

            // 3. Delete related ordered_detail record (best matching latest one)
            detailCursor = readDb.rawQuery(
                    "SELECT " + DBHelper.COL_OD_ID +
                            " FROM " + DBHelper.TABLE_ORDERED_DETAIL +
                            " WHERE " + DBHelper.COL_OD_PRODUCT_ID + "=? " +
                            "AND " + DBHelper.COL_OD_QUANTITY + "=? " +
                            "AND " + DBHelper.COL_OD_TOTAL + "=? " +
                            " ORDER BY " + DBHelper.COL_OD_ID + " DESC LIMIT 1",
                    new String[]{
                            String.valueOf(productId),
                            String.valueOf(order.getQuantity()),
                            String.valueOf(order.getFinalPrice())
                    }
            );

            if (detailCursor.moveToFirst()) {
                int orderedDetailId = detailCursor.getInt(
                        detailCursor.getColumnIndexOrThrow(DBHelper.COL_OD_ID)
                );

                writeDb.delete(
                        DBHelper.TABLE_ORDERED_DETAIL,
                        DBHelper.COL_OD_ID + "=?",
                        new String[]{String.valueOf(orderedDetailId)}
                );
            }

            // 4. Delete order from orders table
            writeDb.delete(
                    DBHelper.TABLE_ORDERS,
                    DBHelper.COL_ORDER_ID + "=?",
                    new String[]{String.valueOf(order.getId())}
            );

            // 5. Remove from current list
            orderList.remove(position);
            notifyDataSetChanged();

            Toast.makeText(context,
                    "Order Cancelled & Stock Restored",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(context,
                    "Error while cancelling order",
                    Toast.LENGTH_LONG).show();
        } finally {
            if (orderCursor != null) orderCursor.close();
            if (productCursor != null) productCursor.close();
            if (detailCursor != null) detailCursor.close();
        }
    }
}