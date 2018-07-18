package com.ly.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class WifiPlugin implements MethodCallHandler {
    private final Registrar registrar;
    private WifiDelegate delegate;

    private WifiPlugin(Registrar registrar, WifiDelegate delegate) {
        this.registrar = registrar;
        this.delegate = delegate;
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "plugins.ly.com/wifi");
        WifiManager wifiManager = (WifiManager) registrar.activity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final WifiDelegate delegate = new WifiDelegate(registrar.activity(), wifiManager);
        registrar.addRequestPermissionsResultListener(delegate);
        channel.setMethodCallHandler(new WifiPlugin(registrar, delegate));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (registrar.activity() == null) {
            result.error("no_activity", "wifi plugin requires a foreground activity.", null);
            return;
        }

        switch (call.method) {
            case "ssid":
                delegate.getSSID(call, result);
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

}
