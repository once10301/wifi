import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:wifi/wifi.dart';

void main() => runApp(new MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      title: 'Wifi',
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
  String _wifiName = 'click button to get wifi ssid.';
  List ssidList = [];
  String ssid = '', password = '';

  @override
  void initState() {
    super.initState();
    loadData();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Wifi'),
        centerTitle: true,
      ),
      body: SafeArea(
        child: ListView.builder(
          padding: EdgeInsets.all(8.0),
          itemCount: ssidList.length + 1,
          itemBuilder: (BuildContext context, int index) {
            return itemSSID(index);
          },
        ),
      ),
    );
  }

  Widget itemSSID(index) {
    if (index == 0) {
      return Column(
        children: [
          Row(
            children: <Widget>[
              RaisedButton(
                child: Text('ssid'),
                onPressed: _getWifiName,
              ),
              Text(_wifiName),
            ],
          ),
          TextField(
            decoration: InputDecoration(
              border: UnderlineInputBorder(),
              filled: true,
              icon: Icon(Icons.wifi),
              hintText: 'Your wifi ssid',
              labelText: 'ssid',
            ),
            keyboardType: TextInputType.text,
            onChanged: (value) {
              ssid = value;
            },
          ),
          TextField(
            decoration: InputDecoration(
              border: UnderlineInputBorder(),
              filled: true,
              icon: Icon(Icons.lock_outline),
              hintText: 'Your wifi password',
              labelText: 'password',
            ),
            keyboardType: TextInputType.text,
            onChanged: (value) {
              password = value;
            },
          ),
          RaisedButton(
            child: Text('connection'),
            onPressed: connection,
          ),
        ],
      );
    } else {
      return Column(children: <Widget>[
        ListTile(
          leading: Icon(Icons.wifi),
          title: Text(
            ssidList[index - 1],
            style: TextStyle(
              color: Colors.black87,
              fontSize: 16.0,
            ),
          ),
          dense: true,
        ),
        Divider(),
      ]);
    }
  }

 void loadData() {
    Wifi.list('').then((list) {
      setState(() {
        ssidList = list;
      });
    });
  }

  Future<Null> _getWifiName() async {
    String wifiName = await Wifi.ssid;
    setState(() {
      _wifiName = wifiName;
    });
  }

  Future<Null> connection() async {
    Wifi.connection(ssid, password).then((v) {
      print(v);
    });
  }
}
