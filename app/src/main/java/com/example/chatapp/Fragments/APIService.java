package com.example.chatapp.Fragments;


import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAl9F2lRU:APA91bHDFG8SeBhJ7QeeLu6K-_85K5xEl-e8wg6zoOteOGKQV9sriIijl15dSMMcZlMOCTR_dc_GQeveB25Iq6IWkqT9kbkp6lvYkkK087BqnxtI3GfChR8QrhrbVOyG7FtB5auvvGKd"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
