package com.example.farmerassistant.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FarmerDB";
    private static final int DATABASE_VERSION = 11;

    private static final String TAG = "DBHelper";
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // =================== TABLE NAMES ===================
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDERED_DETAIL = "ordered_detail";

    // =================== USERS COLUMNS ===================
    public static final String COL_USER_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ROLE = "role";

    // =================== PRODUCTS COLUMNS ===================
    public static final String COL_ID = "_id";
    public static final String COL_PRODUCT_FARMER_ID = "farmerId";
    public static final String COL_PRODUCT_FARMER_USERNAME = "farmerUsername";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_PRICE = "price";
    public static final String COL_CATEGORY = "category";
    public static final String COL_IMAGE = "image";
    public static final String COL_ORGANIC = "isOrganic";
    public static final String COL_BULK = "bulkOrder";
    public static final String COL_DELIVERY = "homeDelivery";
    public static final String COL_FINAL_PRICE = "finalPrice";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";

    // =================== ORDERS COLUMNS ===================
    public static final String COL_ORDER_ID = "orderId";
    public static final String COL_ORDER_BUYER_ID = "buyerId";
    public static final String COL_ORDER_BUYER_NAME = "buyerName";
    public static final String COL_ORDER_FARMER_ID = "farmerId";
    public static final String COL_ORDER_FARMER_NAME = "farmerName";
    public static final String COL_ORDER_PRODUCT_ID = "productId";
    public static final String COL_ORDER_PRODUCT_NAME = "productName";
    public static final String COL_ORDER_QUANTITY = "quantity";
    public static final String COL_ORDER_BASE_PRICE = "basePrice";
    public static final String COL_ORDER_FINAL_PRICE = "finalPrice";
    public static final String COL_ORDER_ORGANIC = "isOrganic";
    public static final String COL_ORDER_BULK = "bulkOrder";
    public static final String COL_ORDER_DELIVERY = "homeDelivery";
    public static final String COL_ORDER_LAT = "buyerLatitude";
    public static final String COL_ORDER_LNG = "buyerLongitude";

    // =================== ORDERED DETAIL COLUMNS ===================
    public static final String COL_OD_ID = "odId";
    public static final String COL_OD_BUYER_ID = "buyerId";
    public static final String COL_OD_BUYER_NAME = "buyerName";
    public static final String COL_OD_FARMER_ID = "farmerId";
    public static final String COL_OD_FARMER_NAME = "farmerName";
    public static final String COL_OD_PRODUCT_ID = "productId";
    public static final String COL_OD_PRODUCT_NAME = "productName";
    public static final String COL_OD_QUANTITY = "quantity";
    public static final String COL_OD_TOTAL = "total";
    public static final String COL_OD_LAT = "latitude";
    public static final String COL_OD_LNG = "longitude";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =================== USERS TABLE ===================
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT UNIQUE, "
                + COL_PASSWORD + " TEXT, "
                + COL_ROLE + " TEXT)";
        db.execSQL(createUsersTable);

        // Demo users
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                COL_USERNAME + "," + COL_PASSWORD + "," + COL_ROLE + ") VALUES ('naresh','12345678','farmer')");
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" +
                COL_USERNAME + "," + COL_PASSWORD + "," + COL_ROLE + ") VALUES ('buyer1','11111111','buyer')");

        // =================== PRODUCTS TABLE ===================
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PRODUCT_FARMER_ID + " INTEGER, "
                + COL_PRODUCT_FARMER_USERNAME + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_QUANTITY + " TEXT, "
                + COL_PRICE + " TEXT, "
                + COL_CATEGORY + " TEXT, "
                + COL_IMAGE + " BLOB, "
                + COL_ORGANIC + " INTEGER, "
                + COL_BULK + " INTEGER, "
                + COL_DELIVERY + " INTEGER, "
                + COL_FINAL_PRICE + " REAL, "
                + COL_LATITUDE + " REAL, "
                + COL_LONGITUDE + " REAL)";
        db.execSQL(createProductsTable);

        // =================== ORDERS TABLE ===================
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " ("
                + COL_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ORDER_BUYER_ID + " INTEGER, "
                + COL_ORDER_BUYER_NAME + " TEXT, "
                + COL_ORDER_FARMER_ID + " INTEGER, "
                + COL_ORDER_FARMER_NAME + " TEXT, "
                + COL_ORDER_PRODUCT_ID + " INTEGER, "
                + COL_ORDER_PRODUCT_NAME + " TEXT, "
                + COL_ORDER_QUANTITY + " INTEGER, "
                + COL_ORDER_BASE_PRICE + " REAL, "
                + COL_ORDER_FINAL_PRICE + " REAL, "
                + COL_ORDER_ORGANIC + " INTEGER, "
                + COL_ORDER_BULK + " INTEGER, "
                + COL_ORDER_DELIVERY + " INTEGER, "
                + COL_ORDER_LAT + " REAL, "
                + COL_ORDER_LNG + " REAL)";
        db.execSQL(createOrdersTable);

        // =================== ORDERED DETAIL TABLE ===================
        String createOrderedDetailTable = "CREATE TABLE " + TABLE_ORDERED_DETAIL + " ("
                + COL_OD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_OD_BUYER_ID + " INTEGER, "
                + COL_OD_BUYER_NAME + " TEXT, "
                + COL_OD_FARMER_ID + " INTEGER, "
                + COL_OD_FARMER_NAME + " TEXT, "
                + COL_OD_PRODUCT_ID + " INTEGER, "
                + COL_OD_PRODUCT_NAME + " TEXT, "
                + COL_OD_QUANTITY + " INTEGER, "
                + COL_OD_TOTAL + " REAL, "
                + COL_OD_LAT + " REAL, "
                + COL_OD_LNG + " REAL)";
        db.execSQL(createOrderedDetailTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERED_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // =================== USER METHODS ===================

    public boolean insertUser(String username, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_USERNAME, username);
        cv.put(COL_PASSWORD, password);
        cv.put(COL_ROLE, role);

        long result = db.insert(TABLE_USERS, null, cv);

        if (result != -1) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("localUserId", result);
            userMap.put("username", username);
            userMap.put("role", role);

            firestore.collection("users")
                    .add(userMap)
                    .addOnSuccessListener(documentReference ->
                            Log.d(TAG, "User synced: " + documentReference.getId()))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "User sync failed", e));
        }

        return result != -1;
    }

    public Cursor loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT " + COL_USER_ID + ", " + COL_USERNAME + ", " + COL_ROLE +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password}
        );
    }

    public String checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ROLE + " FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});

        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        } else {
            cursor.close();
            return null;
        }
    }

    public int getUserIdByUsername(String username) {
        int userId = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS +
                        " WHERE " + COL_USERNAME + "=?",
                new String[]{username}
        );

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();

        return userId;
    }

    // =================== PRODUCT METHODS ===================

    public boolean insertProduct(int farmerId,
                                 String farmerUsername,
                                 String name,
                                 String quantity,
                                 String price,
                                 String category,
                                 byte[] image,
                                 boolean isOrganic,
                                 boolean bulkOrder,
                                 boolean homeDelivery,
                                 double finalPrice,
                                 double latitude,
                                 double longitude) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_PRODUCT_FARMER_ID, farmerId);
        cv.put(COL_PRODUCT_FARMER_USERNAME, farmerUsername);
        cv.put(COL_NAME, name);
        cv.put(COL_QUANTITY, quantity);
        cv.put(COL_PRICE, price);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_IMAGE, image);
        cv.put(COL_ORGANIC, isOrganic ? 1 : 0);
        cv.put(COL_BULK, bulkOrder ? 1 : 0);
        cv.put(COL_DELIVERY, homeDelivery ? 1 : 0);
        cv.put(COL_FINAL_PRICE, finalPrice);
        cv.put(COL_LATITUDE, latitude);
        cv.put(COL_LONGITUDE, longitude);

        long result = db.insert(TABLE_PRODUCTS, null, cv);

        if (result != -1) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("localProductId", result);
            productMap.put("farmerId", farmerId);
            productMap.put("farmerUsername", farmerUsername);
            productMap.put("name", name);
            productMap.put("quantity", quantity);
            productMap.put("price", price);
            productMap.put("category", category);
            productMap.put("isOrganic", isOrganic);
            productMap.put("bulkOrder", bulkOrder);
            productMap.put("homeDelivery", homeDelivery);
            productMap.put("finalPrice", finalPrice);
            productMap.put("latitude", latitude);
            productMap.put("longitude", longitude);

            firestore.collection("products")
                    .add(productMap)
                    .addOnSuccessListener(documentReference ->
                            Log.d(TAG, "Product synced: " + documentReference.getId()))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Product sync failed", e));
        }

        return result != -1;
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COL_ID + " DESC", null);
    }

    public Cursor getProductsByFarmer(int farmerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_PRODUCTS +
                        " WHERE " + COL_PRODUCT_FARMER_ID + "=? ORDER BY " + COL_ID + " DESC",
                new String[]{String.valueOf(farmerId)}
        );
    }

    // =================== ORDER METHODS ===================

    public boolean insertOrder(int buyerId,
                               String buyerName,
                               int farmerId,
                               String farmerName,
                               int productId,
                               String productName,
                               int quantity,
                               double basePrice,
                               double finalPrice,
                               boolean isOrganic,
                               boolean bulkOrder,
                               boolean homeDelivery,
                               double buyerLat,
                               double buyerLng) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_ORDER_BUYER_ID, buyerId);
        cv.put(COL_ORDER_BUYER_NAME, buyerName);
        cv.put(COL_ORDER_FARMER_ID, farmerId);
        cv.put(COL_ORDER_FARMER_NAME, farmerName);
        cv.put(COL_ORDER_PRODUCT_ID, productId);
        cv.put(COL_ORDER_PRODUCT_NAME, productName);
        cv.put(COL_ORDER_QUANTITY, quantity);
        cv.put(COL_ORDER_BASE_PRICE, basePrice);
        cv.put(COL_ORDER_FINAL_PRICE, finalPrice);
        cv.put(COL_ORDER_ORGANIC, isOrganic ? 1 : 0);
        cv.put(COL_ORDER_BULK, bulkOrder ? 1 : 0);
        cv.put(COL_ORDER_DELIVERY, homeDelivery ? 1 : 0);
        cv.put(COL_ORDER_LAT, buyerLat);
        cv.put(COL_ORDER_LNG, buyerLng);

        long result = db.insert(TABLE_ORDERS, null, cv);

        if (result != -1) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("localOrderId", result);
            orderMap.put("buyerId", buyerId);
            orderMap.put("buyerName", buyerName);
            orderMap.put("farmerId", farmerId);
            orderMap.put("farmerName", farmerName);
            orderMap.put("productId", productId);
            orderMap.put("productName", productName);
            orderMap.put("quantity", quantity);
            orderMap.put("basePrice", basePrice);
            orderMap.put("finalPrice", finalPrice);
            orderMap.put("isOrganic", isOrganic);
            orderMap.put("bulkOrder", bulkOrder);
            orderMap.put("homeDelivery", homeDelivery);
            orderMap.put("buyerLatitude", buyerLat);
            orderMap.put("buyerLongitude", buyerLng);

            firestore.collection("orders")
                    .add(orderMap)
                    .addOnSuccessListener(documentReference ->
                            Log.d(TAG, "Order synced: " + documentReference.getId()))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Order sync failed", e));
        }

        return result != -1;
    }

    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + COL_ORDER_ID + " DESC", null);
    }

    public Cursor getOrdersByBuyer(int buyerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_ORDERS +
                        " WHERE " + COL_ORDER_BUYER_ID + "=? ORDER BY " + COL_ORDER_ID + " DESC",
                new String[]{String.valueOf(buyerId)}
        );
    }

    public Cursor getOrdersByFarmer(int farmerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_ORDERS +
                        " WHERE " + COL_ORDER_FARMER_ID + "=? ORDER BY " + COL_ORDER_ID + " DESC",
                new String[]{String.valueOf(farmerId)}
        );
    }

    // =================== ORDERED DETAIL METHODS ===================

    public boolean insertOrderedDetail(int buyerId,
                                       String buyerName,
                                       int farmerId,
                                       String farmerName,
                                       int productId,
                                       String productName,
                                       int quantity,
                                       double total,
                                       double latitude,
                                       double longitude) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_OD_BUYER_ID, buyerId);
        cv.put(COL_OD_BUYER_NAME, buyerName);
        cv.put(COL_OD_FARMER_ID, farmerId);
        cv.put(COL_OD_FARMER_NAME, farmerName);
        cv.put(COL_OD_PRODUCT_ID, productId);
        cv.put(COL_OD_PRODUCT_NAME, productName);
        cv.put(COL_OD_QUANTITY, quantity);
        cv.put(COL_OD_TOTAL, total);
        cv.put(COL_OD_LAT, latitude);
        cv.put(COL_OD_LNG, longitude);

        long result = db.insert(TABLE_ORDERED_DETAIL, null, cv);

        if (result != -1) {
            Map<String, Object> detailMap = new HashMap<>();
            detailMap.put("localDetailId", result);
            detailMap.put("buyerId", buyerId);
            detailMap.put("buyerName", buyerName);
            detailMap.put("farmerId", farmerId);
            detailMap.put("farmerName", farmerName);
            detailMap.put("productId", productId);
            detailMap.put("productName", productName);
            detailMap.put("quantity", quantity);
            detailMap.put("total", total);
            detailMap.put("latitude", latitude);
            detailMap.put("longitude", longitude);

            firestore.collection("orderedDetails")
                    .add(detailMap)
                    .addOnSuccessListener(documentReference ->
                            Log.d(TAG, "Ordered detail synced: " + documentReference.getId()))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Ordered detail sync failed", e));
        }

        return result != -1;
    }

    public Cursor getAllOrderedDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_ORDERED_DETAIL +
                        " ORDER BY " + COL_OD_ID + " DESC",
                null
        );
    }

    public Cursor getOrderedDetailsByFarmer(int farmerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_ORDERED_DETAIL +
                        " WHERE " + COL_OD_FARMER_ID + "=? ORDER BY " + COL_OD_ID + " DESC",
                new String[]{String.valueOf(farmerId)}
        );
    }

    public Cursor getOrderedDetailsByBuyer(int buyerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_ORDERED_DETAIL +
                        " WHERE " + COL_OD_BUYER_ID + "=? ORDER BY " + COL_OD_ID + " DESC",
                new String[]{String.valueOf(buyerId)}
        );
    }
}