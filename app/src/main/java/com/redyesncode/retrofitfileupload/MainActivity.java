package com.redyesncode.retrofitfileupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.redyesncode.retrofitfileupload.databinding.ActivityMainBinding;
import com.redyesncode.retrofitfileupload.retroProgress.ProgressRequestBody;
import com.redyesncode.retrofitfileupload.retroProgress.UploadCallbacks;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements UploadCallbacks {

    private ActivityMainBinding binding;
    private String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Miwicm9sZUlkIjoyLCJmaXJzdE5hbWUiOiJ0ZXN0dCIsImxhc3ROYW1lIjoidXNlciIsImVtYWlsIjoiYXNodXRvc2hzbXRncm91cEBnbWFpbC5jb20iLCJjb3VudHJ5Q29kZSI6IklOIiwicGhvbmVDb2RlIjoiKzkxIiwibW9iaWxlTnVtYmVyIjoiODc2NTQzMjEzNCIsIm1vYmlsZVZlcmlmaWVkQXQiOiIyMDIyLTA5LTA1VDExOjQ3OjA3LjAwMFoiLCJ6aXBjb2RlIjoiOTAwMDEiLCJ1c2VybmFtZSI6InRlc3RzY3JpcHQyOG5vdmVtYmVyMTY2OTYzODgxNzUzNiIsImNpdHkiOiJMb3MgQW5nZWxlcyBDb3VudHkiLCJnZW5kZXIiOm51bGwsInN0YXRlIjoiQ2FsaWZvcm5pYSIsInN0YXRlQ29kZSI6bnVsbCwiY3JlYXRlZEF0IjoiMjAyMi0wOS0wNVQxMTo0NzowOC4wMDBaIiwiZW1haWxWZXJpZmVkQXQiOiIyMDIyLTEwLTI5VDEyOjAxOjQ1LjAwMFoiLCJzdGF0dXMiOjEsImlzU3RvcmUiOnRydWUsInJldHJpdmVJZCI6IjQyYzUxMjVmLTYzZjktNDA5Mi05YWVhLWFhYjliMDJhNzNlZSIsImF2YXRhciI6Imh0dHBzOi8vbGl2YmF5bGFzaC5zMy51cy13ZXN0LTEuYW1hem9uYXdzLmNvbS9hdmF0YXIvMjE2NzAyNjQ1OTUxNTMucHNkIiwiZGV2aWNlVHlwZSI6IklPUyIsImlhdCI6MTY3MDI2NTE0MSwiZXhwIjoxNzAxODAxMTQxfQ.zJHHDSKxsWApQQU7u62taCJbmFZB6DPzj71yoA-1WhU";
    private static final String livbay = "https://dev.api.supersourcing.net/customer/";
    private static final int PICKFILE_REQUEST_CODE =77;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        initClicks();
        binding.progressBar.setMax(100);






    }

    private void initClicks() {
        binding.ivUpload.setOnClickListener(v->{
            binding.progressBar.setProgress(0);
            binding.tvSpeed.setText("at 0 Kbps.");
            binding.tvPercentage.setText("0%");
        });
        binding.btnFileUpload.setOnClickListener(v->{
            pickFilesFromAndroid();
        });
        binding.btnUploadImage.setOnClickListener(view -> {
            checkPermissions();
        });

    }

    private void pickFilesFromAndroid() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);


    }

    private void checkPermissions(){
        // Checking the permission for picking the image from camera and gallery.

        // Checking if the permission is already granted or not.
        Dexter.withContext(MainActivity.this).withPermissions(new String[]{Manifest.permission.CAMERA}).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if(multiplePermissionsReport.areAllPermissionsGranted()){
                    pickProductImage();
                }else{
                    showToast("Permission not granted.");
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);

            }
        }).check();





    }

    private void pickProductImage() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK

            if(requestCode == ImagePicker.REQUEST_CODE){
                Uri finalUri = data.getData();
                try {
                    binding.selectedImage.setImageURI(finalUri);

                    // Confirm with your backend guy for the CONTENT_TYPE Before Proceed.

                    uploadImageRetrofit(Utils.getFile(MainActivity.this,finalUri),"image/jpeg");
                    showToast("Uploading.....");


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else if(requestCode==PICKFILE_REQUEST_CODE){
                showToast("PICKED FILE");
                Uri finalUri = data.getData();
                try {

                    uploadImageRetrofit(Utils.getFile(MainActivity.this,finalUri),"image/jpeg");
                    showToast("Uploading...FILE..");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            showToast(ImagePicker.getError(data));
        } else {
            showToast("Task cancelled.");
        }


    }

    private void getNetworkSpeed(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
        int upSpeed = nc.getLinkUpstreamBandwidthKbps();

        binding.tvSpeed.setText("at "+upSpeed);

    }

    public void showToast(String message){

        Snackbar snackbar = Snackbar.make(binding.getRoot(),message,Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    private void uploadImageRetrofit(File image,String contentType){

        final Gson gson = new GsonBuilder().setLenient().create();
        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS).build();
        ApiInterface retrofit = new Retrofit.Builder().client(httpClient)
                .baseUrl(livbay)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(ApiInterface.class);


        // Creating the Body type now.

        // IMAGE PROGRESS
        ProgressRequestBody profilePicRequestBody =  new ProgressRequestBody(image,contentType,this);

        // DEFAULT Request Body
//        RequestBody profilePicRequestBody = RequestBody.create(MediaType.parse("image/jpeg"),image);


        MultipartBody.Part multiPartProfilePic = MultipartBody.Part.createFormData("avatar",image.getName(),profilePicRequestBody);
        Call<JsonElement> call = retrofit.callUpdateProfilePicture(ACCESS_TOKEN,multiPartProfilePic);




        // Observing the response here.

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.code()==200){
                    showToast("Done. Uploading");
                }else if(response.code()==401){
                    showToast("CHANGE_TOKEN");
                }else{

                    Log.i("DEV_ASHUTOSH",new Gson().toJson(response.errorBody()));


                    /*[text={"status":"fail","message":"file must be an image."}]*/

                    showToast("FAILED.--->"+response.code());
                }


            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                showToast(t.getMessage());

            }
        });




    }

    @Override
    public void onProgressUpdate(int percentage) {

        binding.progressBar.setProgress(percentage);


        binding.tvPercentage.setText(String.valueOf(percentage)+"%");

    }

    @Override
    public void onError(String error) {
        showToast(error);

    }

    @Override
    public void onFinish() {
        binding.progressBar.setProgress(100);
        binding.tvPercentage.setText("100"+"%");
    }
}