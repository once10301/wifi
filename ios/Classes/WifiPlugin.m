#import "WifiPlugin.h"
#import <SystemConfiguration/CaptiveNetwork.h>

@implementation WifiPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"plugins.ly.com/wifi"
            binaryMessenger:[registrar messenger]];
  WifiPlugin* instance = [[WifiPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"ssid" isEqualToString:call.method]) {
    NSString *wifiName = [self getSSID];
    if ([wifiName isEqualToString: @"Not Found"]) {
      result([FlutterError errorWithCode:@"UNAVAILABLE"
                                 message:@"wifi name unavailable"
                                 details:nil]);
    } else {
      result(wifiName);
    }
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (NSString *) getSSID {
    NSString *ssid = @"Not Found";
    CFArrayRef myArray = CNCopySupportedInterfaces();
    if (myArray != nil) {
        CFDictionaryRef myDict = CNCopyCurrentNetworkInfo(CFArrayGetValueAtIndex(myArray, 0));
       if (myDict != nil) {
            NSDictionary *dict = (NSDictionary*)CFBridgingRelease(myDict);
            ssid = [dict valueForKey:@"SSID"];
        }
    }
    return ssid;
}

@end
