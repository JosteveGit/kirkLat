package glirt.motun.glirt2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import glirt.motun.glirt2.Adapter.ChatUserAdapter;
import glirt.motun.glirt2.Model.Chat;
import glirt.motun.glirt2.Model.User;
import glirt.motun.glirt2.Notification.Token;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import glirt.motun.glirt2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentChatssActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    private OneViewModel oneViewModel;
    private List<Message> mUsers;
    FirebaseUser fuser;
    private ChatUserAdapter chatUserAdapter;
    private RecyclerView recyclerView;




    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chatss);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        FloatingActionButton search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecentChatssActivity.this, NewChatActivity.class));
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageurl().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //change this
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        recyclerView = findViewById(R.id.recyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatUserAdapter = new ChatUserAdapter(this, new ArrayList<Message>(), true, null);
        recyclerView.setAdapter(chatUserAdapter);


        mUsers = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        oneViewModel = ViewModelProviders.of(this).get(OneViewModel.class);
        getEveryMessage();

    }

    //    private void readChats() {
//
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mUsers.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    User user = snapshot.getValue(User.class);
//
//                    // display 1 user from chats
//                    for (String id : usersList) {
//                        if (user.getId().equals(id)) {
//                            if (mUsers.size() != 0) {
//                                for (User user1 : mUsers) {
//                                    if (!user.getId().equals(user1.getId())) {
//                                        mUsers.add(user);
//                                    }
//                                }
//                            } else {
//                                mUsers.add(user);
//                            }
//                        }
//                    }
//                }
//
//                chatUserAdapter = new ChatUserAdapter(getContext(), mUsers, true);
//                recyclerView.setAdapter(chatUserAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void getEveryMessage() {
        final List<glirt.motun.glirt2.Model.Chat> chats = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                String theIdToSearchFor = fuser.getUid();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    glirt.motun.glirt2.Model.Chat chat = dataSnapshot1.getValue(glirt.motun.glirt2.Model.Chat.class);
                    if (Objects.requireNonNull(chat).getSender().equals(theIdToSearchFor) || chat.getReceiver().equals(theIdToSearchFor)) {
                        chats.add(chat);
                    }
                }

                for (final glirt.motun.glirt2.Model.Chat chat1 : newRemoveRedundancies(chats)) {

                    FirebaseDatabase.getInstance().getReference("Users").child(chat1.getReceiver().equals(theIdToSearchFor) ? chat1.getSender() : chat1.getReceiver()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    Log.d("user", user.getUsername());
                                    mUsers.add(new Message(user, chat1.getMessage()));
                                    oneViewModel.set_messages(mUsers);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            }
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        oneViewModel.get_messages().observe((LifecycleOwner) this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                chatUserAdapter = new ChatUserAdapter(RecentChatssActivity.this, mUsers, true, null);
                chatUserAdapter.notifyDataSetChanged();
                Log.d("SettingAdapter", "YES");
                recyclerView.setAdapter(chatUserAdapter);
            }
        });
    }

    private List<glirt.motun.glirt2.Model.Chat> arrangeChat(List<glirt.motun.glirt2.Model.Chat> chats) {
        Collections.sort(chats, new Comparator<glirt.motun.glirt2.Model.Chat>() {
            @Override
            public int compare(glirt.motun.glirt2.Model.Chat o1, glirt.motun.glirt2.Model.Chat o2) {
                return Integer.compare(o2.getCount(), o1.getCount());
            }
        });
        return chats;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private List<glirt.motun.glirt2.Model.Chat> newRemoveRedundancies(List<glirt.motun.glirt2.Model.Chat> result) {
        List<glirt.motun.glirt2.Model.Chat> temporal = new ArrayList<>();
        for (glirt.motun.glirt2.Model.Chat chat : result) {
            if (temporal.isEmpty()) {
                temporal.add(chat);
            } else {
                boolean foundMatch = false;
                for (Chat chat1 : temporal) {
                    if ((chat.getSender().equals(chat1.getSender()) && chat.getReceiver().equals(chat1.getReceiver())) || (chat.getSender().equals(chat1.getReceiver()) && chat.getReceiver().equals(chat1.getSender()))) {
                        Log.d("Found", "Yes");
                        if (chat1.getCount() < chat.getCount()) {
                            //swap
                            chat1.setMessage(chat.getMessage());
                            chat1.setReceiver(chat.getReceiver());
                            chat1.setSender(chat.getSender());
                            chat1.setCount(chat.getCount());
                        }
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch) {
                    temporal.add(chat);
                }
            }
        }
        Log.d("Size", temporal.size()+"");
        return arrangeChat(temporal);
    }


}

