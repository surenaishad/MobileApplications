package edu.uncc.mysocialapp.fragments;

import android.content.Context;
import android.net.Uri;
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
import edu.uncc.mysocialapp.pojo.Friend;

public class AddFriendsFragment extends Fragment {

    private ArrayAdapter addFriendsAdapter;
    private ArrayList<Friend> friends = new ArrayList<>();
    ListView addFriendsList;
    private TextView emptyAddFriendsMessage;

    private TextView message;

    public AddFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_friends_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        emptyAddFriendsMessage  = (TextView) getActivity().findViewById(R.id.emptyAddFriendsMessage);
        emptyAddFriendsMessage.setVisibility(View.INVISIBLE);
        addFriendsList = (ListView) getActivity().findViewById(R.id.addFriendsList);

        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(getActivity() != null){
                    ArrayList<Friend> friendsArray = new ArrayList<>();
                    DataSnapshot friendsData = null;
                    DataSnapshot requests = null;
                    DataSnapshot currentFriends = null;
                    if(dataSnapshot.child(currentUser.getUid()).hasChild("friends")){
                        friendsData = dataSnapshot.child(currentUser.getUid()).child("friends");

                        if(friendsData.hasChild("requests")){
                            requests = dataSnapshot.child(currentUser.getUid()).child("friends").child("requests");
                        }
                        if(friendsData.hasChild("currentFriends")){
                            currentFriends = dataSnapshot.child(currentUser.getUid()).child("friends").child("currentFriends");
                        }
                    }
                    for(DataSnapshot child: dataSnapshot.getChildren()){
                        Friend friend = new Friend();
                        if(child.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                ||( requests != null && requests.hasChild(child.getKey()))
                                ||( currentFriends != null && currentFriends.hasChild(child.getKey()))){

                        }else{
                            friend.setUid(child.getKey());
                            friend.setUsername((String) child.child("username").getValue());
                            friendsArray.add(friend);
                        }

                    }

                    if(friendsArray!= null && !friendsArray.isEmpty() && addFriendsList != null){
                        emptyAddFriendsMessage.setVisibility(View.INVISIBLE);
                        addFriendsAdapter = new AddFriendsAdapter(getActivity(), friendsArray);
                        addFriendsList.setAdapter(addFriendsAdapter);
                    }else{
                        emptyAddFriendsMessage.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
