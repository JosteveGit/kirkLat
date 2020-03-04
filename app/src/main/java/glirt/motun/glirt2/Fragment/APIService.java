package glirt.motun.glirt2.Fragment;

import glirt.motun.glirt2.Notification.MyResponse;
import glirt.motun.glirt2.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface  APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA4H0l-CY:APA91bFuDQ0v_iFHVFXQ4NfZTe9kbYJC3EpP8cNGv7yioKXEUSLav7fO0KH7Kq6RBfmwo3PFbN6Vd95lr5c4eJKsJa-slEgdxz0PN5VB7Z4bmSB84AWgCAqKhbyrLALc_EJOOfz3NT5h"
            }
    )

    @POST("fcm/send")
    Call <MyResponse> sendNotification(@Body Sender body);
}

