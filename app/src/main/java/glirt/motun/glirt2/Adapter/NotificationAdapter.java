package glirt.motun.glirt2.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import glirt.motun.glirt2.Fragment.ProfileFragment;
import glirt.motun.glirt2.Fragment.PostDetailFragment;
import glirt.motun.glirt2.Model.Notification;
import glirt.motun.glirt2.Model.Post;
import glirt.motun.glirt2.Model.User;
import glirt.motun.glirt2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

        private Context mContext;
        private List<Notification> mNotification;

        public NotificationAdapter(Context context, List<Notification> notification){
            mContext = context;
            mNotification = notification;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewholder, final int position) {

            final Notification notification = mNotification.get(position);

            viewholder.text.setText(notification.getText());

            getUserInfo(viewholder.image_profile, viewholder.username, notification.getUserid());

            if (notification.isIspost()) {
                viewholder.post_image.setVisibility(View.VISIBLE);
                getPostImage(viewholder.post_image, notification.getPostid());
            } else {
                viewholder.post_image.setVisibility(View.GONE);
            }

            viewholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (notification.isIspost()) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("postid", notification.getPostid());
                        editor.apply();

                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new PostDetailFragment()).commit();
                    } else {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", notification.getUserid());
                        editor.apply();

                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                    }
                }
            });



        }
        //
        @Override
        public int getItemCount() {
            return mNotification.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView image_profile, post_image;
            public TextView username, text;

            public ViewHolder(View itemView) {
                super(itemView);

                image_profile = itemView.findViewById(R.id.image_profile);
                post_image = itemView.findViewById(R.id.post_image);
                username = itemView.findViewById(R.id.username);
                text = itemView.findViewById(R.id.comment);
            }
        }

        private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(publisherid);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(mContext).load(user.getImageurl()).into(imageView);
                    username.setText(user.getUsername());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void getPostImage(final ImageView post_image, String postid){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Posts").child(postid);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    Glide.with(mContext).load(post.getPostimage()).into(post_image);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
}
