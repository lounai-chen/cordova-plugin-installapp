package com.cty.cordova.appinstaller;

import org.apache.cordova.BuildHelper;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginManager;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

public class InstallApp extends CordovaPlugin {
    private static final String LOG_TAG = "InstallApp";
    private CallbackContext _callbackContext = null;
    private String _path = "";
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this._callbackContext = callbackContext;
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
          this._path = path;
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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    //boolean hasInstallPermission = this.cordova.getActivity().getPackageManager().canRequestPackageInstalls();
                    boolean hasInstallPermission = ContextCompat.checkSelfPermission(cordova.getActivity(), Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED;
                    if(!hasInstallPermission){
                        LOG.d(LOG_TAG, "Need to require INSTALL_PACKAGES permission");
                       //ToastUtil.makeText(MyApplication.getContext(), MyApplication.getContext().getString(R.string.string_install_unknow_apk_note), false);  
                         if (ActivityCompat.shouldShowRequestPermissionRationale(cordova.getActivity(),
                            Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
                                 //User even denied permission and checked "don't remind again"

                                 LOG.d(LOG_TAG, "Permission Denied");
                                 startInstallPermissionSettingActivity();  
                                 // Show an expanation to the user *asynchronously* -- don't block
                                 // this thread waiting for the user's response! After the user
                                 // sees the explanation, try again to request the permission.
                                 callbackContext.error("permission denied");
                                 return;
                          } else {
                                 // No explanation needed, we can request the permission.
                                 PermissionHelper.requestPermission(this, 1218, Manifest.permission.REQUEST_INSTALL_PACKAGES);
                                 return;
                          }
                     //  return; 
                    }
                }
            }
            cordova.getActivity().startActivity(intent);
            callbackContext.success(path);
        } catch(Exception e) {
            LOG.d("InstallAPP",e.toString());
            callbackContext.error(e.toString());
        }
    }
   
      @Override
  public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults)  throws JSONException {
    switch (requestCode) {
      case 1218: {
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
              _callbackContext.error("permission denied");
              LOG.d(LOG_TAG, "permission denied");
        } else {
            install(_path, _callbackContext);
        }
        return;
      }

     }
   }
    
    /** 
    * 跳转到设置-允许安装未知来源-页面 
    */  
   //@RequiresApi(api = Build.VERSION_CODES.O)  //min android sdk 23以上才支持
   private void startInstallPermissionSettingActivity() {  
       //注意这个是8.0新API  
       Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);  
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
       cordova.getActivity().startActivity(intent);  
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
