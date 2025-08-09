package com.example.bakehouse.Sellers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.DbLink;
import com.example.bakehouse.R;
import com.example.bakehouse.adapters.CakeAdapter;
import com.example.bakehouse.models.Cake;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SellerHomeFragment extends Fragment {

    private static final String ARG_EMAIL = "sellerEmail";
    private String sellerEmail;
    private TextView titleView;
    private RecyclerView recyclerView;
    private CakeAdapter adapter;
    private final List<Cake> cakeList = new ArrayList<>();

    public static SellerHomeFragment newInstance(String email) {
        SellerHomeFragment fragment = new SellerHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String sellerEmail = getArguments().getString("sellerEmail");
            // Now you can use sellerEmail in this fragment
        }

        sellerEmail = getArguments() != null ? getArguments().getString(ARG_EMAIL) : null;

        titleView = view.findViewById(R.id.titleView);
        recyclerView = view.findViewById(R.id.recycler_menu);

        adapter = new CakeAdapter(requireContext(), cakeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        loadCakesFromServer(sellerEmail);
    }

    private void loadCakesFromServer(String email) {


        new Thread(() -> {
            try {
                String urlString = DbLink.BASE_URL + "fetch_seller_cakes.php?email=" + URLEncoder.encode(email, "UTF-8");
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                InputStream input;
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    input = new BufferedInputStream(connection.getInputStream());
                } else {
                    input = new BufferedInputStream(connection.getErrorStream());
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject response = new JSONObject(result.toString());

                requireActivity().runOnUiThread(() -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray cakesArray = response.getJSONArray("cakes");
                            cakeList.clear();

                            if (cakesArray.length() == 0) {
                                Toast.makeText(requireContext(), "No cakes found", Toast.LENGTH_SHORT).show();
                            } else {
                                titleView.setText("My Cakes (" + cakesArray.length() + ")");
                                for (int i = 0; i < cakesArray.length(); i++) {
                                    JSONObject obj = cakesArray.getJSONObject(i);
                                    int id = obj.getInt("cake_id");
                                    String title = obj.getString("cake_title");
                                    String price = obj.getString("cake_price");
                                    String desc = obj.getString("cake_description");

                                    JSONArray imagesArray = obj.getJSONArray("cake_images");
                                    byte[] image = null;
                                    if (imagesArray.length() > 0) {
                                        String imageString = imagesArray.getString(0);
                                        image = Base64.decode(imageString, Base64.DEFAULT);
                                    }
                                    cakeList.add(new Cake(id, title, price, desc, image));
                                }
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), "Error loading cakes", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Parse error", Toast.LENGTH_SHORT).show();

                    }
                });

                connection.disconnect();

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
