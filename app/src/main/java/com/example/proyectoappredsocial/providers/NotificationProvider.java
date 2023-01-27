package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.FCMBody;
import com.example.proyectoappredsocial.models.FCMResponse;
import com.example.proyectoappredsocial.retrofit.IFCMApi;
import com.example.proyectoappredsocial.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url ="https://fcm.googleapis.com";
    public NotificationProvider(){

    }
    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
