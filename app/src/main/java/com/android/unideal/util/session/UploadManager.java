package com.android.unideal.util.session;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bhavdip on 17/11/16.
 */

public class UploadManager {

  private static final String PREF_NAME = "Upload_Files.xml";

  private static UploadManager SINGLETON;
  private Context _context;
  // Shared pref mode
  private int PRIVATE_MODE = 0;
  private SharedPreferences pref;
  private SharedPreferences.Editor editor;

  private UploadManager(Context context) {
    this._context = context;
    pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  public static UploadManager get(Context context) {
    if (SINGLETON == null) {
      SINGLETON = new UploadManager(context);
    }
    return SINGLETON;
  }

  public void addUploadFile(String jobId, Set<String> fileList) {
    editor.putStringSet(jobId, fileList);
    editor.commit();
  }
  /**
   *
   * @param jobId
   */
  public void removeJobFiles(String jobId) {
    editor.remove(jobId);
    editor.commit();
  }

  public ArrayList<String> getUploadFileList(String jobId) {
    return new ArrayList<>(pref.getStringSet(jobId, new HashSet<String>()));
  }

  public ArrayList<String> pendingUploadList() {
    return new ArrayList<>(pref.getAll().keySet());
  }
}
