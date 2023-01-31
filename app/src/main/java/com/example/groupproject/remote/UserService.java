package com.example.groupproject.remote;

import com.example.groupproject.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @FormUrlEncoded
    @POST("api/users/login")
    Call<User> login(@Field("username") String username, @Field("password") String password);

    @POST("api/users")
    Call<User> addUser(@Header ("api-key") String apiKey, @Body User user);

    @GET("api/users")
    Call<List<User>> getAllUsers(@Header("api-key") String apiKey);
}