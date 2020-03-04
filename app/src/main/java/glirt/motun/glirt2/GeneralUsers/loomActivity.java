package glirt.motun.glirt2.GeneralUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import glirt.motun.glirt2.Adapter.MyAdapter;
import glirt.motun.glirt2.Model.Job;
import glirt.motun.glirt2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class loomActivity extends AppCompatActivity {
    DatabaseReference ref;
    SearchView searchView;
    MyAdapter adapter;
    RecyclerView recyclerView;
    EditText username, profession, description, address;
    ArrayList<Job> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loom);
        ref = FirebaseDatabase.getInstance().getReference("JOB").child("UserProfession");
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.search_view);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(ref != null){
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        list = new ArrayList<Job>();
                        //Deal pp = dataSnapshot.getValue(Deal.class);
                        //list.add(pp);
                        for(DataSnapshot ds : dataSnapshot.getChildren() ){
                            Job dl =dataSnapshot.getValue(Job.class);
                            list.add(dl);}
                        MyAdapter Adaptor= new MyAdapter(list);
                        recyclerView.setAdapter(Adaptor);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(loomActivity.this, "Error to fatch", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }

    }

    private void search(String sr) {
        ArrayList<Job> mylist = new ArrayList<>();
        for(Job object : list){
            if(object.getUserProfession().toUpperCase().contains(sr.toUpperCase())){
                mylist.add(object);
            }
        }
        MyAdapter adapter = new MyAdapter(mylist);
        recyclerView.setAdapter(adapter);
    }
}
