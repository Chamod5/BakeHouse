package com.example.bakehouse;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bakehouse.R;
import com.example.bakehouse.adapters.SearchAdapter;
import com.example.bakehouse.models.Cake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchAdapter.OnCakeClickListener {

    private static final String TAG = "SearchFragment";
    //private static final String SEARCH_URL = "http://your-server-url/search_cakes.php"; // Replace with your actual URL
    String url = DbLink.BASE_URL + "search_cakes.php";

    private EditText searchInput;
    private Button searchButton;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private List<Cake> cakeList;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initViews(view);
        setupRecyclerView();
        setupSearchFunctionality();
        loadAllCakes(); // Load all cakes initially

        return view;
    }

    private void initViews(View view) {
        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);
        recyclerView = view.findViewById(R.id.search_recycler_view);

        requestQueue = Volley.newRequestQueue(getContext());
        cakeList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        searchAdapter = new SearchAdapter(getContext(), cakeList);
        searchAdapter.setOnCakeClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchAdapter);
    }

    private void setupSearchFunctionality() {
        // Search button click listener
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString().trim();
            searchCakes(searchTerm);
        });

        // Real-time search as user types
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchTerm = s.toString().trim();
                // Add a small delay to avoid too many API calls
                searchInput.removeCallbacks(searchRunnable);
                searchInput.postDelayed(searchRunnable, 300);
            }
        });
    }

    private Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            String searchTerm = searchInput.getText().toString().trim();
            searchCakes(searchTerm);
        }
    };

    private void loadAllCakes() {
        searchCakes(""); // Empty string will return all cakes
    }

    private void searchCakes(String searchTerm) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("search_term", searchTerm);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonRequest,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            handleSearchResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Search error: " + error.getMessage());
                            Toast.makeText(getContext(), "Search failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(request);

        } catch (JSONException e) {
            Log.e(TAG, "JSON error: " + e.getMessage());
        }
    }

    private void handleSearchResponse(JSONObject response) {
        try {
            boolean success = response.getBoolean("success");

            if (success) {
                cakeList.clear();
                JSONArray dataArray = response.getJSONArray("data");

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject cakeObj = dataArray.getJSONObject(i);

                    // Extract data according to your Cake model
                    int id = cakeObj.getInt("id");
                    String title = cakeObj.getString("title");
                    String price = cakeObj.getString("price");
                    String description = cakeObj.getString("description");

                    // Handle image - convert base64 to byte array
                    byte[] imageBytes = null;
                    String imageString = cakeObj.optString("image", null);
                    if (imageString != null && !imageString.isEmpty()) {
                        try {
                            imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                        } catch (Exception e) {
                            Log.e(TAG, "Error decoding image: " + e.getMessage());
                        }
                    }

                    Cake cake = new Cake(id, title, price, description, imageBytes);
                    cakeList.add(cake);
                }

                searchAdapter.updateCakeList(cakeList);

                if (cakeList.isEmpty()) {
                    Toast.makeText(getContext(), "No cakes found", Toast.LENGTH_SHORT).show();
                }

            } else {
                String message = response.getString("message");
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            Toast.makeText(getContext(), "Error parsing search results", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCakeClick(Cake cake) {
        // Handle cake item click - navigate to cake details or perform action
        Toast.makeText(getContext(), "Clicked: " + cake.getTitle(), Toast.LENGTH_SHORT).show();

        // You can add navigation logic here, for example:
        // Bundle bundle = new Bundle();
        // bundle.putInt("cake_id", cake.getId());
        // NavController navController = Navigation.findNavController(getView());
        // navController.navigate(R.id.action_search_to_cake_details, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}