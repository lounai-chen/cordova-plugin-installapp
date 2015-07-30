var Install = function () {};

Install.prototype.InstallApp = function (src,onSuccess, onFail) {
    return cordova.exec(onSuccess, onFail,"InstallApp","Install",[src]);
};

Install.prototype.OpenApp = function (src,onSuccess, onFail) {
    return cordova.exec(onSuccess, onFail,"InstallApp","OpenApp",[src]);
};
module.exports = (new Install());