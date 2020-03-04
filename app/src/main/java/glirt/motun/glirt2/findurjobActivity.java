package glirt.motun.glirt2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import glirt.motun.glirt2.Model.SearchData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import glirt.motun.glirt2.R;

import java.util.ArrayList;
import java.util.List;

public class findurjobActivity extends AppCompatActivity {
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://kirk-30f6a.firebaseio.com/");
    private final List<SearchData> searchDataList = new ArrayList<>();
    EditText pop;

    private boolean watch = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findurjob);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ENTERTAINMENT MARKET");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        final Button nothing = findViewById(R.id.nothing);
        pop = findViewById(R.id.pop);
        monitorSearch();

        nothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchDataList.size() == 0) {
                    firebaseDatabase.getReference().child("JOB").addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                String location = data.child("userLocation").getValue(String.class);
                                String name = data.child("userName").getValue(String.class);
                                String phone = data.child("userNumber").getValue(String.class);
                                String profession = data.child("userProfession").getValue(String.class);
                                String address = data.child("userAddress").getValue(String.class);
                                String description = data.child("userDescription").getValue(String.class);
                                SearchData searchData1 = new SearchData(name, phone, profession, location, address, description);
                                searchDataList.add(searchData1);

                                RecyclerView recyclerView = findViewById(R.id.recyclerview);
                                recyclerView.setLayoutManager(new LinearLayoutManager(findurjobActivity.this));
                                recyclerView.setAdapter(new RecyclerAdapter(filterList()));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(findurjobActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    RecyclerView recyclerView = findViewById(R.id.recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(findurjobActivity.this));
                    recyclerView.setAdapter(new RecyclerAdapter(filterList()));
                    searchDataList.clear();
                }
            }
        });
    }

    private List<SearchData> filterList() {
        String profession = "";
        boolean postToProfession = true;
        String location = "";
        for (int i = 0; i < pop.getText().toString().length(); i++) {
            if (postToProfession) {
                if (pop.getText().toString().charAt(i) != ' ') {
                    profession += Character.toString(pop.getText().toString().charAt(i));
                } else {
                    postToProfession = false;
                }
            } else {
                location += Character.toString(pop.getText().toString().charAt(i));
            }
        }

        List<SearchData> list = new ArrayList<>();
        boolean noData = true;
        for (SearchData searchData : searchDataList) {
            if (searchData.getLocation().toLowerCase().contains(location.toLowerCase()) || location.toLowerCase().contains(searchData.getLocation().toLowerCase())) {
                if (profession.toLowerCase().contains(searchData.getProfession().toLowerCase()) || searchData.getProfession().toLowerCase().contains(profession.toLowerCase())) {
                    list.add(searchData);
                    noData = false;
                }
            }
        }

        if (noData) {
            Toast.makeText(this, "No data found", Toast.LENGTH_LONG).show();
        }

        return list;
    }

    private void monitorSearch() {
        pop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (watch) {
                    if (s.toString().contains(" ")) {
                        watch = false;
                        pop.setText(pop.getText().toString().trim() + " In ");
                    }
                } else {
                    if (!s.toString().contains(" ")) {
                        watch = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
