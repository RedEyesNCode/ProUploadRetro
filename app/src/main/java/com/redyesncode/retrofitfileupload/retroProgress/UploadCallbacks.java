package com.redyesncode.retrofitfileupload.retroProgress;

public interface UploadCallbacks {
    void onProgressUpdate(int percentage);
    void onError(String error);
    void onFinish();
}

