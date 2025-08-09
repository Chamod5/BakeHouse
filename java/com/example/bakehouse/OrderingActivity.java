package com.example.bakehouse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class OrderingActivity extends AppCompatActivity {

    private static final String TAG = "OrderingActivity";

    private TextView cakeTitle, cakeSeller, cakePrice, cakeQuantityLabel, noOfCakes, referenceNumber;
    private EditText etName, etPhone, etAddress, etComment;
    private RadioGroup paymentMethodGroup;
    private RadioButton radioOnlinePayment, radioHandOverPayment;
    private Button btnMinus, btnPlus, btnPlaceOrder, btnBack;

    private int cakeId, sellerId, userId;
    private int quantity = 1;
    private String generatedRefNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        // View binding
        cakeTitle = findViewById(R.id.cakeTitle);
        cakeSeller = findViewById(R.id.cakeSeller);
        cakePrice = findViewById(R.id.cakePrice);
        cakeQuantityLabel = findViewById(R.id.cakeQuantity);
        noOfCakes = findViewById(R.id.noOfCakes);
        referenceNumber = findViewById(R.id.referenceNumber);

        etName = findViewById(R.id.name);
        etPhone = findViewById(R.id.phoneNumber);
        etAddress = findViewById(R.id.address);
        etComment = findViewById(R.id.comment);

        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        radioOnlinePayment = findViewById(R.id.radioOnlinePayment);
        radioHandOverPayment = findViewById(R.id.radioHandOverPayment);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnBack = findViewById(R.id.btnBack);

        // Get intent extras
        cakeId = getIntent().getIntExtra("cake_id", -1);
        sellerId = getIntent().getIntExtra("seller_id", -1);
        userId = getIntent().getIntExtra("user_id", -1);

        Log.d(TAG, "Received - cake_id: " + cakeId + ", seller_id: " + sellerId + ", user_id: " + userId);

        // Validate required data
        if (cakeId < 0 || sellerId < 0) {
            Toast.makeText(this, "Invalid cake or seller ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (userId < 0) {
            Toast.makeText(this, "User session invalid. Please login again.", Toast.LENGTH_SHORT).show();
            // Redirect to login
            Intent loginIntent = new Intent(this, LogInActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Generate reference number
        generatedRefNumber = generateReferenceNumber();
        referenceNumber.setText("Reference Number: " + generatedRefNumber);

        // Load cake and seller details
        loadCakeDetails();

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Place order
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String comment = etComment.getText().toString().trim();

        String quantityStr = noOfCakes.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Please enter cake quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity entered", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPaymentId = paymentMethodGroup.getCheckedRadioButtonId();
        String paymentMethod = selectedPaymentId == R.id.radioOnlinePayment ? "Online Payment" :
                selectedPaymentId == R.id.radioHandOverPayment ? "Hand Over Payment" :
                        "Not Selected";

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || selectedPaymentId == -1) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate user session before placing order
        if (userId < 0) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String summary = "Order Summary:\n"
                + "Cake: " + cakeTitle.getText() + "\n"
                + "Quantity: " + quantityStr + "\n"
                + "Name: " + name + "\n"
                + "Phone: " + phone + "\n"
                + "Address: " + address + "\n"
                + "Payment method: " + paymentMethod + "\n"
                + "Comment: " + comment + "\n"
                + "Reference no: " + generatedRefNumber;

        Toast.makeText(this, summary, Toast.LENGTH_LONG).show();

        // Send order to server
        new Thread(() -> {
            try {
                URL url = new URL(DbLink.BASE_URL + "cake_ordering.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String postData = "reference_no=" + URLEncoder.encode(generatedRefNumber, "UTF-8") +
                        "&user_id=" + URLEncoder.encode(String.valueOf(userId), "UTF-8") +
                        "&seller_id=" + URLEncoder.encode(String.valueOf(sellerId), "UTF-8") +
                        "&buyer_name=" + URLEncoder.encode(name, "UTF-8") +
                        "&cake_title=" + URLEncoder.encode(cakeTitle.getText().toString(), "UTF-8") +
                        "&quantity=" + URLEncoder.encode(String.valueOf(quantity), "UTF-8") +
                        "&cake_price=" + URLEncoder.encode(cakePrice.getText().toString().replace("Rs. ", ""), "UTF-8") +
                        "&phone_number=" + URLEncoder.encode(phone, "UTF-8") +
                        "&address=" + URLEncoder.encode(address, "UTF-8") +
                        "&comment=" + URLEncoder.encode(comment, "UTF-8") +
                        "&payment_method=" + URLEncoder.encode(paymentMethod, "UTF-8");

                Log.d(TAG, "Sending order with user_id: " + userId);

                conn.getOutputStream().write(postData.getBytes());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                runOnUiThread(() -> {
                    if (jsonResponse.optString("status").equals("success")) {
                        Toast.makeText(OrderingActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(OrderingActivity.this, "Order failed: " + jsonResponse.optString("message"), Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(OrderingActivity.this, "Error placing order: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String generateReferenceNumber() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder reference = new StringBuilder();
        int length = 10; //desired length

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            reference.append(characters.charAt(index));
        }

        return reference.toString();
    }

    private void loadCakeDetails() {
        new Thread(() -> {
            try {
                String urlStr = DbLink.BASE_URL + "fetch_cake_order_page.php?cake_id=" + cakeId;
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                InputStream in = conn.getResponseCode() == 200 ?
                        new BufferedInputStream(conn.getInputStream()) :
                        new BufferedInputStream(conn.getErrorStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);

                JSONObject resp = new JSONObject(sb.toString());
                if (!resp.optString("status").equals("success")) {
                    runOnUiThread(() -> Toast.makeText(this, "Cake not found", Toast.LENGTH_SHORT).show());
                    return;
                }

                JSONObject cake = resp.getJSONObject("cake");
                final String title = cake.getString("cake_title");
                final String price = "Rs. " + cake.getString("cake_price");

                runOnUiThread(() -> {
                    cakeTitle.setText(title);
                    cakePrice.setText(price);
                });

                conn.disconnect();

                // Load seller details after cake details are loaded
                loadSellerName();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error loading cake", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loadSellerName() {
        new Thread(() -> {
            try {
                String urlStr = DbLink.BASE_URL + "fetch_seller_order_page.php?seller_id=" + sellerId;
                HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                InputStream in = conn.getResponseCode() == 200 ?
                        new BufferedInputStream(conn.getInputStream()) :
                        new BufferedInputStream(conn.getErrorStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);

                String responseText = sb.toString();
                Log.d(TAG, "Seller response: " + responseText);

                JSONObject resp = new JSONObject(responseText);
                if (!resp.optString("status").equals("success")) {
                    String errorMsg = resp.optString("message", "Seller not found");
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show());
                    return;
                }

                final String sellerName = resp.getJSONObject("seller").getString("name");
                runOnUiThread(() -> cakeSeller.setText("Seller: " + sellerName));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}