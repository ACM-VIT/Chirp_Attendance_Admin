package com.example.chirpattendance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chirpattendance.R;
import com.example.chirpattendance.activities.RoomActivity;
import com.example.chirpattendance.adapters.AttendeesListAdapter;
import com.example.chirpattendance.models.AttendeesDefaultersList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class FragmentAttendeesList extends Fragment {

    private DatabaseReference reference;
    private ArrayList<String> uniqueNumber;
    private ArrayList<AttendeesDefaultersList> attendeeList;
    private RecyclerView recyclerView;
    private AttendeesListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;

    public FragmentAttendeesList() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_attendees_defaulters_list, container, false);
        initialize(rootView);
        getAttendeesList();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        RoomActivity.getChirpAttendance().topBarSetText("Attendees List");
        return rootView;
    }

    private void initialize(View rootView) {
        reference = FirebaseDatabase.getInstance().getReference();
        uniqueNumber = new ArrayList<>();
        attendeeList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_attendees_defaulters_list_view);
        adapter = new AttendeesListAdapter(attendeeList);
        layoutManager = new LinearLayoutManager(getContext());
        progressBar = rootView.findViewById(R.id.progress_bar_attendees_defaulters_list);
    }

    private void getAttendeesList() {
        progressBar.setVisibility(View.VISIBLE);
        String h = RoomActivity.getHashedKey();
        reference.child("rooms").child(RoomActivity.getHashedKey()).child("attendees").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uniqueNumber.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String unique = snapshot.getValue().toString();
                    uniqueNumber.add(unique);
                }

                reference.child("users").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(String unique : uniqueNumber)
                        {
                            if(dataSnapshot.child(unique).exists())
                            {
                                DataSnapshot snap = dataSnapshot.child(unique);
                                AttendeesDefaultersList attendee = new AttendeesDefaultersList(snap.child("name").getValue().toString(),
                                snap.child("email").getValue().toString(),
                                snap.child("user_id").getValue().toString());
                                attendeeList.add(attendee);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}
