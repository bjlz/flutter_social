//
//  FlutterMethodCallHandler.swift
//  flutter_social
//
//  Created by Guimin Chu on 2019/5/6.
//

import Foundation

public struct FlutterMethodCallHandler {
    public static func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let callMethod = call.method
        //    result("iOS " + UIDevice.current.systemVersion)
        print("callMethod: " + callMethod)
        if "registerApp" == callMethod {
            FlutterWXApiHandler.registerApp(call, result: result)
        } else if "order" == callMethod {
            FlutterOrderHandler.handleOrder(call, result: result)
        } else if callMethod == Constants.Methods.launchMiniProgram {
            FlutterWXApiHandler.launchMiniProgram(call, result: result)
        } else if callMethod.hasPrefix("share") {
            FlutterShareHandler.handleShare(call, result: result)
        }
    }
}
