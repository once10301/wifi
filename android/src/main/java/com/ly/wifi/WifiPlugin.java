package com.ly.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class WifiPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {
    private WifiDelegate delegate;
    private boolean isAttachedToActivity = false;

    public WifiPlugin() {
        this.delegate = null;
    }

    private WifiPlugin(WifiDelegate delegate) {
        this.delegate = delegate;
    }


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        BinaryMessenger messenger = binding.getBinaryMessenger();
        Context appContext = binding.getApplicationContext();
        if (delegate == null) {
            delegate = getDelegate(messenger, appContext);
        }

    }

    @Deprecated // keeping for backward compatibility
    public static void registerWith(Registrar registrar) {
        BinaryMessenger messenger = registrar.messenger();
        Context appContext = registrar.activeContext().getApplicationContext();
        Activity activity = registrar.activity();

        WifiDelegate.PermissionManager permissionManager = new WifiDelegate.PermissionManager() {
            @Override
            public boolean isPermissionGranted(String permissionName) {
                return ActivityCompat.checkSelfPermission(appContext, permissionName) == PackageManager.PERMISSION_GRANTED;
            }

            @Override
            public void askForPermission(String permissionName, int requestCode) {
                ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);
            }
        };

        final WifiDelegate delegate = getDelegate(messenger, appContext);


        registrar.addRequestPermissionsResultListener(delegate);
        delegate.setPermissionManager(permissionManager);
    }

    @NonNull
    private static WifiDelegate getDelegate(BinaryMessenger messenger, Context appContext) {
        final MethodChannel channel = new MethodChannel(messenger, "plugins.ly.com/wifi");
        WifiManager wifiManager = (WifiManager) appContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiDelegate delegate = new WifiDelegate(appContext, wifiManager);
        // support Android O,listen network disconnect event
        // https://stackoverflow.com/questions/50462987/android-o-wifimanager-enablenetwork-cannot-work
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        appContext.registerReceiver(delegate.networkReceiver, filter);
        channel.setMethodCallHandler(new WifiPlugin(delegate));
        return delegate;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (isAttachedToActivity) {
            result.error("no_activity", "wifi plugin requires a foreground activity.", null);
            return;
        }
        switch (call.method) {
            case "ssid":
                delegate.getSSID(call, result);
                break;
            case "level":
                delegate.getLevel(call, result);
                break;
            case "ip":
                delegate.getIP(call, result);
                break;
            case "list":
                delegate.getWifiList(call, result);
                break;
            case "connection":
                delegate.connection(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        delegate = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        isAttachedToActivity = true;
        Activity activity = binding.getActivity();
        if (!delegate.isPermissionManagerExist()) {
            WifiDelegate.PermissionManager permissionManager = new WifiDelegate.PermissionManager() {
                @Override
                public boolean isPermissionGranted(String permissionName) {
                    return ActivityCompat.checkSelfPermission(activity, permissionName) == PackageManager.PERMISSION_GRANTED;
                }

                @Override
                public void askForPermission(String permissionName, int requestCode) {
                    ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);
                }
            };
            delegate.setPermissionManager(permissionManager);
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        isAttachedToActivity = false;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        isAttachedToActivity = true;
    }

    @Override
    public void onDetachedFromActivity() {
        isAttachedToActivity = false;
    }
}
