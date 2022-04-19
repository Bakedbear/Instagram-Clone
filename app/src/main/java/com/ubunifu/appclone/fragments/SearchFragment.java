package com.ubunifu.appclone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ubunifu.appclone.R;
import com.ubunifu.appclone.adapters.UserAdapter;
import com.ubunifu.appclone.models.User;

import java.util.ArrayList;


public class SearchFragment extends Fragment {
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;
    ArrayList<User> mUserArrayList;
    UserAdapter mUserAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();
        mSearchView = view.findViewById(R.id.search_view);
        mRecyclerView = view.findViewById(R.id.search_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mUserArrayList = new ArrayList<>();

        mUserAdapter = new UserAdapter(getContext(), mUserArrayList);
        mRecyclerView.setAdapter(mUserAdapter);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchUsers(newText);
                return true;
            }
        });

        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        User user = documentSnapshot.toObject(User.class);
                        mUserArrayList.add(user);
                        mUserAdapter.notifyDataSetChanged();
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        return view;
    }

    private void searchUsers(String newText) {

        db.collection("Users")
                .whereEqualTo("email", newText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mUserArrayList.clear();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            User user = documentSnapshot.toObject(User.class);
                            mUserArrayList.add(user);
                            mUserAdapter.notifyDataSetChanged();
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}