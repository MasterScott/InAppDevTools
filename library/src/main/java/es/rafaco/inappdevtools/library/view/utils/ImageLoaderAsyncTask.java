package es.rafaco.inappdevtools.library.view.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

public class ImageLoaderAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private final ImageView imageView;

    public ImageLoaderAsyncTask(ImageView view) {
        super();
        imageView = view;
    }

    @Override
    protected Bitmap doInBackground(String... paths) {
        File imgFile = new  File(paths[0]);
        if(imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Point screenDimensions = UiUtils.getDisplaySize(imageView.getContext());
            int sizeX = (screenDimensions.x /2) - 54;

            return getProportionalBitmap(bitmap, sizeX, "X");
        }
        return null;
    }

    public Bitmap getProportionalBitmap(Bitmap bitmap,
                                        int newDimensionXorY,
                                        String xory) {
        if (bitmap == null)
            return null;

        float xyRatio;
        int newWidth;
        int newHeight;

        if (xory.equalsIgnoreCase("x")) {
            xyRatio = (float) newDimensionXorY / bitmap.getWidth();
            newHeight = (int) (bitmap.getHeight() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newDimensionXorY, newHeight, true);
        }
        else if (xory.equalsIgnoreCase("y")) {
            xyRatio = (float) newDimensionXorY / bitmap.getHeight();
            newWidth = (int) (bitmap.getWidth() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newWidth, newDimensionXorY, true);
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result){
        if (result != null){
            imageView.setImageBitmap(result);
        }
    }
}

