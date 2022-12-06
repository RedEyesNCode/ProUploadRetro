package com.redyesncode.retrofitfileupload;

import com.google.gson.JsonElement;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    // EXAMPLE
    @Multipart
    @POST("upload-profile-pic")
    Call<JsonElement> callUpdateProfilePicture(@Header("Authorization") String accessToken, @Part MultipartBody.Part imgeProfile);

}
