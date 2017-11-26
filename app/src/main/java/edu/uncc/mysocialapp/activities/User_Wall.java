package edu.uncc.mysocialapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.adapters.WallPostsAdapter;
import edu.uncc.mysocialapp.pojo.Post;

public class User_Wall extends AppCompatActivity {

    private ListView wallPosts;
    private ArrayAdapter wallPostsAdapter;
    private DataSnapshot userSnapshot;
    private ArrayList<Post> posts = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private DatabaseReference userData;
    private DatabaseReference currentUser;
    private TextView emptyWallMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_wall);

        emptyWallMessage = (TextView) findViewById(R.id.emptyWallMessage);
        emptyWallMessage.setVisibility(View.INVISIBLE);

        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(User_Wall.this, MainActivity.class);
                //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                startActivity(loginIntent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userData = FirebaseDatabase.getInstance().getReference();
        currentUser = userData.child("users").child(mUser.getUid()).getRef();

        TextView loggedInUser = (TextView) findViewById(R.id.wallLoggedInUsername);
        ImageView friendsButton = (ImageView) findViewById(R.id.wallFriendsButton);
        ImageView editButton = (ImageView) findViewById(R.id.wallEditButton);
        wallPosts = (ListView) findViewById(R.id.wallPosts);
        /*wallPostsAdapter = new WallPostsAdapter(User_Wall.this, posts);
        wallPosts.setAdapter(wallPostsAdapter);*/
        final EditText newPost = (EditText) findViewById(R.id.homeNewPost);
        ImageView sendNewPost = (ImageView) findViewById(R.id.homeSendPost);
        loggedInUser.setText(mUser.getDisplayName());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editUserIntent = new Intent(User_Wall.this, EditUserProfile.class);
                editUserIntent.putExtra("userFname",userSnapshot.child("userprofile").child("fname").getValue(String.class));
                editUserIntent.putExtra("userLname",userSnapshot.child("userprofile").child("lname").getValue(String.class));
                editUserIntent.putExtra("userEid",userSnapshot.child("userprofile").child("email").getValue(String.class));
                editUserIntent.putExtra("userDob",userSnapshot.child("userprofile").child("dob").getValue(String.class));
                startActivity(editUserIntent);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent manageFriendsIntent = new Intent(User_Wall.this, Manage_Friends_Tabbed.class);
                startActivity(manageFriendsIntent);
            }
        });
        currentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userSnapshot = dataSnapshot;
                posts.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.getKey().equals("posts")){
                        GenericTypeIndicator<ArrayList<Post>> t = new GenericTypeIndicator<ArrayList<Post>>() {};
                        posts = (ArrayList<Post>) child.getValue(t);
                        //posts.add(post);
                        Log.d("DISPLAY", posts.toString());
                    /*contacts.remove(null);*/
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -2);
                        Long dateValidation = cal.getTimeInMillis();
                        ArrayList<Post> lastTwoDaysList = new ArrayList<>();
                        ArrayList<Post> beforeTwoDaysList = new ArrayList<Post>();
                        beforeTwoDaysList.addAll(posts);
                        for(Post post: posts){
                            if(post.getTime().getTime() > dateValidation){
                                lastTwoDaysList.add(post);
                                beforeTwoDaysList.remove(post);
                            }
                        }
                        if(!posts.isEmpty()){
                            emptyWallMessage.setVisibility(View.INVISIBLE);
                            wallPostsAdapter = new WallPostsAdapter(User_Wall.this, posts, dataSnapshot, lastTwoDaysList, beforeTwoDaysList);
                            wallPosts.setAdapter(wallPostsAdapter);
                        }else{
                            emptyWallMessage.setVisibility(View.VISIBLE);
                        }
                    }

                    if(posts.isEmpty()){
                        emptyWallMessage.setVisibility(View.VISIBLE);
                    }else{
                        emptyWallMessage.setVisibility(View.INVISIBLE);
                    }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backToHomeIntent = new Intent(User_Wall.this, Home_Activity.class);
        backToHomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToHomeIntent);
        finish();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(User_Wall.this, MainActivity.class);
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
