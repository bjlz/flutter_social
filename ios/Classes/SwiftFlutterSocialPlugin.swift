import Flutter
import UIKit

var isWeChatRegistered = false

public class SwiftFlutterSocialPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "cn.fb/flutter_social", binaryMessenger: registrar.messenger())
        let instance = SwiftFlutterSocialPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
        
        FlutterResponseHandler.default.methodChannel = channel
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        FlutterMethodCallHandler.handle(call, result: result)
    }
}
