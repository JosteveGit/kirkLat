package glirt.motun.glirt2.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import glirt.motun.glirt2.Adapter.ChatUserAdapter;
import glirt.motun.glirt2.Message;
import glirt.motun.glirt2.Model.Chat;
import glirt.motun.glirt2.Model.User;
import glirt.motun.glirt2.Notification.Token;
import glirt.motun.glirt2.OneViewModel;
import glirt.motun.glirt2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class GchatFragment extends Fragment {
    private RecyclerView recyclerView;

    private ChatUserAdapter chatUserAdapter;
    private List<Message> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<String> usersList;

    private OneViewModel oneViewModel;

    private Context context;

    public GchatFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gchat, container, false);

        recyclerView = view.findViewById(R.id.recycler_viewd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatUserAdapter = new ChatUserAdapter(getContext(), new ArrayList<Message>(), true, null);
        recyclerView.setAdapter(chatUserAdapter);


        mUsers = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        oneViewModel = ViewModelProviders.of(this).get(OneViewModel.class);
        getEveryMessage();


//        usersList = new ArrayList<>();
//
//        reference = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat = snapshot.getValue(Chat.class);
//
//                    if (chat.getSender().equals(fuser.getUid())){
//                        usersList.add(chat.getReceiver());
//                    }
//                    if (chat.getReceiver().equals(fuser.getUid())){
//                        usersList.add(chat.getSender());
//                    }
//
//                }
//
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
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
        final List<Chat> chats = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                String theIdToSearchFor = fuser.getUid();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Chat chat = dataSnapshot1.getValue(Chat.class);
                    if (Objects.requireNonNull(chat).getSender().equals(theIdToSearchFor) || chat.getReceiver().equals(theIdToSearchFor)) {
                        chats.add(chat);
                    }
                }

                for (final Chat chat1 : newRemoveRedundancies(chats)) {

                    FirebaseDatabase.getInstance().getReference("Users").child(chat1.getReceiver().equals(theIdToSearchFor) ? chat1.getSender() : chat1.getReceiver()).addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
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

        oneViewModel.get_messages().observe((LifecycleOwner) context, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                chatUserAdapter = new ChatUserAdapter(getContext(), mUsers, true, null);
                chatUserAdapter.notifyDataSetChanged();
                Log.d("SettingAdapter", "YES");
                recyclerView.setAdapter(chatUserAdapter);
            }
        });
    }

    private List<Chat> arrangeChat(List<Chat> chats) {
        Collections.sort(chats, new Comparator<Chat>() {
            @Override
            public int compare(Chat o1, Chat o2) {
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

    private List<Chat> newRemoveRedundancies(List<Chat> result) {
        List<Chat> temporal = new ArrayList<>();
        for (Chat chat : result) {
            if (temporal.isEmpty()) {
                temporal.add(chat);
            } else {
                boolean foundMatch = false;
                for (Chat chat1 : temporal) {
                    if ((chat.getSender().equals(chat1.getSender()) && chat.getReceiver().equals(chat1.getReceiver())) || (chat.getSender().equals(chat1.getReceiver()) && chat.getReceiver().equals(chat1.getSender()))) {
                        if (chat1.getCount() < chat.getCount()) {
                            //swap
                            chat1.setMessage(chat.getMessage());
                            chat1.setReceiver(chat.getReceiver());
                            chat1.setSender(chat.getSender());
                            chat1.setCount(chat.getCount());
                            foundMatch = true;
                            break;
                        }
                    }
                }
                if (!foundMatch) {
                    temporal.add(chat);
                }
            }
        }
        return arrangeChat(temporal);
    }


}
