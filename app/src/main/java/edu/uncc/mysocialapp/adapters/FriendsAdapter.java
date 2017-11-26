package edu.uncc.mysocialapp.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.pojo.Friend;

/**
 * Created by hemva on 18-Nov-17.
 */

public class FriendsAdapter extends ArrayAdapter<Friend> {

    private Activity context;
    private ArrayList<Friend> friends;
    public FriendsAdapter(@NonNull Activity context, ArrayList friends) {
        super(context, R.layout.add_friend_item, friends);
        this.context = context;
        this.friends = friends;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.friends_item, null, true);

        TextView username = (TextView) rowView.findViewById(R.id.friendsUsername);
        ImageView removeFriendButton= (ImageView) rowView.findViewById(R.id.friendRemoveButton);

        username.setText(friends.get(position).getUsername());
        removeFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").child("currentFriends").child(friends.get(position).getUid()).removeValue();
                friends.remove(position);
                Toast.makeText(context, "Removed from Friends.", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });
        return rowView;
    }
}
