package com.example.bakehouse.Sellers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.bakehouse.DbLink;
import com.example.bakehouse.R;
import com.example.bakehouse.VolleyMultipartRequest;
import com.example.bakehouse.adapters.ImagePagerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class SellerUploadCakeFragment extends Fragment {

    private static final int PICK_IMAGES = 1;
    private static final String ARG_EMAIL = "seller_email";

    private EditText titleEt, priceEt, descEt;
    private RadioGroup categoryGroup;
    private Button saveBtn;
    private ImageView viewImage;
    private ViewPager imagePager;
    private final ArrayList<Bitmap> selectedBitmaps = new ArrayList<>();
    private ImagePagerAdapter pagerAdapter;
    private ProgressDialog progressDialog;
    private String sellerEmail;

    // Required empty constructor
    public SellerUploadCakeFragment() {}

    // ✅ Static method to create instance
    public static SellerUploadCakeFragment newInstance(String sellerEmail) {
        SellerUploadCakeFragment fragment = new SellerUploadCakeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, sellerEmail);
        fragment.setArguments(args);
        return fragment;
    }

    // ✅ Retrieve argument in onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String sellerEmail = getArguments().getString("sellerEmail");
            // Now you can use sellerEmail in this fragment
        }
        if (getArguments() != null) {
            sellerEmail = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_upload_cake, container, false);

        titleEt = view.findViewById(R.id.uploadCakeTitle);
        priceEt = view.findViewById(R.id.uploadCakePrice);
        descEt = view.findViewById(R.id.uploadCakeDescription);
        categoryGroup = view.findViewById(R.id.radioGroup);
        saveBtn = view.findViewById(R.id.uploadCakeButton);
        viewImage = view.findViewById(R.id.uploadCakeImages);
        imagePager = view.findViewById(R.id.uploadImageViewer);

        pagerAdapter = new ImagePagerAdapter(requireContext(), selectedBitmaps);
        imagePager.setAdapter(pagerAdapter);

        viewImage.setOnClickListener(v -> openImagePicker());
        saveBtn.setOnClickListener(v -> attemptUpload());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES && resultCode == getActivity().RESULT_OK && data != null) {
            selectedBitmaps.clear();
            try {
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        selectedBitmaps.add(bmp);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    selectedBitmaps.add(bmp);
                }
                pagerAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), selectedBitmaps.size() + " images selected", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void attemptUpload() {
        String title = titleEt.getText().toString().trim();
        String priceStr = priceEt.getText().toString().trim();
        String desc = descEt.getText().toString().trim();

        if (title.isEmpty()) {
            titleEt.setError("Title is required");
            return;
        }

        if (priceStr.isEmpty()) {
            priceEt.setError("Price is required");
            return;
        }

        if (desc.isEmpty()) {
            descEt.setError("Description is required");
            return;
        }

        if (categoryGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedBitmaps.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            priceEt.setError("Invalid price format");
            return;
        }

        String category = ((RadioButton) categoryGroup.findViewById(categoryGroup.getCheckedRadioButtonId())).getText().toString();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading cake...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = DbLink.BASE_URL + "upload_cake.php";

        VolleyMultipartRequest request = new VolleyMultipartRequest(
                Request.Method.POST,url,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Upload Success", Toast.LENGTH_LONG).show();
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Upload Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", sellerEmail);
                params.put("cake_title", title);
                params.put("cake_category", category);
                params.put("cake_price", String.valueOf(price));
                params.put("cake_description", desc);
                return params;
            }

            @Override
            public Map<String, DataPart> getByteData() {
                Map<String, DataPart> imageData = new HashMap<>();
                for (int i = 0; i < selectedBitmaps.size(); i++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedBitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    imageData.put("cake_images[" + i + "]", new DataPart("img_" + i + ".jpg", stream.toByteArray(), "image/jpeg"));
                }
                return imageData;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(getContext()).add(request);
    }
}
