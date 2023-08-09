package com.example.homemanagement.api;

import com.example.homemanagement.model.JsonReadThingspeak;
import com.example.homemanagement.model.WriteData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    //    link API
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://api.thingspeak.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("channels/2187417/feeds.json")
    Call<JsonReadThingspeak> readDataThingspeak(@Query("api_key") String api_key,
                                                @Query("results") int results);

    //    https://api.thingspeak.com/update?api_key=3YLKCNPRL5NY6RQH&field3=1
    ApiService apiServiceControlL1 = new Retrofit.Builder()
            .baseUrl("https://api.thingspeak.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("update")
    Call<Integer> writeL1(@Query("api_key") String api_key,
                          @Query("field3") int field3);

    @GET("update")
    Call<Integer> writeL2(@Query("api_key") String api_key,
                          @Query("field4") int field4);

    @GET("update")
    Call<Integer> writeL3(@Query("api_key") String api_key,
                          @Query("field5") int field5);

    @GET("update")
    Call<Integer> writeL4(@Query("api_key") String api_key,
                          @Query("field6") int field6);

    @GET("update")
    Call<Integer> writeDoor(@Query("api_key") String api_key,
                            @Query("field7") int field7);

    @GET("update")
    Call<Integer> writeAll(@Query("api_key") String api_key,
                           @Query("field3") int field3,
                           @Query("field4") int field4,
                           @Query("field5") int field5,
                           @Query("field6") int field6);
}
