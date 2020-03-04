package glirt.motun.glirt2.GeneralUsers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import glirt.motun.glirt2.Adapter.MessageAdapter;
import glirt.motun.glirt2.Model.Chat;
import glirt.motun.glirt2.Model.User;
import glirt.motun.glirt2.R;
import glirt.motun.glirt2.TheMedia;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.example.kirk.Fragment.APIService2;

public class MessagingActivity extends AppCompatActivity implements TheMedia {
    CircleImageView profile_image;
    TextView username;

    private final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private final String SERVER_KEY = "AAAA4H0l-CY:APA91bFuDQ0v_iFHVFXQ4NfZTe9kbYJC3EpP8cNGv7yioKXEUSLav7fO0KH7Kq6RBfmwo3PFbN6Vd95lr5c4eJKsJa-slEgdxz0PN5VB7Z4bmSB84AWgCAqKhbyrLALc_EJOOfz3NT5h";


    boolean hasSentMessage = false;


    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;
    String userid;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;
    //    APIService apiService;
    boolean notify = false;


    boolean isImage = false;

    TextView removeimag, removeAudio, removeVideo;
    LinearLayout audioPane, videopane;

    ImageView selectPicture, selectAudio, selectVideo;
    ImageView img;
    private Uri mImageUri;
    private Uri audioUri;
    private Uri videoUri;
    MediaPlayer audioPlayer;
    private boolean isAudio = false;
    private boolean isVideo = false;

    VideoView videoView;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            audioPlayer.stop();
            audioPlayer.release();
        } catch (Exception e) {
            //
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            audioPlayer.stop();
            audioPlayer.release();
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onBackPressed() {
        try{
            takeMyMedia.stopMedia();
        }catch (Exception e){
            //
        }
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        requestQueue = Volley.newRequestQueue(this.getApplicationContext());

        videoView = findViewById(R.id.video);
        videopane = findViewById(R.id.videoPane);
        removeVideo = findViewById(R.id.removeVideo);

        selectVideo = findViewById(R.id.selectVideo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService2.class);

        selectPicture = findViewById(R.id.selectPicture);
        selectAudio = findViewById(R.id.selectAudio);

        audioPane = findViewById(R.id.audioPane);
        removeAudio = findViewById(R.id.removeAudio);

        audioPlayer = new MediaPlayer();

        removeimag = findViewById(R.id.removeImage);
        removeimag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImage = false;
                removeimag.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
            }
        });

        removeAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPane.setVisibility(View.GONE);
                audioPlayer.stop();
                audioPlayer = new MediaPlayer();
                isAudio = false;
            }
        });

        removeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videopane.setVisibility(View.GONE);
                isVideo = false;
            }
        });

        selectAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImage) {
                    isImage = false;
                    removeimag.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                }
                if (isVideo) {
                    videopane.setVisibility(View.GONE);
                    isVideo = false;
                }
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 991);
            }
        });

        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImage) {
                    isImage = false;
                    removeimag.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                }
                if (isAudio) {
                    audioPane.setVisibility(View.GONE);
                    audioPlayer.stop();
                    isAudio = false;
                }
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 990);
            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        img = findViewById(R.id.img);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                final String msg = text_send.getText().toString();
                try{
                    takeMyMedia.stopMedia();
                }catch (Exception e){
                    //
                }
                if (isImage) {
                    final ProgressDialog progressDialog = new ProgressDialog(MessagingActivity.this);
                    progressDialog.setMessage("Sending...Please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference("ChatImages").child(mImageUri.toString());
                    storageReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    sendMessage(fuser.getUid(), userid, "thisIsAnImage$$#908###()" + msg + "endOfImageCaption$$%%^^&&--" + task.getResult().toString());
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MessagingActivity.this, "Couldn't send message", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, "Couldn't send message", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (isAudio) {
                    final ProgressDialog progressDialog = new ProgressDialog(MessagingActivity.this);
                    progressDialog.setMessage("Sending...Please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference("ChatAudios").child(audioUri.toString());
                    storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    sendMessage(fuser.getUid(), userid, "thisIsAAudio$$#908###()" + msg + "endOfAudioCaption$$%%^^&&--" + task.getResult().toString());
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MessagingActivity.this, "Couldn't send message", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, "Couldn't send message", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (isVideo) {
                    final ProgressDialog progressDialog = new ProgressDialog(MessagingActivity.this);
                    progressDialog.setMessage("Sending...Please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference("ChatVideos").child(videoUri.toString());
                    storageReference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    sendMessage(fuser.getUid(), userid, "thisIsAVideo$$#908###()" + msg + "endOfVideoCaption$$%%^^&&--" + task.getResult().toString());
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MessagingActivity.this, "Couldn't send message", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MessagingActivity.this, "Couldn't send message", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg);

                } else {
                    Toast.makeText(MessagingActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });


        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageurl().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    try {
                        Glide.with(MessagingActivity.this).load(user.getImageurl()).into(profile_image);
                    } catch (Exception e) {
                    }
                }
                readMessages(fuser.getUid(), userid, user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        selectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudio) {
                    audioPane.setVisibility(View.GONE);
                    audioPlayer.stop();
                    isAudio = false;
                }
                videopane.setVisibility(View.GONE);
                isVideo = false;
                CropImage.activity().start(MessagingActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            isImage = true;
            mImageUri = result.getUri();
            img.setVisibility(View.VISIBLE);
            removeimag.setVisibility(View.VISIBLE);
            img.setImageURI(mImageUri);
        } else if (requestCode == 991 && resultCode == RESULT_OK) {
            isAudio = true;
            audioUri = data.getData();
            audioPane.setVisibility(View.VISIBLE);
            audioPlayer = new MediaPlayer();
            try {
                audioPlayer.setDataSource(MessagingActivity.this, audioUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                audioPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startAudio();
        } else if (requestCode == 990 && resultCode == RESULT_OK) {
            videopane.setVisibility(View.VISIBLE);
            isVideo = true;
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            MediaController controller = new MediaController(this);
            controller.setAnchorView(videoView);
            videoView.setMediaController(controller);
        } else {
            isImage = false;
            isAudio = false;
            img.setVisibility(View.GONE);
            removeimag.setVisibility(View.GONE);
            audioPane.setVisibility(View.GONE);
            Toast.makeText(this, "Couldn't load attachment", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAudio() {
        final SeekBar seekBar = findViewById(R.id.audioSeekBars);
        final ImageView pausePlay = findViewById(R.id.pausePlays);

        final boolean[] changedByTimer = {false};


        pausePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessagingActivity.this, "starting", Toast.LENGTH_LONG).show();

                if (audioPlayer.isPlaying()) {
                    pausePlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    audioPlayer.pause();
                } else {
                    pausePlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    audioPlayer.start();
                }

                seekBar.setMax(audioPlayer.getDuration());
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (!changedByTimer[0]) {
                            audioPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        changedByTimer[0] = false;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                changedByTimer[0] = true;
                try {
                    seekBar.setProgress(audioPlayer.getCurrentPosition());
                    if (audioPlayer.getCurrentPosition() == audioPlayer.getDuration()) {
                        pausePlay.setImageDrawable(getResources().getDrawable(R.drawable.play_white));
                        audioPlayer.stop();
                        seekBar.setProgress(0);
                    }
                } catch (Exception e) {
                    //nothing
                }

            }
        }, 0, 1000);

    }

    private RequestQueue requestQueue;

    private void sendMessage(final String sender, final String receiver, final String message) {
        img.setVisibility(View.GONE);
        removeimag.setVisibility(View.GONE);
        audioPlayer.release();
        isImage = false;
        audioPane.setVisibility(View.GONE);
        isAudio = false;
        isVideo = false;
        videopane.setVisibility(View.GONE);

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase.getInstance().getReference().child("Chats").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender", sender);
                        hashMap.put("receiver", receiver);
                        hashMap.put("message", message);
                        hashMap.put("count", (int) (dataSnapshot.getChildrenCount() + 1));
                        chatref.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hasSentMessage = true;
                            }
                        });


//                        Log.d("hhhh", receiver);
//                        String topic = "/topics/" + receiver;
//                        JSONObject notification = new JSONObject();
//                        JSONObject notificationBody = new JSONObject();
//                        try {
//                            notificationBody.put("title", fuser.getEmail() + " sent a message");
//                            notificationBody.put("message", message);
//                            notification.put("to", topic);
//                            notification.put("data", notificationBody);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        sendNotification(notification);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                }
        );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(sender);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                String username = user.getUsername();
                Log.d("hhhh", receiver);
                String topic = "/topics/" + receiver;
                JSONObject notification = new JSONObject();
                JSONObject notificationBody = new JSONObject();
                try {
                    notificationBody.put("title", username + " sent a message");
                    notificationBody.put("message", message);
                    notification.put("to", topic);
                    notification.put("data", notificationBody);
                    sendNotification(notification);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(MessagingActivity.this, "Sent", Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessagingActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);

    }


    private void readMessages(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }
                    new Intent(MessagingActivity.this, MessagingActivity.class);
                    messageAdapter = new MessageAdapter(MessagingActivity.this, mchat, imageurl, MessagingActivity.this);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    TakeMyMedia takeMyMedia;

    @Override
    public void take(TakeMyMedia takeMyMedia) {
        this.takeMyMedia = takeMyMedia;
    }

    @Override
    public void requestPermission() {
        String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE} ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 9999);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9999) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted...Try download now", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface TakeMyMedia {
        void stopMedia();
    }

}
