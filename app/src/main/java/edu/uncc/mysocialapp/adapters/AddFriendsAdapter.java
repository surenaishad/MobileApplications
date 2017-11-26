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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.activities.MainActivity;
import edu.uncc.mysocialapp.pojo.Friend;
import edu.uncc.mysocialapp.pojo.Post;

/**
 * Created by hemva on 18-Nov-17.
 */

public class AddFriendsAdapter extends ArrayAdapter<Friend> {

    private Activity context;
    private ArrayList<Friend> friends;
    private ArrayList<Friend> pendingFriends;
    private ArrayList<Friend> currentFriends;
    DatabaseReference requestsReference;

    public AddFriendsAdapter(@NonNull Activity context, ArrayList friends) {
        super(context, R.layout.add_friend_item, friends);
        this.context = context;
        this.friends = friends;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.add_friend_item, null, true);

        TextView username = (TextView) rowView.findViewById(R.id.addFriendUsername);
        ImageView addFriendButton = (ImageView) rowView.findViewById(R.id.addFriendButton);

        username.setText(friends.get(position).getUsername());
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Add new Friend", Toast.LENGTH_SHORT).show();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                requestsReference = rootRef.child("users");

                if (!friends.isEmpty()) {
                    Friend temp1 = friends.get(position);
                    temp1.setRequestStatus(MainActivity.REQUEST_SENT);
                    requestsReference.child(currentUser.getUid()).child("friends")
                            .child("requests").child(friends.get(position).getUid())
                            .setValue(temp1);
                    Friend temp = new Friend();
                    temp.setUsername(currentUser.getDisplayName());
                    temp.setUid(currentUser.getUid());
                    temp.setRequestStatus(MainActivity.REQUEST_RECEIVED);
                    requestsReference.child(friends.get(position).getUid())
                            .child("friends").child("requests")
                            .child(currentUser.getUid()).setValue(temp);
                    friends.remove(position);
                    notifyDataSetChanged();
                }

            }
        });
        return rowView;
    }
}
