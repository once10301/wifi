import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:wifi/wifi.dart';

void main() => runApp(new MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'WiFi Info',
      theme: new ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: new MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {

  @override
  _MyHomePageState createState() => new _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  String _wifiName = 'Unknown wifi name.';

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            RaisedButton(
              child: Text('Get Wifi Info'),
              onPressed: _getWifiName,
            ),
            Text(_wifiName),
          ],
        ),
      ),
    );
  }

  Future<Null> _getWifiName() async {
    String wifiName;
    try {
      var result = await Wifi.ssid;
      wifiName = result;
    } on PlatformException catch (e) {
      wifiName = "Failed to get wifi name: '${e.message}'.";
    }

    setState(() {
      _wifiName = wifiName;
    });
  }
}