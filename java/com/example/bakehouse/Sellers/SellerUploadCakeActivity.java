package com.example.bakehouse.Sellers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.bakehouse.HomePageActivity;
import com.example.bakehouse.R;
import com.example.bakehouse.adapters.ViewPagerAdapter;
import com.example.bakehouse.models.CakeClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;

public class SellerUploadCakeActivity extends AppCompatActivity {

    RelativeLayout pickImageButton;
    EditText cakeTitle, cakePrice, cakeDescription;
    ViewPager imageViewPager;
    Button uploadButton;
    Uri ImageUri;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;
    FirebaseStorage mStorage;
    StorageReference storageReference;
    RadioGroup categoryRadioGroup;
    String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_upload_cake);

        pickImageButton = findViewById(R.id.uploadCakeImages);
        imageViewPager = findViewById(R.id.uploadImageViewer);
        uploadButton = findViewById(R.id.uploadCakeButton);
        cakeTitle = findViewById(R.id.uploadCakeTitle);
        cakePrice = findViewById(R.id.uploadCakePrice);
        cakeDescription = findViewById(R.id.uploadCakeDescription);
        categoryRadioGroup = findViewById(R.id.radioGroup);

        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();

        // Firebase instance
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        Intent intent2 = getIntent();

        String sellerId = intent2.getStringExtra("sellerId");
        String name = intent2.getStringExtra("name");
        String email = intent2.getStringExtra("email");
        String phoneNo = intent2.getStringExtra("phone");
        String businessName = intent2.getStringExtra("business_name");
        String address = intent2.getStringExtra("address");
        String imageUrl = intent2.getStringExtra("imageUrl");


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get selected category
                int selectedId = categoryRadioGroup.getCheckedRadioButtonId();
                selectedCategory = getCategory(selectedId);

                if (selectedCategory != null) {
                    uploadCakeProduct();
                } else {
                    Toast.makeText(SellerUploadCakeActivity.this, "Please select a category", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPermission();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_add_item_seller);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_add_item_seller) {
                return true;
            } else if (itemId == R.id.bottom_home_seller) {
                //Intent intent = new Intent(SellerUploadCakeActivity.this, SellerHomeActivity.class);
                Intent intent = new Intent(SellerUploadCakeActivity.this, SellerHomeActivity.class);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phoneNo);
                intent.putExtra("business_name", businessName);
                intent.putExtra("address", address);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile_seller) {
                //Intent intent = new Intent(SellerUploadCakeActivity.this, SellerProfileActivity.class);
                Intent intent = new Intent(SellerUploadCakeActivity.this, SellerProfileActivity.class);
                intent.putExtra("sellerId", sellerId);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phoneNo);
                intent.putExtra("business_name", businessName);
                intent.putExtra("address", address);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        // Handle the back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to HomePageActivity
                startActivity(new Intent(getApplicationContext(), SellerHomeActivity.class));
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private String getCategory(int selectedId) {
        if (selectedId == R.id.radioButtonBirthdayCake) {
            return "Birthday cakes";
        } else if (selectedId == R.id.radioButtonWeddingCake) {
            return "Wedding cakes";
        } else if (selectedId == R.id.radioButtonOtherCake) {
            return "Others";
        }
        return null;
    }

    private void uploadCakeProduct() {
        // Upload images and store data in Firebase
        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri SingleImage = ChooseImageList.get(i);
            if (SingleImage != null) {
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("Cake_Images");
                final StorageReference ImageName = ImageFolder.child("Image" + i + " :" + SingleImage.getLastPathSegment());
                ImageName.putFile(SingleImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UrlsList.add(String.valueOf(uri));
                                if (UrlsList.size() == ChooseImageList.size()) {
                                    StoreLinks(UrlsList);
                                }
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void StoreLinks(ArrayList<String> urlsList) {
        String cake_title = cakeTitle.getText().toString();
        String cake_price = cakePrice.getText().toString();
        String cake_description = cakeDescription.getText().toString();

        String sellerId = getIntent().getStringExtra("sellerId");

        if (!TextUtils.isEmpty(cake_title) && !TextUtils.isEmpty(cake_price) && !TextUtils.isEmpty(cake_description)) {
            CakeClass cakemodel = new CakeClass(cake_title, cake_price, cake_description, sellerId, UrlsList);
            String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

            // Store cake data under the selected category
            FirebaseDatabase.getInstance().getReference("Cakes").child(selectedCategory).child(currentDate)
                    .setValue(cakemodel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SellerUploadCakeActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SellerUploadCakeActivity.this, SellerHomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SellerUploadCakeActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }

        // Clear the viewpager after uploading
        ChooseImageList.clear();
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SellerUploadCakeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SellerUploadCakeActivity.this, new
                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                PickImage();
            }
        } else {
            PickImage();
        }
    }

    private void PickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                ImageUri = data.getClipData().getItemAt(i).getUri();
                ChooseImageList.add(ImageUri);
                SetAdapter();
            }
        }
    }

    private void SetAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, ChooseImageList);
        imageViewPager.setAdapter(adapter);
    }
}
