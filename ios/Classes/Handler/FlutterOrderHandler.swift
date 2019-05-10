//
//  FlutterOrderHandler.swift
//  flutter_social
//
//  Created by Guimin Chu on 2019/5/6.
//

import Foundation

struct FlutterOrderHandler {
    static func handleOrder(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let arguments = call.arguments as? [String: String] else {
            return
        }
        
        let appId = arguments["appId"]!
        let timestamp = arguments["timeStamp"]!
        let partnerId = arguments["partnerId"]!
        let prepayId = arguments["prepayId"]!
        let packageValue = arguments["packageValue"]!
        let nonceStr = arguments["nonceStr"]!
        let sign = arguments["sign"]!
        
        let orderString = "weixin://app/\(appId)/pay/?nonceStr=\(nonceStr)&package=\(packageValue)&partnerId=\(partnerId)&prepayId=\(prepayId)&timeStamp=\(timestamp)&sign=\(sign)&signType=SHA1"

        let urlString = orderString.replacingOccurrences(of: "Sign=WXPay", with: "Sign%3DWXPay")
        
        MonkeyKing.deliver(MonkeyKing.Order.weChat(urlString: urlString)) { (done) in
            result([Constants.Key.platform: Constants.Key.iOS, Constants.Key.result: done])
            
            FlutterResponseHandler.default.handlePayResponse(done: done)
            if (done) {
                print("支付成功")
            } else {
                print("取消支付")
            }
        }
    }
}
