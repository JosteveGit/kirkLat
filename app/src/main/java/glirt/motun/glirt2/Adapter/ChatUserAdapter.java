package glirt.motun.glirt2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import glirt.motun.glirt2.GeneralUsers.MessagingActivity;
import glirt.motun.glirt2.Message;
import glirt.motun.glirt2.Model.User;
import glirt.motun.glirt2.R;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {
    private Context mContext;
    private List<Message> mUsers;
    private boolean ischat;
    private List<User> users;



    public ChatUserAdapter(Context mContext, List<Message> mUsers, boolean ischat, List<User> users) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        this.users = users;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chatuser_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (users == null) {
                if(mUsers.get(position).message.contains("thisIsAnImage$$#908###()")){
                    String mess;
                    mess = mUsers.get(position).message.replace("endOfImageCaption$$%%^^&&--", "`");
                    mess = mess.replace("thisIsAnImage$$#908###()", "");
                    String message = mess.substring(0, mess.indexOf('`'));
                    holder.last_msg.setText(message);
                }else if(mUsers.get(position).message.contains("thisIsAAudio$$#908###()")){
                    holder.last_msg.setText("Audio file...");
                }else  if(mUsers.get(position).message.contains("thisIsAVideo$$#908###()")){


                    String mess;
                    mess =  mUsers.get(position).message.replace("endOfVideoCaption$$%%^^&&--", "`");
                    mess = mess.replace("thisIsAVideo$$#908###()", "");
                    String message = mess.substring(0, mess.indexOf('`'));

                    holder.last_msg.setText(message);

                }
                else{
                    holder.last_msg.setText(mUsers.get(position).message);
                }
                final User user = mUsers.get(holder.getAdapterPosition()).user;
                holder.username.setText(user.getUsername());
                if (user.getImageurl().equals("default")) {
                    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    try {
                        Glide.with(mContext).load(user.getImageurl()).into(holder.profile_image);
                    } catch (Exception e) {
                    }
                }

                if (ischat) {
                    if(user.getStatus()!=null){
                        if (user.getStatus().equals("online")) {
                            holder.img_on.setVisibility(View.VISIBLE);
                            holder.img_off.setVisibility(View.GONE);
                        } else {
                            holder.img_on.setVisibility(View.GONE);
                            holder.img_off.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    holder.img_on.setVisibility(View.GONE);
                    holder.img_off.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, MessagingActivity.class);
                        intent.putExtra("userid", user.getId());
                        mContext.startActivity(intent);
                    }
                });
            } else {
                final User user = users.get(position);
                holder.username.setText(user.getUsername());
                if (user.getImageurl().equals("default")) {
                    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    try {
                        Glide.with(mContext).load(user.getImageurl()).into(holder.profile_image);
                    } catch (Exception e) {
                    }
                }

                if (ischat) {
                    if (user.getStatus().equals("online")) {
                        holder.img_on.setVisibility(View.VISIBLE);
                        holder.img_off.setVisibility(View.GONE);
                    } else {
                        holder.img_on.setVisibility(View.GONE);
                        holder.img_off.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.img_on.setVisibility(View.GONE);
                    holder.img_off.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, MessagingActivity.class);
                        intent.putExtra("userid", user.getId());
                        mContext.startActivity(intent);
                    }
                });
            }
    }

    @Override
    public int getItemCount() {
        int value = 0;
        if (mUsers!=null){
            value = mUsers.size();
        }else if(users!=null){
            value = users.size();
        }
        return value;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);


        }
    }
}
