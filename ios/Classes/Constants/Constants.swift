//
//  Constants.swift
//  flutter_social
//
//  Created by Guimin Chu on 2019/5/6.
//

struct Constants {
    struct Key {
        public static let appId = "appId"
        public static let appKey = "appKey"
        public static let miniProgramId = "miniProgramId"
        
        public static let miniProgramType = "miniProgramType"
        
        public static let scene = "scene"
        public static let timeline = "timeline"
        public static let session = "session"
        public static let favorite = "favorite"
        
        public static let text = "text"
        
        public static let data = "data"
        public static let thumbnailData = "thumbnailData"
        
        public static let url = "webPage"
        
        public static let path = "path"
        
        public static let title = "title"
        public static let image = "image"
        public static let thumbnail = "thumbnail"
        public static let description = "description"
        
        public static let package = "?package="
        
        public static let messageExt = "messageExt"
        public static let mediaTagName = "mediaTagName"
        public static let messageAction = "messageAction"
        
        public static let platform = "platform"
        
        public static let iOS = "iOS"
        
        public static let result = "result"
        
        public static let videoUrl = "videoUrl"
        public static let videoLowBandUrl = "videoLowBandUrl"
    }
    
    struct CallResults {
        public static let resultDone = "done"
        public static let resultErrorNeedWeChat = "wxapi not configured"
        public static let resultMessageNeedWeChat = "please config  wxapi first"
    }
    
    struct Methods {
        public static let registerApp = "registerApp"
        public static let unregisterApp = "unregisterApp"
        public static let weChatResponse = "weChatResponse"
        public static let shareText = "shareText"
        public static let shareImage = "shareImage"
        public static let shareMusic = "shareMusic"
        public static let shareVideo = "shareVideo"
        public static let shareWebPage = "shareWebPage"
        public static let shareMiniProgram = "shareMiniProgram"
        public static let launchMiniProgram = "launchMiniProgram"
        public static let sendAuth = "sendAuth"
    }
}
