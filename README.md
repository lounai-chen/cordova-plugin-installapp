# cordova-plugin-installapp

This plugin allows you to install Android App.

> To get a few ideas, check out the [sample](#sample) at the bottom of this page.

## Installation

```bash
cordova plugin add https://github.com/wlnjtan/cordova-plugin-installapp.git
```

## Supported Platforms

- Android (Tested on Android L, M, N, O)

### Example <a name="sample"></a>

```js
// !! Assumes variable fileURL contains a valid URL to a APK file on the device,
//    for example, 
// file:///localhost/persistent/path/to/file.apk  (any Android version)
// content://localhost/persistent/path/to/file.apk (>= Android N)

window['cordova'].InstallApp.InstallApp(apkfile, ret => {
            console.log('Success');
        },
            error => {
                console.log(error);
            }); 
```
