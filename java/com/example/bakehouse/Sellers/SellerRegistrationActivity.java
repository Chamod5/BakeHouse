package com.example.bakehouse.Sellers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakehouse.R;
import com.example.bakehouse.models.Sellers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class SellerRegistrationActivity extends AppCompatActivity {

    ImageView profilepic;
    String imageURL;
    FirebaseAuth mAuth;
    TextView seller_login_ask;
    Button seller_registration_btn;
    EditText sellerName, sellerPhone, sellerEmail, sellerBusinessName,sellerAddress,sellerPassword,sellerRePassword;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();


        seller_login_ask = findViewById(R.id.seller_login_ask);
        seller_registration_btn = findViewById(R.id.seller_btn_register);

        profilepic = findViewById(R.id.seller_rg_profilePic);

        sellerName = findViewById(R.id.seller_rg_name);
        sellerPhone = findViewById(R.id.seller_rg_phone_no);
        sellerEmail = findViewById(R.id.seller_rg_email);
        sellerBusinessName = findViewById(R.id.seller_rg_business_name);
        sellerAddress = findViewById(R.id.seller_rg_business_address);
        sellerPassword = findViewById(R.id.seller_rg_password);
        sellerRePassword = findViewById(R.id.seller_rg_repassword);

        seller_login_ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerRegistrationActivity.this,SellerLogInActivity.class);
                startActivity(intent);
            }
        });


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            profilepic.setImageURI(uri);
                        } else {
                            Toast.makeText(SellerRegistrationActivity.this, "No profile picture", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });


        seller_registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveData();
            }
        });


    }

    private void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(SellerRegistrationActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    private void uploadData() {
        String seller_name = sellerName.getText().toString();
        String seller_phone = sellerPhone.getText().toString();
        String seller_email = sellerEmail.getText().toString();
        String seller_business_name = sellerBusinessName.getText().toString();
        String seller_address = sellerAddress.getText().toString();
        String seller_password = sellerPassword.getText().toString();
        String seller_re_password = sellerRePassword.getText().toString();

        // Create a new Sellers object
        Sellers seller = new Sellers(seller_name, seller_phone, seller_email, seller_business_name, seller_address, seller_password, seller_re_password, imageURL);

        // Get a reference to the database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Sellers");

        // Generate a unique ID for the new seller
        String sellerId = databaseReference.push().getKey();

        // Save the seller data under the unique ID
        databaseReference.child(sellerId).setValue(seller).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SellerRegistrationActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SellerRegistrationActivity.this, SellerLogInActivity.class);
                    // Pass the sellerId
                    intent.putExtra("sellerId", sellerId);
                    startActivity(intent);
                } else {
                    Toast.makeText(SellerRegistrationActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SellerRegistrationActivity.this, "Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

    /*
    private void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(SellerRegistrationActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    private void uploadData() {
        String seller_name = sellerName.getText().toString();
        String seller_phone = sellerPhone.getText().toString();
        String seller_email = sellerEmail.getText().toString();
        String seller_business_name = sellerBusinessName.getText().toString();
        String seller_address = sellerAddress.getText().toString();
        String seller_password = sellerPassword.getText().toString();
        String seller_re_password = sellerRePassword.getText().toString();

        Sellers seller = new Sellers(seller_name, seller_phone, seller_email, seller_business_name, seller_address, seller_password, seller_re_password, imageURL);
        //We are changing the child from title to currentDate,
        // because we will be updating title as well and it may affect child value.
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        FirebaseDatabase.getInstance().getReference("Sellers").child(currentDate)
                .setValue(seller).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SellerRegistrationActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SellerRegistrationActivity.this, SellerLogInActivity.class);
                            startActivity(intent);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SellerRegistrationActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    }

                });

    }
    */
////////////////////////////////////
        /*
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            profilepic.setImageURI(uri);
                        } else {
                            Toast.makeText(RegistrationActivity.this, "No profile picture", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );



        String name = sellerName.getText().toString();
        String phone = sellerPhone.getText().toString();
        String email = sellerEmail.getText().toString();
        String business_name = sellerBusinessName.getText().toString();
        String address = sellerAddress.getText().toString();
        String password = sellerPassword.getText().toString();
        String re_password = sellerRePassword.getText().toString();

        if (!name.equals("") && !phone.equals("") && !email.equals("") &&
                !business_name.equals("") && !address.equals("") && !password.equals("") &&
                !re_password.equals("")){
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                final DatabaseReference rootRef;
                                rootRef = FirebaseDatabase.getInstance().getReference();

                                String sid = mAuth.getCurrentUser().getUid();

                                HashMap<String, Object> sellerMap = new HashMap<>();
                                sellerMap.put("sellerId", sid);
                                sellerMap.put("name", name);
                                sellerMap.put("phone", phone);
                                sellerMap.put("email", email);
                                sellerMap.put("business_name", business_name);
                                sellerMap.put("address", address);
                                sellerMap.put("password", password);
                                sellerMap.put("re_password", re_password);

                                rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(SellerRegistrationActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLogInActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        }
                    });


        }else{
            Toast.makeText(this, "Complete the form..", Toast.LENGTH_SHORT).show();
        }
    */