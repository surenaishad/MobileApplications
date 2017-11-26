package edu.uncc.mysocialapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.adapters.AddFriendsAdapter;
import edu.uncc.mysocialapp.adapters.RequestPendingAdapter;
import edu.uncc.mysocialapp.pojo.Friend;

public class PendingRequestsFragment extends Fragment {

    private ArrayList<Friend> friends;
    private ArrayAdapter pendingRequestsAdapter;
    ListView pendingRequestsList;
    private TextView emptyPendingRequestsMessage;
    public PendingRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.requests_pending_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        emptyPendingRequestsMessage = (TextView) getActivity().findViewById(R.id.emptyPendingRequestsMessage);
        emptyPendingRequestsMessage.setVisibility(View.INVISIBLE);
        //friends = new ArrayList<>();
        pendingRequestsList = (ListView) getActivity().findViewById(R.id.pendingRequestsList);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(getActivity() != null){
                    ArrayList<Friend> friends = new ArrayList<Friend>();
                    if(dataSnapshot.child(currentUser.getUid()).hasChild("friends")){
                        if(dataSnapshot.child(currentUser.getUid()).child("friends").hasChild("requests")){
                            DataSnapshot requests = dataSnapshot.child(currentUser.getUid()).child("friends").child("requests");
                            for(DataSnapshot child: requests.getChildren()){
                            /*Friend friend = new Friend();
                            friend.setUid(child.getKey());
                            friend.setUid(child.getValue(Friend.class).getUid());*/
                                friends.add(child.getValue(Friend.class));
                            }
                            if(friends != null && !friends.isEmpty() && pendingRequestsList != null){
                                emptyPendingRequestsMessage.setVisibility(View.INVISIBLE);
                                pendingRequestsAdapter = new RequestPendingAdapter(getActivity(), friends);
                                pendingRequestsList.setAdapter(pendingRequestsAdapter);
                            }
                        }else{
                            emptyPendingRequestsMessage.setVisibility(View.VISIBLE);
                        }
                    }else{
                        emptyPendingRequestsMessage.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
