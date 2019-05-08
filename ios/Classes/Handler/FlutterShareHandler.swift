//
//  FlutterShareHandler.swift
//  flutter_social
//
//  Created by Guimin Chu on 2019/5/7.
//

class FlutterShareHandler {
    static func handleShare(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if !isWeChatRegistered {
            result(FlutterError(code: Constants.CallResults.resultErrorNeedWeChat, message: Constants.CallResults.resultMessageNeedWeChat, details: nil))
            return
        }
        
        switch call.method {
        case Constants.Methods.shareText:
            shareText(call, result: result)
        case Constants.Methods.shareImage:
            shareImage(call, result: result)
        case Constants.Methods.shareWebPage:
            shareURL(call, result: result)
        case Constants.Methods.shareMiniProgram:
            shareMiniApp(call, result: result)
        default:
            break
        }
    }
    
    private static func shareText(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let arguments = call.arguments as? [String: String] else {
            return
        }
        
        let text = arguments[Constants.Key.text] ?? ""
        let scene = arguments[Constants.Key.scene] ?? "session"
        
        let info = MonkeyKing.Info(
            title: text,
            description: nil,
            thumbnail: nil,
            media: nil
        )
        
        shareInfo(info, scene: scene)
    }
    
    private static func shareImage(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let arguments = call.arguments as? [String: Any],
            let flutterData = arguments[Constants.Key.data] as? FlutterStandardTypedData,
            let scene = arguments[Constants.Key.scene] as? String else {
                return
        }

        let image = UIImage(data: flutterData.data)
        
        let info = MonkeyKing.Info(
            title: nil,
            description: nil,
            thumbnail: image,
            media: .image(image!)
        )
        
        shareInfo(info, scene: scene)
    }
    
    private static func shareURL(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let arguments = call.arguments as? [String: Any],
            let urlString = arguments[Constants.Key.url] as? String,
            let url = URL(string: urlString),
            let scene = arguments[Constants.Key.scene] as? String else {
            return
        }
        
        let title = arguments[Constants.Key.title] as? String
        let description = arguments[Constants.Key.description] as? String
//        let image = UIImage(data: flutterData.data)
        
        let info = MonkeyKing.Info(
            title: title,
            description: description,
            thumbnail: nil,
            media: .url(url)
        )
        
        shareInfo(info, scene: scene)
    }
    
    private static func shareMiniApp(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let arguments = call.arguments as? [String: Any],
            let miniProgramType = arguments[Constants.Key.miniProgramType] as? String else {
            return
        }
        
        let title = arguments[Constants.Key.title] as? String
        let description = arguments[Constants.Key.description] as? String
//                let image = UIImage(data: flutterData.data)
        let path = arguments[Constants.Key.path] as? String
        
        let urlString = arguments["webPageUrl"] as? String
//        let data = arguments[Constants.Key.description] as? String
        let flutterData = arguments[Constants.Key.thumbnailData] as? FlutterStandardTypedData
        
        var thumbnail: UIImage?
        if let data = flutterData?.data, let image = UIImage(data: data) {
            thumbnail = image
        }
        
//        let miniProgramType = arguments[Constants.Key.miniProgramType] as? String
//        let thumbnail = UIImage(data: flutterData?.data)
        
//        let info = MonkeyKing.Info(
//            title: title,
//            description: description,
//            thumbnail: nil,
//            media: .url(url)
//        )

//        let type = (miniProgramType ?? "").lowercased()
        
        let miniAppType = MonkeyKing.MiniAppType.init(rawValue: miniProgramType) ?? MonkeyKing.MiniAppType.release
        
        let info = MonkeyKing.Info(
            title: title,
            description: description,
            thumbnail: thumbnail,
            media: .miniApp(url: URL(string: urlString!)!, path: path ?? "", withShareTicket: true, type: miniAppType)
        )
        
//        let info1 = MonkeyKing.Info(
//            title: "aaa",
//            description: "",
//            thumbnail: nil,
//            media: .miniApp(url: URL(string: "www.baidu.com")!, path: "", withShareTicket: false, type: miniAppType)
//        )
        
        let info2 = MonkeyKing.Info(
            title: "Mini App, \(UUID().uuidString)",
            description: nil,
            thumbnail: nil,
            media: .miniApp(url: URL(string: "https://www.baidu.com/")!, path: "", withShareTicket: true, type: .release)
        )
        
        let message = MonkeyKing.Message.weChat(.session(info: info2))
        

            MonkeyKing.deliver(message) { result in
                print("result: \(result)")
            }
        
        
//        shareInfo(info2, scene: "session")
        
//        MonkeyKing.deliver(message) { result in
//            print("result: \(result)")
//        }
//
//
//
////        MonkeyKing.de
    }
    
    private static func shareInfo(_ info: MonkeyKing.Info, scene: String) {
        var message: MonkeyKing.Message?
        switch scene {
        case "session":
            message = MonkeyKing.Message.weChat(.session(info: info))
        case "timeline":
            message = MonkeyKing.Message.weChat(.timeline(info: info))
        case "favorite":
            message = MonkeyKing.Message.weChat(.favorite(info: info))
        default:
            break
        }
        
        if let message = message {
            MonkeyKing.deliver(message) { result in
                print("result: \(result)")
            }
        }
    }
}
