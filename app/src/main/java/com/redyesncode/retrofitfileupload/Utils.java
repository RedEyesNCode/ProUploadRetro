package com.redyesncode.retrofitfileupload;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static File getFile(Context context, Uri uri) throws IOException {
        try {

            File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + queryName(context, uri));
            try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
                createFileFromStream(ins, destinationFilename);
            } catch (Exception ex) {
                Log.e("Save File", ex.getMessage());
                ex.printStackTrace();
            }
            return destinationFilename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static String queryName(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String name = null;
            if (uri.getScheme().equals("content")) {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                assert cursor != null;
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                name = cursor.getString(nameIndex);
            }
            if (name == null) {
                name = uri.getPath();
                int cut = name.lastIndexOf('/');
                if (cut != -1) {
                    name = name.substring(cut + 1);
                }
            }

            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }
}
