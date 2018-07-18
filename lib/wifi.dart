import 'dart:async';

import 'package:flutter/services.dart';

class Wifi {
  static const MethodChannel _channel = const MethodChannel('plugins.ly.com/wifi');

  static Future<String> get ssid async {
    return await _channel.invokeMethod('ssid');
  }

  static Future<T> list<T>(String key) async {
    final Map<String, dynamic> params = {
      'key': key,
    };
    return await _channel.invokeMethod('list', params);
  }

  static Future<String> connection(String ssid, String password) async {
    final Map<String, dynamic> params = {
      'ssid': ssid,
      'password': password,
    };
    return await _channel.invokeMethod('connection', params);
  }
}
