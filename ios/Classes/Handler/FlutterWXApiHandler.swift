//
//  FlutterWXApiHandler.swift
//  flutter_social
//
//  Created by Guimin Chu on 2019/5/6.
//

import Foundation

struct FlutterWXApiHandler {
    static func registerApp(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if isWeChatRegistered {
            result([Constants.Key.platform: Constants.Key.iOS, Constants.Key.result: true])
            return
        }
        
        let flutterError = FlutterError(code: "invalid app id", message: "are you sure your app id is correct ? ", details: nil)
        
        guard let arguments = call.arguments as? [String: Any] else {
            result(flutterError)
            return
        }
        
        guard let appId = arguments[Constants.Key.appId] as? String, !appId.isEmpty else {
            result(flutterError)
            return
        }
        
        let appKey = arguments[Constants.Key.appKey] as? String
        let miniAppId = arguments[Constants.Key.miniProgramId] as? String
        
        let account = MonkeyKing.Account.weChat(appID: appId, appKey: appKey, miniAppID: miniAppId)
        MonkeyKing.registerAccount(account)
        
        isWeChatRegistered = true
        
        result([Constants.Key.platform: Constants.Key.iOS, Constants.Key.result: isWeChatRegistered])
    }
    
    static func checkWeChatInstallation(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if !isWeChatRegistered {
            result(FlutterError(code: Constants.CallResults.resultErrorNeedWeChat, message: Constants.CallResults.resultMessageNeedWeChat, details: nil))
            return
        } else {
            result(MonkeyKing.SupportedPlatform.weChat)
        }
    }
    
    static func launchMiniProgram(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let arguments = call.arguments as? [String: String] else {
            return
        }
        
        let userName = arguments["userName"]!
        let path = arguments["path"]
        let type = (arguments["miniProgramType"] ?? "").lowercased()
        
        let miniAppType = MonkeyKing.MiniAppType.init(rawValue: type) ?? MonkeyKing.MiniAppType.release
        
        MonkeyKing.launch(.weChat(.miniApp(username: userName, path: path, type: miniAppType))) { (result) in
            print("result: \(result)")
        }
    }
}
