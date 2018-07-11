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

  private WifiPlugin(Registrar registrar) {
    this.registrar = registrar;
  }

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "plugins.ly.com/wifi");
    channel.setMethodCallHandler(new WifiPlugin(registrar));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (registrar.activity() == null) {
      result.error("no_activity", "wifi plugin requires a foreground activity.", null);
      return;
    }
    if (call.method.equals("getWifi")) {
      String wifiName = getWiFiName(registrar.activity());
      if (!wifiName.isEmpty()) {
        result.success(wifiName);
      } else {
        result.error("unavailable", "wifi name not available.", null);
      }
    } else {
      result.notImplemented();
    }
  }

  private String getWiFiName(Context context) {
    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    if (wifiManager != null) {
      return wifiManager.getConnectionInfo().getSSID().replace("\"", "");
    }
    return "";
  }
}
