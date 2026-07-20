package com.example.farmerassistant;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.farmerassistant.database.DBHelper;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private List<Product> productList;
    private DBHelper dbHelper;

    public ProductAdapter(Context context, List<Product> list) {
        super(context, 0, list);
        this.context = context;
        this.productList = list;
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_product, parent, false);
        }

        Product product = productList.get(position);

        ImageView img = convertView.findViewById(R.id.imgProduct);
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvCategory = convertView.findViewById(R.id.tvCategory);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        TextView tvExtra = convertView.findViewById(R.id.tvExtra);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);

        tvName.setText(product.getName());
        tvCategory.setText("Category: " + product.getCategory());
        tvQuantity.setText("Quantity: " + product.getQuantity() + " kg");
        tvPrice.setText("Price: ₹" + product.getPrice());

        String extras = "";
        if (product.isOrganic()) extras += "Organic, ";
        if (product.isBulkOrder()) extras += "Bulk Order, ";
        if (product.isHomeDelivery()) extras += "Home Delivery, ";
        if (!extras.isEmpty()) extras = extras.substring(0, extras.length() - 2);
        tvExtra.setText(extras);

        byte[] imageBytes = product.getImage();
        if (imageBytes != null && imageBytes.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            img.setImageBitmap(bitmap);
        }

        // ================= DELETE =================
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        int rows = dbHelper.getWritableDatabase().delete(
                                DBHelper.TABLE_PRODUCTS,
                                DBHelper.COL_ID + "=?",
                                new String[]{String.valueOf(product.getId())}
                        );

                        if (rows > 0) {
                            productList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Product Deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // ================= EDIT =================
        btnEdit.setOnClickListener(v -> {

            View dialogView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_edit_product, null);

            EditText etName = dialogView.findViewById(R.id.etEditName);
            EditText etQuantity = dialogView.findViewById(R.id.etEditQuantity);
            EditText etPrice = dialogView.findViewById(R.id.etEditPrice);
            EditText etCategory = dialogView.findViewById(R.id.etEditCategory);
            CheckBox chkOrganic = dialogView.findViewById(R.id.chkOrganic);
            CheckBox chkBulk = dialogView.findViewById(R.id.chkBulk);
            CheckBox chkDelivery = dialogView.findViewById(R.id.chkDelivery);

            etName.setText(product.getName());
            etQuantity.setText(product.getQuantity());
            etPrice.setText(product.getPrice());
            etCategory.setText(product.getCategory());
            chkOrganic.setChecked(product.isOrganic());
            chkBulk.setChecked(product.isBulkOrder());
            chkDelivery.setChecked(product.isHomeDelivery());

            new AlertDialog.Builder(context)
                    .setTitle("Edit Product")
                    .setView(dialogView)
                    .setPositiveButton("Update", (dialog, which) -> {

                        android.content.ContentValues values = new android.content.ContentValues();
                        values.put(DBHelper.COL_NAME, etName.getText().toString());
                        values.put(DBHelper.COL_QUANTITY, etQuantity.getText().toString());
                        values.put(DBHelper.COL_PRICE, etPrice.getText().toString());
                        values.put(DBHelper.COL_CATEGORY, etCategory.getText().toString());
                        values.put(DBHelper.COL_ORGANIC, chkOrganic.isChecked() ? 1 : 0);
                        values.put(DBHelper.COL_BULK, chkBulk.isChecked() ? 1 : 0);
                        values.put(DBHelper.COL_DELIVERY, chkDelivery.isChecked() ? 1 : 0);

                        int rows = dbHelper.getWritableDatabase().update(
                                DBHelper.TABLE_PRODUCTS,
                                values,
                                DBHelper.COL_ID + "=?",
                                new String[]{String.valueOf(product.getId())}
                        );

                        if (rows > 0) {
                            productList.set(position, new Product(
                                    product.getId(),
                                    etName.getText().toString(),
                                    etQuantity.getText().toString(),
                                    etPrice.getText().toString(),
                                    etCategory.getText().toString(),
                                    product.getImage(),
                                    chkOrganic.isChecked(),
                                    chkBulk.isChecked(),
                                    chkDelivery.isChecked()
                            ));
                            notifyDataSetChanged();
                            Toast.makeText(context, "Product Updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }
}