import 'dart:async';

import 'package:flutter/services.dart';

class Wifi {
  static const MethodChannel _channel = const MethodChannel('plugins.ly.com/wifi');

  static Future<String> get ssid async {
    final String ssid = await _channel.invokeMethod('getWifi');
    return ssid;
  }
}
