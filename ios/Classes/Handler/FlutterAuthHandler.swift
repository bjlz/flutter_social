//
//  FlutterAuthHandler.swift
//  flutter_social
//
//  Created by Guimin Chu on 2020/3/26.
//

import Foundation

class FlutterAuthHandler {
    static func handleAuth(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if !isWeChatRegistered {
            result(FlutterError(code: Constants.CallResults.resultErrorNeedWeChat, message: Constants.CallResults.resultMessageNeedWeChat, details: nil))
            return
        }
        
        switch call.method {
        case Constants.Methods.sendAuth:
            auth(call, result: result)
        default:
            break
        }
    }
    
    private static func auth(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        MonkeyKing.weChatOAuthForCode { (code, error) in
            guard let code = code else {
                result("")
                return
            }
            
            result(code)
        }
    }
    
}
