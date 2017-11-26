package edu.uncc.mysocialapp.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.adapters.SectionsPageAdapter;
import edu.uncc.mysocialapp.fragments.AddFriendsFragment;
import edu.uncc.mysocialapp.fragments.FriendsFragment;
import edu.uncc.mysocialapp.fragments.PendingRequestsFragment;

public class Manage_Friends_Tabbed extends AppCompatActivity {

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage__friends__tabbed);
        Log.d("MANAGE", "onCreate : Starting.");

        ImageView logout = (ImageView) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(Manage_Friends_Tabbed.this, MainActivity.class);
                //loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                startActivity(loginIntent);
                finish();
            }
        });

        ImageView homeButton = (ImageView) findViewById(R.id.friendsTabHome);
        TextView loggedInUsername = (TextView) findViewById(R.id.manageFriendsLoggedInUsername);
        loggedInUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeIntent = new Intent(Manage_Friends_Tabbed.this, Home_Activity.class);
                startActivity(homeIntent);
            }
        });

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsFragment(), "Friends");
        adapter.addFragment(new AddFriendsFragment(), "Add Friends");
        adapter.addFragment(new PendingRequestsFragment(), "Pending Requests");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if(getParent() != null) {
            return getParent().onCreateOptionsMenu(menu);
        }
        //menu.getItem(1).setVisible(false);
        return true;
    }



    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(Manage_Friends_Tabbed.this, MainActivity.class);
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
