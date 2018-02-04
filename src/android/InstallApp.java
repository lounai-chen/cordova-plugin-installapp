package com.cty.cordova.appinstaller;

import org.apache.cordova.BuildHelper;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

public class InstallApp extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      String applicationId = (String) BuildHelper.getBuildConfigValue(cordova.getActivity(), "APPLICATION_ID");
      applicationId = preferences.getString("applicationId", applicationId);
        if ("Install".equals(action)) {
            String path = args.getString(0);
            
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && path.startsWith("file://"))
          {
              path = path.substring(7);
              Uri url = FileProvider.getUriForFile(cordova.getActivity(),
                applicationId + ".provider",
                new File(path));
              path = url.toString();
             LOG.d("InstallAPP", path);
          }

          this.install(path, callbackContext);
          return true;
        }
        return false;
    }

    private void install(String path, CallbackContext callbackContext) {
        try {

            Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(path), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
               intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
               intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            cordova.getActivity().startActivity(intent);
            callbackContext.success(path);
        } catch(Exception e) {
            LOG.d("InstallAPP",e.toString());
            callbackContext.error(e.toString());
        }
    }

   // private void version(String path, CallbackContext callbackContext) {
      //  try {
          // PackageManager packageManager = this.cordova.getActivity().getPackageManager();
         //  callbackContext.success(packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionCode);
     //   } catch(Exception e) {
     //       callbackContext.error(e.toString());
     //   }
   // }
}
