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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.activities.MainActivity;
import edu.uncc.mysocialapp.pojo.Friend;

/**
 * Created by hemva on 18-Nov-17.
 */

public class RequestPendingAdapter extends ArrayAdapter<Friend> {

    private Activity context;
    private ArrayList<Friend> friends;

    public RequestPendingAdapter(@NonNull Activity context, ArrayList friends) {
        super(context, R.layout.request_pending_item, friends);
        this.context = context;
        this.friends = friends;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.request_pending_item, null, true);

        TextView username = (TextView) rowView.findViewById(R.id.reqPendingUsername);
        ImageView accept = (ImageView) rowView.findViewById(R.id.requestAccept);
        ImageView reject = (ImageView) rowView.findViewById(R.id.requestReject);

        if (friends.get(position).getRequestStatus().equals(MainActivity.REQUEST_SENT)) {
            accept.setVisibility(View.INVISIBLE);
        } else {
            accept.setVisibility(View.VISIBLE);
        }

        username.setText(friends.get(position).getUsername());
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Accepted.", Toast.LENGTH_SHORT).show();

                final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users").getRef();

                FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
                usersReference.child(loggedInUser.getUid()).child("friends").child("requests").child(friends.get(position).getUid()).removeValue();
                usersReference.child(loggedInUser.getUid()).child("friends").child("currentFriends").child(friends.get(position).getUid()).setValue(friends.get(position));

                usersReference.child(friends.get(position).getUid()).child("friends").child("requests").child(loggedInUser.getUid()).removeValue();
                Friend temp = new Friend();
                temp.setUid(loggedInUser.getUid());
                temp.setUsername(loggedInUser.getDisplayName());
                usersReference.child(friends.get(position).getUid()).child("friends").child("currentFriends").child(loggedInUser.getUid()).setValue(temp);

                friends.remove(position);
                notifyDataSetChanged();


            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
                final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users").getRef();
                usersReference.child(loggedInUser.getUid()).child("friends").child("requests").child(friends.get(position).getUid()).removeValue();
                usersReference.child(friends.get(position).getUid()).child("friends").child("requests").child(loggedInUser.getUid()).removeValue();
                friends.remove(position);
                notifyDataSetChanged();

            }
        });
        return rowView;
    }
}
