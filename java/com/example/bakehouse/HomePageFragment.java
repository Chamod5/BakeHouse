// HomePageFragment.java
package com.example.bakehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bakehouse.Categories.BirthdayCakeActivity;
import com.example.bakehouse.Categories.OtherCakeActivity;
import com.example.bakehouse.Categories.WeddingCakeActivity;
import com.example.bakehouse.R;
import com.example.bakehouse.database.AppDatabase;
import com.example.bakehouse.database.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomePageFragment extends Fragment {

    private LinearLayout birthdayCakesCategory, weddingCakesCategory, othersCategory;
    private TextView nameUser;
    private AppDatabase database;
    private ExecutorService executor;

    private String userId;
    private String userEmail;
    private String userName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        database = AppDatabase.getInstance(getContext());
        executor = Executors.newSingleThreadExecutor();

        // Get arguments passed to fragment
        if (getArguments() != null) {
            userId = getArguments().getString("user_id", "");
            userEmail = getArguments().getString("email", "");
            //userName = getArguments().getString("user_name", "Guest");
        }

        // Initialize views - These are LinearLayouts as per the XML
        birthdayCakesCategory = view.findViewById(R.id.Birthday_cake_category);
        weddingCakesCategory = view.findViewById(R.id.Wedding_cake_category);
        othersCategory = view.findViewById(R.id.Others_category);
        nameUser = view.findViewById(R.id.nameUser);


        fetchUserName();

        // Set user name in welcome banner

        // Set click listeners with null checks
        if (birthdayCakesCategory != null) {
            birthdayCakesCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BirthdayCakeActivity.class);
                    intent.putExtra("user_id", userId != null ? userId : "");
                    intent.putExtra("email", userEmail != null ? userEmail : "");
                    startActivity(intent);
                }
            });
        }

        if (weddingCakesCategory != null) {
            weddingCakesCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WeddingCakeActivity.class);
                    intent.putExtra("user_id", userId != null ? userId : "");
                    intent.putExtra("email", userEmail != null ? userEmail : "");
                    startActivity(intent);
                }
            });
        }

        if (othersCategory != null) {
            othersCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), OtherCakeActivity.class);
                    intent.putExtra("user_id", userId != null ? userId : "");
                    intent.putExtra("email", userEmail != null ? userEmail : "");
                    startActivity(intent);
                }
            });
        }

        return view;
    }

    private void fetchUserName() {
        if (userEmail != null && !userEmail.isEmpty()) {
            executor.execute(() -> {
                User user = database.userDao().getUserByEmail(userEmail);

                // Update UI on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (user != null && user.name != null) {
                            nameUser.setText(user.name);
                        } else {
                            nameUser.setText("Guest User");
                        }
                    });
                }
            });
        } else {
            nameUser.setText("Guest User");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

}