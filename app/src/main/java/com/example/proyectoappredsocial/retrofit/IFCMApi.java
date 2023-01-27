package com.example.proyectoappredsocial.retrofit;

import com.example.proyectoappredsocial.models.FCMBody;
import com.example.proyectoappredsocial.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAaLoRJmY:APA91bET_QkZmcsHnMfvBMG9Dz3sEaLtFNY4bqImlUY5nhvREaTl6t1eyKiG2Ws_oJSDXLsF7k_neU1vITiO657fisex-cp2JLV2FC8d3KHGSsNCICRCzE8eVbIj4HF7ADjRb2GvgUst"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
