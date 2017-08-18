package com.wag.project.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.FileOutputStream;

/**
 * Image Utils Helper class
 */
public class ImageUtils {

    /**
     * Save image on device (internal storage)
     * @param context The context
     * @param bitmap The bitmap
     * @param imageName The name of the image
     */
    public static void saveImage(Context context, Bitmap bitmap, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
            Log.d("saveImage", "Image saved.");

        } catch (Exception error) {
            Log.d("saveImage", "Exception, image not saved!" + error.getMessage());

        }
    }
}
