package com.example.bakehouse.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bakehouse.AppUtils;
import com.example.bakehouse.DbLink;
import com.example.bakehouse.LogInActivity;
import com.example.bakehouse.R;
import com.example.bakehouse.RegistrationActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SellerLoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    TextView signUpText;
    Button loginButton;
    ProgressDialog progressDialog;

    String url = DbLink.BASE_URL + "seller_login.php";   //database link

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_log_in);

        emailEditText = findViewById(R.id.seller_login_Email);
        passwordEditText = findViewById(R.id.seller_login_Password);
        loginButton = findViewById(R.id.seller_login_Button);
        signUpText = findViewById(R.id.signuplink);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginSeller(email, password);
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SellerLoginActivity.this, SellerRegistrationActivity.class));
            }
        });
    }

    private void loginSeller(String email, String password) {
        progressDialog.show();

        AppUtils.trustEveryone();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");

                        if (message.equalsIgnoreCase("Login Success")) {
                            String sellerEmail = jsonObject.getString("email");

                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SellerLoginActivity.this, SellerMainActivity.class);
                            intent.putExtra("sellerEmail", sellerEmail);  // pass email
                            startActivity(intent);
                            finish();
                    } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Invalid response", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    //Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("email", email);
                data.put("password", password);
                return data;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
