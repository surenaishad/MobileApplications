package edu.uncc.mysocialapp.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.pojo.Post;

/**
 * Created by hemva on 18-Nov-17.
 */

public class WallPostsAdapter extends ArrayAdapter {

    private Activity context;
    private ArrayList<Post> posts;
    private DataSnapshot userDataSnapshot;
    private ArrayList<Post> fullList;
    public WallPostsAdapter(@NonNull Activity context, ArrayList<Post> posts, DataSnapshot userDataSnapshot, ArrayList<Post> lastTwoDaysList, ArrayList<Post> beforeTwoDaysList) {
        super(context, R.layout.wall_item, lastTwoDaysList);
        this.context = context;
        this.fullList = beforeTwoDaysList;
        this.posts = lastTwoDaysList;
        this.userDataSnapshot = userDataSnapshot;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.wall_item, null, true);

        TextView username = (TextView) rowView.findViewById(R.id.wallItemUsername);
        TextView time = (TextView) rowView.findViewById(R.id.wallItemTime);
        TextView message = (TextView) rowView.findViewById(R.id.wallItemMessage);
        ImageView deleteButton = (ImageView) rowView.findViewById(R.id.wallItemDeleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false)
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                posts.remove(position);
                                notifyDataSetChanged();
                                fullList.addAll(posts);
                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("posts").setValue(fullList);
                            }
                        }).setTitle("Delete Confirmation")
                        .setMessage("Are you sure?");
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        username.setText(userDataSnapshot.child("username").getValue().toString());
        PrettyTime prettyTime = new PrettyTime();

        time.setText(prettyTime.format(posts.get(position).getTime()));
        message.setText(posts.get(position).getMessage());

        return rowView;
    }
}
