package com.ldh.cordova.appinstaller;

import java.io.File;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class InstallApp extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("Install".equals(action)) {
            String path = args.getString(0);
            this.install(path, callbackContext);
            return true;
        }
        return false;
    }

    private void install(String path, CallbackContext callbackContext) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cordova.getActivity().startActivity(intent);
            callbackContext.success();
        } catch(Exception e) {
            callbackContext.error(e.toString());
        }
    }
}
