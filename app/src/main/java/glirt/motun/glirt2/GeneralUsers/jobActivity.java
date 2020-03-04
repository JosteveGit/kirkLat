package glirt.motun.glirt2.GeneralUsers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import glirt.motun.glirt2.Model.Job;
import glirt.motun.glirt2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class jobActivity extends AppCompatActivity {

    EditText user_edit_name,user_edit_jobdescription,user_edit_phoneNumber,user_edit_profession,user_edit_address,user_edit_location;
    Button btn_uploadData;
    FirebaseDatabase database;
    DatabaseReference user_data;
    String userStName,userStAddress,userStNumber,userStDescription,userStProfession,userStLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("E - JOB CREATOR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        database = FirebaseDatabase.getInstance();
        user_data = database.getReference("JOB");

        user_edit_name = (EditText)findViewById(R.id.user_name);
        user_edit_address = (EditText)findViewById(R.id.user_address);
        user_edit_phoneNumber = (EditText)findViewById(R.id.userphone_number);
        user_edit_jobdescription = (EditText)findViewById(R.id.user_description);
        user_edit_profession = (EditText)findViewById(R.id.user_profession);
        user_edit_location = (EditText)findViewById(R.id.user_location);



        btn_uploadData = (Button)findViewById(R.id.btn_upload_data);

        btn_uploadData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                userStName = user_edit_name.getText().toString();
                userStAddress = user_edit_address.getText().toString();
                userStNumber = user_edit_phoneNumber.getText().toString();
                userStDescription = user_edit_jobdescription.getText().toString();
                userStProfession = user_edit_profession.getText().toString();
                userStLocation = user_edit_location.getText().toString();

                user_data.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(userStName.isEmpty())
                        {
                            user_edit_name.setError("Please enter your name");
                            user_edit_name.requestFocus();
                        }
                        else if(userStAddress.isEmpty())
                        {
                            user_edit_address.setError("Please enter the Job Address");
                            user_edit_address.requestFocus();
                        }
                        else if(userStDescription.isEmpty())
                        {
                            user_edit_jobdescription.setError("Please enter your Job Description");
                            user_edit_jobdescription.requestFocus();
                        }
                        else if(userStProfession.isEmpty())
                        {
                            user_edit_profession.setError("Please enter the profession you are wanting to hire");
                            user_edit_profession.requestFocus();
                        }

                        else if(userStNumber.isEmpty() || userStNumber.length() < 11)
                        {
                            user_edit_phoneNumber.setError("Please enter valid number");
                            user_edit_phoneNumber.requestFocus();
                        }
                        else if(userStLocation.isEmpty())
                        {
                            user_edit_location.setError("Please enter location");
                            user_edit_location.requestFocus();
                        }
                        else
                        {
                            Job job = new Job(userStName,userStAddress,userStNumber,userStDescription,userStProfession,userStLocation);
                            user_data.child(userStDescription).setValue(job);
                            Toast.makeText(jobActivity.this, "Job is created.", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
