package com.android.unideal.rest;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RestUtils {
  public static final String MULTIPART_FORM_DATA = "multipart/form-data";

  public static RequestBody TypedString(String value) {
    return RequestBody.create(MediaType.parse("text/plain"), value);
  }

  public static RequestBody TypedString(double value) {
    return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(value));
  }

  public static RequestBody TypedString(int value) {
    return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(value));
  }

  public static RequestBody TypedImageFile(File file) {
    return RequestBody.create(MediaType.parse("image/*"), file);
  }

  public static RequestBody TypedDocumentFile(File file) {
    return RequestBody.create(MediaType.parse("multipart/form-data"), file);
  }

  public static MultipartBody.Part prepareMultiFilePart(String partName, String filePath) {
    File file = new File(filePath);
    // create RequestBody instance from file
    RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), file);
    // MultipartBody.Part is used to send also the actual file name
    return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
  }
}
