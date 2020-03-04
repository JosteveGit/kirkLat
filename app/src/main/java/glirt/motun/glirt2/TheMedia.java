package glirt.motun.glirt2;

import glirt.motun.glirt2.GeneralUsers.MessagingActivity;

public interface TheMedia{
    void take(MessagingActivity.TakeMyMedia takeMyMedia);

    void requestPermission();
}
