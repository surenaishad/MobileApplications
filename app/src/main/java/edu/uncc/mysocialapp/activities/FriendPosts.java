package edu.uncc.mysocialapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.adapters.HomePostsAdapter;
import edu.uncc.mysocialapp.pojo.Friend;
import edu.uncc.mysocialapp.pojo.Post;

public class FriendPosts extends AppCompatActivity {

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_posts);

        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(FriendPosts.this, MainActivity.class);
                //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                startActivity(loginIntent);
                finish();
            }
        });

        final Friend friend = (Friend) getIntent().getExtras().get("friend");
        final Post friendPost = (Post) getIntent().getExtras().get("friendPost");

        if(friend != null){
            userId = friend.getUid();
        }else{
            userId = friendPost.getUid();
        }
        final ListView friendsPostsListView = (ListView) findViewById(R.id.friendPostsAllPosts);
        final TextView friendName = (TextView) findViewById(R.id.friendName);
        final TextView postsLabel = (TextView) findViewById(R.id.friendPostsLabel);

        ImageView homeButton = (ImageView) findViewById(R.id.friendPostsFriendsButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(FriendPosts.this, Home_Activity.class);
                startActivity(homeIntent);
            }
        });



        final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users").getRef();
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String fullName = dataSnapshot.child(userId).child("username").getValue().toString();
                friendName.setText(fullName);
                postsLabel.setText(dataSnapshot.child(userId).child("userprofile").child("fname").getValue()+"'s Posts");
                DataSnapshot friendReference = dataSnapshot.child(userId).child("posts");
                GenericTypeIndicator<ArrayList<Post>> postArrayListType = new GenericTypeIndicator<ArrayList<Post>>(){};
                ArrayList<Post> friendPosts = friendReference.getValue(postArrayListType);
                Collections.sort(friendPosts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post, Post t1) {
                        if(post.getTime().getTime()< t1.getTime().getTime()){
                            return 1;
                        }else{
                            return -1;
                        }
                    }
                });
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2);
                Long dateValidation = cal.getTimeInMillis();
                ArrayList<Post> subList = new ArrayList<>();
                for(Post post: friendPosts){
                    if(post.getTime().getTime() > dateValidation){
                        subList.add(post);
                    }
                }
                ArrayAdapter friendsPostsAdapter = new HomePostsAdapter(FriendPosts.this, friendPosts, dataSnapshot, subList);
                friendsPostsListView.setAdapter(friendsPostsAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        //menu.getItem(1).setVisible(false);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(FriendPosts.this, MainActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
                finish();
                break;
            // action with ID action_settings was selected
            default:
                break;
        }

        return true;
    }*/
}
