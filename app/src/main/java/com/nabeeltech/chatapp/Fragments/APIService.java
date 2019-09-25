package com.nabeeltech.chatapp.Fragments;

import com.nabeeltech.chatapp.Notifications.MyResponse;
import com.nabeeltech.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAApLsbHcM:APA91bFYFVMy6H2bR6O-tLo3ltDqUaXDknikridmRlp7YC9r_1dtRD5kcn3Jx9Gr9uWmZkyTisl4BBbQmfxGTMhSBnydKTXMt_gTns1LFUibEMVrK-SN0c-pBjLNzrC2nbhnEpGUOWl_"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
