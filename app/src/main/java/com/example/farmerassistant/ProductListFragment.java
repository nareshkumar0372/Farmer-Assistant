package com.example.farmerassistant;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmerassistant.database.DBHelper;

import java.util.ArrayList;

public class ProductListFragment extends Fragment {

    private ListView listView;
    private ArrayList<Product> productList;
    private ProductAdapter adapter;
    private DBHelper dbHelper;

    private int farmerId = -1;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listProductsFragment);
        dbHelper = new DBHelper(requireContext());

        if (getArguments() != null) {
            farmerId = getArguments().getInt("farmerId", -1);
        }

        if (farmerId == -1) {
            Toast.makeText(requireContext(),
                    "Farmer information not found",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        loadProducts();
    }

    private void loadProducts() {
        productList = new ArrayList<>();

        Cursor cursor = dbHelper.getProductsByFarmer(farmerId);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_ID));

                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_NAME));

                    String quantity = cursor.getString(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_QUANTITY));

                    String price = cursor.getString(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_PRICE));

                    String category = cursor.getString(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_CATEGORY));

                    byte[] image = cursor.getBlob(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_IMAGE));

                    boolean isOrganic = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_ORGANIC)) == 1;

                    boolean bulkOrder = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_BULK)) == 1;

                    boolean homeDelivery = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DBHelper.COL_DELIVERY)) == 1;

                    Product product = new Product(
                            id,
                            name,
                            quantity,
                            price,
                            category,
                            image,
                            isOrganic,
                            bulkOrder,
                            homeDelivery
                    );

                    productList.add(product);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        if (productList.isEmpty()) {
            Toast.makeText(requireContext(),
                    "No products found for this farmer",
                    Toast.LENGTH_SHORT).show();
        }

        adapter = new ProductAdapter(requireContext(), productList);
        listView.setAdapter(adapter);
    }
}