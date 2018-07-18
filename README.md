# wifi

This plugin allows Flutter apps to get wifi ssid and list, connect wifi with ssid and password.

This plugin works Android.

iOS later released.



Sample usage to check current status:

```dart
import 'package:wifi/wifi.dart';

String ssid = await Wifi.ssid;

List<String> ssidList = await Wifi.list('key'); // this key is used to filter

var result = await Wifi.connection('ssid', 'password');
```
## Getting Started

For help getting started with Flutter, view our online
[documentation](https://flutter.io/).

For help on editing plugin code, view the [documentation](https://flutter.io/developing-packages/#edit-plugin-package).
