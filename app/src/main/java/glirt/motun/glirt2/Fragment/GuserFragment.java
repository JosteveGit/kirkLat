package glirt.motun.glirt2.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import glirt.motun.glirt2.Adapter.ChatUserAdapter;
import glirt.motun.glirt2.Model.User;
import glirt.motun.glirt2.OneViewModel;
import glirt.motun.glirt2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GuserFragment extends Fragment {
    private RecyclerView recyclerView;

    private ChatUserAdapter chatuserAdapter;
    private List<User> mUsers;

    private OneViewModel oneViewModel;


    EditText search_users;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_guser, container, false);
        recyclerView = view.findViewById(R.id.recycler_views);
        chatuserAdapter = new ChatUserAdapter(getContext(), null, false, new ArrayList<User>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(chatuserAdapter);

        oneViewModel = ViewModelProviders.of(this).get(OneViewModel.class);

        mUsers = new ArrayList<>();
        readUsers();
        search_users = view.findViewById(R.id.search_users);

        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return view;
    }

    private void searchUsers(String s){

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")

                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mUsers.add(user);
                }

                chatuserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                        oneViewModel.set_users(mUsers);

                    }
                }

                oneViewModel.get_users().observe((LifecycleOwner) getContext(), new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {
                        chatuserAdapter = new ChatUserAdapter(getContext(), null, false, mUsers);
                        chatuserAdapter.notifyDataSetChanged();
                        Log.d("SeAdapter", "YES");
                        recyclerView.setAdapter(chatuserAdapter);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
