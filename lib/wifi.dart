import 'dart:async';

import 'package:flutter/services.dart';

enum WifiState { error, success, already }

class Wifi {
  static const MethodChannel _channel = const MethodChannel('plugins.ly.com/wifi');

  static Future<String> get ssid async {
    return await _channel.invokeMethod('ssid');
  }

  static Future<int> get level async {
    return await _channel.invokeMethod('level');
  }

  static Future<String> get ip async {
    return await _channel.invokeMethod('ip');
  }

  static Future<bool> get wifiEnabled async {
    return await _channel.invokeMethod('wifiEnabled');
  }

  static Future<List<WifiResult>> list(String key) async {
    final Map<String, dynamic> params = {
      'key': key,
    };
    var results = await _channel.invokeMethod('list', params);
    List<WifiResult> resultList = [];
    for (int i = 0; i < results.length; i++) {
      resultList.add(WifiResult(results[i]['ssid'], results[i]['level'], results[i]['bssid']));
    }
    return resultList;
  }

  static Future<WifiState> connection(String ssid, [String password]) async {
    final Map<String, dynamic> params = {
      'ssid': ssid
    };
    if (password != null && password.isNotEmpty) {
      params.addEntries([MapEntry('password', password)]);
    }
    int state = await _channel.invokeMethod('connection', params);
    switch (state) {
      case 0:
        return WifiState.error;
      case 1:
        return WifiState.success;
      case 2:
        return WifiState.already;
      default:
        return WifiState.error;
    }
  }
}

class WifiResult {
  String ssid;
  int level;
  String bssid;

  WifiResult(this.ssid, this.level, this.bssid);
}
