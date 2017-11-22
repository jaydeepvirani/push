package com.android.unideal.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by ADMIN on 14-10-2016.
 */

public class ImageCompressTask extends AsyncTask<Void, Void, String> {
  private int desireWidth, desireHeight;
  private String srcImagePath;

  public ImageCompressTask(String srcImagePath) {
    this.srcImagePath = srcImagePath;
    this.desireWidth = -1;
    this.desireHeight = -1;
  }

  public ImageCompressTask(int desireWidth, int desireHeight, String srcImagePath) {
    this.desireWidth = desireWidth;
    this.desireHeight = desireHeight;
    this.srcImagePath = srcImagePath;
  }

  @Override
  protected String doInBackground(Void... params) {
    return decodeFile();
  }

  private String decodeFile() {
    String strMyImagePath = null;
    Bitmap scaledBitmap = null;

    try {
      // Part 1: Decode image
      Bitmap unscaledBitmap = ScalingUtilities.decodeFile(srcImagePath, desireWidth, desireHeight,
          ScalingUtilities.ScalingLogic.FIT);

      // Part 2: get desire dimensions
      if (desireWidth == -1 || desireHeight == -1) {
        //get its original dimensions
        int bmOriginalWidth = unscaledBitmap.getWidth();
        int bmOriginalHeight = unscaledBitmap.getHeight();
        double originalWidthToHeightRatio = 1.0 * bmOriginalWidth / bmOriginalHeight;
        double originalHeightToWidthRatio = 1.0 * bmOriginalHeight / bmOriginalWidth;
        //choose a maximum height
        int maxHeight = 1024;
        //choose a max width
        int maxWidth = 1024;
        if (bmOriginalWidth > maxWidth || bmOriginalHeight > maxHeight) {
          if (bmOriginalWidth > bmOriginalHeight) {
            //scale the width
            desireWidth = (int) Math.max(maxWidth, bmOriginalWidth * .75);
            desireHeight = (int) (desireWidth * originalHeightToWidthRatio);
          } else {
            desireWidth = (int) Math.max(maxHeight, bmOriginalHeight * .55);
            desireHeight = (int) (desireWidth * originalWidthToHeightRatio);
          }
        } else {
          return srcImagePath;
        }
      }

      if (!(unscaledBitmap.getWidth() <= desireWidth
          && unscaledBitmap.getHeight() <= desireHeight)) {
        // Part 3: Scale image
        scaledBitmap =
            ScalingUtilities.createScaledBitmap(unscaledBitmap, desireWidth, desireHeight,
                ScalingUtilities.ScalingLogic.FIT);
      } else {
        unscaledBitmap.recycle();
        return srcImagePath;
      }

      // Part 3: Store to tmp file
      File mFolder = new File(Consts.getImageDir());
      String s = "tmp_" + System.currentTimeMillis() + ".png";
      File f = new File(mFolder.getAbsolutePath(), s);
      strMyImagePath = f.getAbsolutePath();
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(f);
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        fos.flush();
        fos.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }

      scaledBitmap.recycle();
    } catch (Throwable e) {
      e.printStackTrace();
    }

    if (strMyImagePath == null) {
      return srcImagePath;
    }
    return strMyImagePath;
  }
}
