package glirt.motun.glirt2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import glirt.motun.glirt2.R;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private final String ADMIN_CHANNEL_id = "ADMIN_CHANNEL";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, RecentChatssActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificatioID = new Random().nextInt(3000);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            assert notificationManager != null;
            setUpChannels(notificationManager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
          this, 0, intent, PendingIntent.FLAG_ONE_SHOT
        );

        Bitmap largeIcon = BitmapFactory.decodeResource(
                getResources(), R.drawable.glirt
        );

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_id)
                .setSmallIcon(R.drawable.glirt)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        }

        assert notificationManager != null;
        notificationManager.notify(notificatioID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpChannels(NotificationManager notificationManager) {
        String adminChannelName = "Kptufwf";
        String adminChannelDescription = "Device to device notification";

        NotificationChannel adminChannel = new NotificationChannel(
          ADMIN_CHANNEL_id,
          adminChannelName,
          NotificationManager.IMPORTANCE_HIGH
        );

        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.GREEN);
        adminChannel.enableVibration(true);
        notificationManager.createNotificationChannel(adminChannel);
    }
}
