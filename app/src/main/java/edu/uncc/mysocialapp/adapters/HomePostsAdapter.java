package edu.uncc.mysocialapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Calendar;

import edu.uncc.mysocialapp.R;
import edu.uncc.mysocialapp.activities.FriendPosts;
import edu.uncc.mysocialapp.activities.Home_Activity;
import edu.uncc.mysocialapp.activities.User_Wall;
import edu.uncc.mysocialapp.pojo.Post;

/**
 * Created by hemva on 17-Nov-17.
 */

public class HomePostsAdapter extends ArrayAdapter<Post> {

    private Activity context;
    private ArrayList<Post> posts;
    private DataSnapshot users;
    public HomePostsAdapter(@NonNull Activity context, ArrayList<Post> posts, DataSnapshot users, ArrayList<Post> subList) {
        super(context, R.layout.home_item, subList);
        this.context = context;
        this.posts = subList;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.home_item, null, true);

        TextView username = (TextView) rowView.findViewById(R.id.homeItemUsername);
        TextView time = (TextView) rowView.findViewById(R.id.homeItemTime);
        TextView message = (TextView) rowView.findViewById(R.id.homeItemMessage);

        username.setText(users.child(posts.get(position).getUid()).child("username").getValue().toString());
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof Home_Activity){
                    if(posts.get(position).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        Intent userWallIntent = new Intent(context, User_Wall.class);
                        context.startActivity(userWallIntent);
                    }else{
                        Intent friendIntent = new Intent(context, FriendPosts.class);
                        friendIntent.putExtra("friendPost", posts.get(position));
                        context.startActivity(friendIntent);
                    }
                }


            }
        });
        PrettyTime prettyTime = new PrettyTime();
       /* DateFormat df = new SimpleDateFormat();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date startDate = null;
        try {
            startDate = df.parse(posts.get(position).getTime().getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        time.setText(prettyTime.format(posts.get(position).getTime()));
        //time.setText(posts.get(position).getTime());
        message.setText(posts.get(position).getMessage());

        return rowView;
    }
}
