package com.ly.wifi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

public class WifiPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {

    private WifiManager wifiManager;
    private WifiDelegate delegate;


    private WifiPlugin() {

    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        delegate = new WifiDelegate(binding.getActivity(), wifiManager);
        binding.addRequestPermissionsResultListener(delegate);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        binding.getActivity().getApplicationContext().registerReceiver(delegate.networkReceiver, filter);

    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        final MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(), "plugins.ly.com/wifi");
        wifiManager = (WifiManager) binding.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        channel.setMethodCallHandler(new WifiPlugin());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

    }

    @Deprecated
    public static void registerWith(PluginRegistry.Registrar registrar) {
        BinaryMessenger messenger = registrar.messenger();
        Context context = registrar.context();
        initPlugin(messenger, context);
    }

    private static void initPlugin(BinaryMessenger messenger, Context context) {
        final MethodChannel channel = new MethodChannel(messenger, "plugins.ly.com/wifi");
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiDelegate delegate = new WifiDelegate( wifiManager);
        registrar.addRequestPermissionsResultListener(delegate);

        // support Android O,listen network disconnect event
        // https://stackoverflow.com/questions/50462987/android-o-wifimanager-enablenetwork-cannot-work
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(delegate.networkReceiver, filter);
        channel.setMethodCallHandler(new WifiPlugin());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

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
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }
}
