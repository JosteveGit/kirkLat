package glirt.motun.glirt2.GeneralUsers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import glirt.motun.glirt2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class createacctActivity extends AppCompatActivity {

    Button biz, user;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if (firebaseUser != null){
            Log.d("Here", firebaseUser.getUid());
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+firebaseUser.getUid());
            Intent intent = new Intent(createacctActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createacct);

        biz = findViewById(R.id.bizact);
        user =  findViewById(R.id.useract);

        biz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(createacctActivity.this, SignupActivity.class));
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(createacctActivity.this, LoginActivity.class));
            }
        });
    }
}
