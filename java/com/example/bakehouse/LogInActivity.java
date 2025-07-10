package com.example.bakehouse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakehouse.Sellers.SellerLogInActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    Button loginButton;
    EditText username, password;
    TextView signUpText;
    ProgressBar progressBar;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        username = findViewById(R.id.loginUsername);
        password = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signuplink);

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, RegistrationActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                loginUser();
            }
        });
    }

    private void loginUser() {
        String userUsername = username.getText().toString();
        String userPassword = password.getText().toString();

        if (TextUtils.isEmpty(userUsername)) {
            Toast.makeText(this, "Username is empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Reference to the "Users" node in Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        // Query to find the user by username
        reference.orderByChild("username").equalTo(userUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String retrievedPassword = snapshot.child("password").getValue(String.class);

                        if (retrievedPassword.equals(userPassword)) {
                            String name = snapshot.child("name").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);
                            String imageUrl = snapshot.child("profilepic").getValue(String.class);

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            //Intent intent = new Intent(LogInActivity.this, HomePageActivity.class);

                            Intent intent2 = new Intent(LogInActivity.this, ProfileActivity.class);
                            intent2.putExtra("name", name);
                            intent2.putExtra("username", userUsername);
                            intent2.putExtra("email", email);
                            intent2.putExtra("imageUrl", imageUrl);
                            startActivity(intent2);

                            finish(); // Close the login activity
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LogInActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LogInActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LogInActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
