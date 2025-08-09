package com.example.bakehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakehouse.adapters.CartAdapter;
import com.example.bakehouse.database.AppDatabase;
import com.example.bakehouse.database.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private Button orderButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CartAdapter(getContext(), cartItems);
        recyclerView.setAdapter(adapter);

        orderButton = view.findViewById(R.id.orderButton); // <-- Make sure this ID exists in fragment_cart.xml


        loadCartItems();

        return view;

    }

    private void loadCartItems() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            List<CartItem> items = db.cartDao().getAllCartItems();

            requireActivity().runOnUiThread(() -> {
                cartItems.clear();
                cartItems.addAll(items);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
}
