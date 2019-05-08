import '../wechat_type.dart';

/// convert [WeChatMiniProgramType] to String
String miniProgramTypeToString(WeChatMiniProgramType type) {
  switch (type) {
    case WeChatMiniProgramType.PREVIEW:
      return "preview";
    case WeChatMiniProgramType.TEST:
      return "test";
    case WeChatMiniProgramType.RELEASE:
      return "release";
  }
  return "release";
}

/// convert [WeChatScene] to String
String weChatSceneToString(WeChatScene scene) {
  switch (scene) {
    case WeChatScene.SESSION:
      return "session";
    case WeChatScene.TIMELINE:
      return "timeline";
    case WeChatScene.FAVORITE:
      return "favorite";
  }
  return "session";
}
