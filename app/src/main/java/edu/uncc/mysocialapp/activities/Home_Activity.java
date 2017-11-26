package edu.uncc.mysocialapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Date;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.adapters.HomePostsAdapter;
import edu.uncc.mysocialapp.pojo.Post;

public class Home_Activity extends AppCompatActivity {

    private ListView homePosts;
    private ArrayAdapter homePostsAdapter;
    private ArrayList<Post> posts = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private DatabaseReference userData;
    private DatabaseReference currentUser;
    private DataSnapshot userPosts;
    private TextView emptyHomeMessage;
    private ImageView emptyHomeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        emptyHomeImage = (ImageView) findViewById(R.id.emptyHomeImage);
        emptyHomeMessage = (TextView) findViewById(R.id.emptyHomeMessage);
        emptyHomeImage.setVisibility(View.INVISIBLE);
        emptyHomeMessage.setVisibility(View.INVISIBLE);
        userData = FirebaseDatabase.getInstance().getReference();
        currentUser = userData.child("users").child(mUser.getUid()).getRef();

        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(Home_Activity.this, MainActivity.class);
                //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                startActivity(loginIntent);
                finish();
            }
        });
        TextView loggedInUser = (TextView) findViewById(R.id.loggedInUsername);
        ImageView friendsButton = (ImageView) findViewById(R.id.homeFriendsButton);
        homePosts = (ListView) findViewById(R.id.homeAllPosts);
        /*homePostsAdapter = new HomePostsAdapter(Home_Activity.this, posts);
        homePosts.setAdapter(homePostsAdapter);*/
        final EditText newPost = (EditText) findViewById(R.id.homeNewPost);
        newPost.clearFocus();
        ImageView sendNewPost = (ImageView) findViewById(R.id.homeSendPost);
        Log.d("HOME", "User Name -- "+mUser.getDisplayName());
        loggedInUser.setText(mUser.getDisplayName());
        loggedInUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userWallIntent = new Intent(Home_Activity.this, User_Wall.class);
                startActivity(userWallIntent);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manageFriendsIntent = new Intent(Home_Activity.this, Manage_Friends_Tabbed.class);

                startActivity(manageFriendsIntent);
            }
        });

        sendNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postMessage = newPost.getText().toString();
                Date time = new Date();
                String uid = mUser.getUid();
                Post post = new Post();
                ArrayList<Post> tempPosts = new ArrayList<Post>();

                if (postMessage == null || postMessage.isEmpty()) {
                    Toast.makeText(Home_Activity.this, "Enter a message.", Toast.LENGTH_SHORT).show();
                }else if(postMessage.length()>200){
                    Toast.makeText(Home_Activity.this, "Post cannot exceed 200 characters.", Toast.LENGTH_SHORT).show();
                }else{
                    post.setMessage(postMessage);
                    post.setTime(time);
                    post.setUid(uid);
                    if (userPosts != null) {
                        Log.d("HOME", "User Posts");
                        GenericTypeIndicator<ArrayList<Post>> t = new GenericTypeIndicator<ArrayList<Post>>() {
                        };
                        tempPosts = (ArrayList<Post>) userPosts.getValue(t);
                        tempPosts.add(post);
                        currentUser.child("posts").setValue(tempPosts);
                    } else {
                        Log.d("HOME", "No User Posts");
                        tempPosts.add(post);
                        currentUser.child("posts").setValue(tempPosts);
                    }
                    newPost.setText("");
                }
            }
        });

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts = new ArrayList<Post>();

                DataSnapshot users = dataSnapshot.child("users");
                if (users.child(mUser.getUid()).hasChild("posts")) {
                    userPosts = users.child(mUser.getUid()).child("posts");
                    GenericTypeIndicator<ArrayList<Post>> t = new GenericTypeIndicator<ArrayList<Post>>() {
                    };
                    posts.addAll((ArrayList<Post>) userPosts.getValue(t));
                    Log.d("HOME", "User Posts -- "+(ArrayList<Post>) userPosts.getValue(t));
                }
                if (users.child(mUser.getUid()).hasChild("friends") && users.child(mUser.getUid()).child("friends").hasChild("currentFriends")) {
                    if (users.child(mUser.getUid()).child("friends").hasChild("currentFriends")) {
                        DataSnapshot currentFriends = users.child(mUser.getUid()).child("friends").child("currentFriends");
                        for (DataSnapshot friend : currentFriends.getChildren()) {
                            if (users.child(friend.getKey()).hasChild("posts")) {
                                GenericTypeIndicator<ArrayList<Post>> t = new GenericTypeIndicator<ArrayList<Post>>() {
                                };
                                ArrayList<Post> friendsPosts = (ArrayList<Post>) users.child(friend.getKey()).child("posts").getValue(t);
                                posts.addAll(friendsPosts);
                            }
                        }


                    }
                }
                Collections.sort(posts, new Comparator<Post>() {
                    @Override
                    public int compare(Post post1, Post post2) {
                        if (post1.getTime().getTime() < post2.getTime().getTime()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2);
                Long dateValidation = cal.getTimeInMillis();
                ArrayList<Post> subList = new ArrayList<>();
                for(Post post: posts){
                    if(post.getTime().getTime() > dateValidation){
                        subList.add(post);
                    }
                }
                if(!posts.isEmpty()){
                    homePostsAdapter = new HomePostsAdapter(Home_Activity.this, posts, users, subList);
                    homePosts.setAdapter(homePostsAdapter);
                    emptyHomeImage.setVisibility(View.INVISIBLE);
                    emptyHomeMessage.setVisibility(View.INVISIBLE);
                }else{
                    emptyHomeImage.setVisibility(View.VISIBLE);
                    emptyHomeMessage.setVisibility(View.VISIBLE);
                }

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
                mAuth.signOut();
                Intent loginIntent = new Intent(Home_Activity.this, MainActivity.class);
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
