package edu.uncc.mysocialapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.activities.FriendPosts;
import edu.uncc.mysocialapp.adapters.AddFriendsAdapter;
import edu.uncc.mysocialapp.adapters.FriendsAdapter;
import edu.uncc.mysocialapp.pojo.Friend;

public class FriendsFragment extends Fragment {

    private ArrayAdapter friendsAdapter;
    private ArrayList<Friend> friends;
    private TextView emptyFriendsMessage;
    private ImageView emptyFriendsButton;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.friends_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ListView friendsList = (ListView) getActivity().findViewById(R.id.friendsList);
        emptyFriendsButton = (ImageView) getActivity().findViewById(R.id.emptyFriendsButton);
        emptyFriendsMessage= (TextView) getActivity().findViewById(R.id.emptyFriendsMessage);
        emptyFriendsButton.setVisibility(View.INVISIBLE);
        emptyFriendsMessage.setVisibility(View.INVISIBLE);
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent friendIntent = new Intent(getActivity(), FriendPosts.class);
                friendIntent.putExtra("friend", friends.get(i));
                getActivity().startActivity(friendIntent);
            }
        });


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(getActivity()!= null){

                    ArrayList<Friend> friendsArray = new ArrayList<>();
                    DataSnapshot currentUser = dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if(currentUser.hasChild("friends")){
                        DataSnapshot userFriendsManager = currentUser.child("friends");
                        if(userFriendsManager.hasChild("currentFriends")){
                            DataSnapshot userFriends = userFriendsManager.child("currentFriends");
                            for(DataSnapshot friendChild : userFriends.getChildren()){
                                friendsArray.add(friendChild.getValue(Friend.class));
                            }
                            if(friendsArray != null && !friendsArray.isEmpty() && friendsList != null){
                                emptyFriendsButton.setVisibility(View.INVISIBLE);
                                emptyFriendsMessage.setVisibility(View.INVISIBLE);
                                friends = friendsArray;
                                friendsAdapter = new FriendsAdapter(getActivity(), friendsArray);
                                friendsList.setAdapter(friendsAdapter);
                            }

                        }


                    }
                    if(friendsArray.isEmpty()){
                        emptyFriendsButton.setVisibility(View.VISIBLE);
                        emptyFriendsMessage.setVisibility(View.VISIBLE);
                    }else{
                        emptyFriendsButton.setVisibility(View.INVISIBLE);
                        emptyFriendsMessage.setVisibility(View.INVISIBLE);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        emptyFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewPager)getActivity().findViewById(R.id.container)).setCurrentItem(1);
            }
        });

    }
}
