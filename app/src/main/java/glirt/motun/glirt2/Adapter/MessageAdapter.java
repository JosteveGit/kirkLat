package glirt.motun.glirt2.Adapter;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import glirt.motun.glirt2.GeneralUsers.MessagingActivity;
import glirt.motun.glirt2.Model.Chat;
import glirt.motun.glirt2.R;
import glirt.motun.glirt2.TheMedia;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements MessagingActivity.TakeMyMedia{
    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mchat;
    private String imageurl;


    FirebaseUser fuser;
    List<MediaPlayer> mediaPlayers = new ArrayList<>();



    TheMedia theMedia;




    public MessageAdapter(Context mContext, List<Chat> mchat, String imageurl, TheMedia theMedia){
        this.mchat = mchat;
        this.mContext = mContext;
        this.imageurl = imageurl;
        this.theMedia = theMedia;
        theMedia.take(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Chat chat = mchat.get(holder.getAdapterPosition());
        final boolean[] changedByTimer = {false};


        if(chat.getMessage().contains("thisIsAnImage$$#908###()")){
            String mess;
            mess = chat.getMessage().replace("endOfImageCaption$$%%^^&&--", "`");
            mess = mess.replace("thisIsAnImage$$#908###()", "");

            String message = mess.substring(0, mess.indexOf('`'));
            final String imageUrl = mess.substring(mess.indexOf('`')+1);

            holder.audioPane.setVisibility(View.GONE);
            holder.videoPane.setVisibility(View.GONE);

            holder.imagePane.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.GONE);
            holder.showMessage2.setText(message);
            Glide.with(mContext).load(imageUrl).placeholder(R.drawable.image_placeholder).into(holder.image);

            holder.downloadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDownloading(imageUrl, "Image");
                }
            });
        }
        else if(chat.getMessage().contains("thisIsAAudio$$#908###()")){
            holder.imagePane.setVisibility(View.GONE);
            holder.videoPane.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.GONE);
            String mess;
            mess = chat.getMessage().replace("thisIsAAudio$$#908###()","");
            mess = mess.replace("endOfAudioCaption$$%%^^&&--", "`");

            final String audioUrl = mess.substring(mess.indexOf('`')+1);

            final MediaPlayer mediaPlayer = new MediaPlayer();

            final boolean[] isLoaded = {false};

            holder.downloadAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDownloading(audioUrl, "Audio");
                }
            });

            holder.pausePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if(!isLoaded[0]){
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(audioUrl);
                            mediaPlayer.prepare();
                            isLoaded[0] = true;
                            mediaPlayers.add(mediaPlayer);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        holder.pausePlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_white));
                    }else{
//                        mediaPlayer.release();
//                        mediaPlayer.reset();
                        mediaPlayer.start();
                        holder.pausePlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pause_white));
                    }

                    holder.seekBar.setMax(mediaPlayer.getDuration());
                    holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if(!changedByTimer[0]){
                                mediaPlayer.seekTo(progress);
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


                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            changedByTimer[0] = true;
                            try{
                                holder.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration()){
                                    holder.pausePlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_white));
                                    mediaPlayer.stop();
                                    holder.seekBar.setProgress(0);
                                }else if(!mediaPlayer.isPlaying()){
                                    holder.seekBar.setProgress(0);
                                    holder.pausePlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.play_white));
                                }
                            }catch (IllegalStateException e){
                                Log.d("nothing","nothing");
                            }
                        }
                    },0,1000);

                }
            });



            holder.audioPane.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.GONE);
            holder.show_message.setText(audioUrl);

        }

        else if(chat.getMessage().contains("thisIsAVideo$$#908###()")){



            holder.audioPane.setVisibility(View.GONE);
            holder.imagePane.setVisibility(View.GONE);

            String mess;
            mess = chat.getMessage().replace("endOfVideoCaption$$%%^^&&--", "`");
            mess = mess.replace("thisIsAVideo$$#908###()", "");




            String message = mess.substring(0, mess.indexOf('`'));
            final String videoUrl = mess.substring(mess.indexOf('`')+1);

            holder.downloadVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDownloading(videoUrl, "Video");
                }
            });

            holder.videoPane.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.GONE);
            holder.showMessage3.setText(message);

            Uri uri = Uri.parse(videoUrl);
            holder.video.setVideoURI(uri);
            MediaController ctlr = new MediaController(mContext);
            ctlr.setAnchorView(holder.video);
            holder.video.setMediaController(ctlr);

        }
        else{

            holder.imagePane.setVisibility(View.GONE);
            holder.audioPane.setVisibility(View.GONE);
            holder.videoPane.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);
            holder.show_message.setText(chat.getMessage());
        }

        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher );
        }else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    private void startDownloading(String theUrl, String type) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                theMedia.requestPermission();
            }else {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(theUrl));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setTitle(type+" Download");
                request.setDescription("Downloading "+type+"...");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, ""+System.currentTimeMillis());

                DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                assert manager != null;
                manager.enqueue(request);
            }
        }else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(theUrl));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setTitle(type+" Download");
            request.setDescription("Downloading "+type+"...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, ""+System.currentTimeMillis());

            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            assert manager != null;
            manager.enqueue(request);
        }

    }


    @Override
    public void stopMedia() {
        for(MediaPlayer mediaPlayer: mediaPlayers){
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayers.clear();
    }



    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message, showMessage2;
        public ImageView profile_image;

        ImageView image;
        LinearLayout imagePane, audioPane;
        ImageView pausePlay;
        SeekBar seekBar;

        LinearLayout videoPane;
        VideoView video;
        TextView showMessage3;

        ImageView downloadImage, downloadVideo, downloadAudio;



        public ViewHolder(View itemView) {
            super(itemView);

            show_message  = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);

            image = itemView.findViewById(R.id.image);
            showMessage2 = itemView.findViewById(R.id.show_message2);

            imagePane = itemView.findViewById(R.id.imagePane);

            audioPane = itemView.findViewById(R.id.audioPane);
            pausePlay = itemView.findViewById(R.id.pausePlay);
            seekBar = itemView.findViewById(R.id.audioSeekBar);

            videoPane = itemView.findViewById(R.id.videoPane);
            video = itemView.findViewById(R.id.video);
            showMessage3 = itemView.findViewById(R.id.show_message3);

            downloadImage = itemView.findViewById(R.id.downloadImage);
            downloadAudio = itemView.findViewById(R.id.downloadAudio);
            downloadVideo = itemView.findViewById(R.id.downloadVideo);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mchat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
