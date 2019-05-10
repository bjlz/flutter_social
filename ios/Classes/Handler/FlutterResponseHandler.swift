//
//  FlutterResponseHandler.swift
//  Alamofire
//
//  Created by Guimin Chu on 2019/5/10.
//

struct FlutterResponseHandler {
    static var `default` = FlutterResponseHandler()
    
    var methodChannel: FlutterMethodChannel!
    
    func handlePayResponse(done: Bool) {
        //  0 成功     展示成功页面
        // -2 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP。
        let dict = ["errCode": done ? 0 : -2]
        
        methodChannel.invokeMethod("onPayResponse", arguments: dict)
    }
}
