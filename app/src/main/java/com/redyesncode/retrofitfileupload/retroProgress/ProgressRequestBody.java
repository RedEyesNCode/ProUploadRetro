package com.redyesncode.retrofitfileupload.retroProgress;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;
    private String content_type;

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public File getmFile() {
        return mFile;
    }

    public void setmFile(File mFile) {
        this.mFile = mFile;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public UploadCallbacks getmListener() {
        return mListener;
    }

    public void setmListener(UploadCallbacks mListener) {
        this.mListener = mListener;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public ProgressRequestBody(final File file, String content_type, final  UploadCallbacks listener) {
        this.content_type = content_type;
        mFile = file;
        mListener = listener;
    }
    @Override
    public MediaType contentType() {
        return MediaType.parse(content_type);
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }


    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;


        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                uploaded += read;

                sink.write(buffer, 0, read);
                sink.flush();


                // update progress on UI thread




                // Below line Causes crash
                // Animations should be on the main thread only.

//                mListener.onProgressUpdate((int)(100 * uploaded / fileLength));

                handler.post(new ProgressUpdater(uploaded, fileLength));




            }
        } finally {
            in.close();
        }
    }
    public class ProgressUpdater implements Runnable{

        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            try{
                mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));

            }catch (Exception e){
                e.printStackTrace();
                mListener.onError(e.getMessage());

            }
        }
    }
}
