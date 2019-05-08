//import 'dart:async';
//
//import 'package:flutter/foundation.dart';
//import 'package:flutter/services.dart';
//
//import 'package:flutter_social/src/wechat_type.dart';
//import 'package:flutter_social/src/models/wechat_share_models.dart';
//import 'package:flutter_social/src/models/wechat_response.dart';
//
//export 'package:flutter_social/src/wechat_type.dart';
//export 'package:flutter_social/src/models/wechat_share_models.dart';
//
//
//class FlutterSocial {
//  static final MethodChannel _channel = const MethodChannel('flutter_social')
//    ..setMethodCallHandler(_handler);
//
//  StreamController<WeChatShareResponse> _responseShareController =
//  new StreamController.broadcast();
//
//  static Future<String> get platformVersion async {
//    final String version = await _channel.invokeMethod('getPlatformVersion');
//    return version;
//  }
//
//  static Future<dynamic> _handler(MethodCall methodCall) {
//    if ("onShareResponse" == methodCall.method) {
//      _responseShareController
//          .add(WeChatShareResponse.fromMap(methodCall.arguments));
//    } else if ("onAuthResponse" == methodCall.method) {
//      _responseAuthController
//          .add(WeChatAuthResponse.fromMap(methodCall.arguments));
//    } else if ("onLaunchMiniProgramResponse" == methodCall.method) {
//      _responseLaunchMiniProgramController
//          .add(WeChatLaunchMiniProgramResponse.fromMap(methodCall.arguments));
//    } else if ("onPayResponse" == methodCall.method) {
//      _responsePaymentController
//          .add(WeChatPaymentResponse.fromMap(methodCall.arguments));
//    } else if ("onSubscribeMsgResp" == methodCall.method) {
//      _responseFromSubscribeMsg
//          .add(WeChatSubscribeMsgResp.fromMap(methodCall.arguments));
//    } else if ("onAuthByQRCodeFinished" == methodCall.method) {
//      _handleOnAuthByQRCodeFinished(methodCall);
//    } else if ("onAuthGotQRCode" == methodCall.method) {
//      _onAuthGotQRCodeController.add(methodCall.arguments);
//    } else if ("onQRCodeScanned" == methodCall.method) {
//      _onQRCodeScannedController.add(null);
//    }
//
//    return Future.value(true);
//  }
//
//  ///[appId] is not necessary.
//  ///if [doOnIOS] is true ,fluwx will register WXApi on iOS.
//  ///if [doOnAndroid] is true, fluwx will register WXApi on Android.
//  static Future register({
//    String appId,
//    //        bool doOnIOS: true,
//    //        doOnAndroid: true,
//    //        enableMTA: false
//  }) async {
//    return await _channel.invokeMethod("registerApp", {
//      "appId": appId,
//      //      "iOS": doOnIOS,
//      //      "android": doOnAndroid,
//      //      "enableMTA": enableMTA
//    });
//  }
//
//  ///the [WeChatShareModel] can not be null
//  ///see [WeChatShareWebPageModel]
//  /// [WeChatShareTextModel]
//  ///[WeChatShareVideoModel]
//  ///[WeChatShareMusicModel]
//  ///[WeChatShareImageModel]
//  static Future share(WeChatShareModel model) async {
//    if (_shareModelMethodMapper.containsKey(model.runtimeType)) {
//      return await _channel.invokeMethod(
//          _shareModelMethodMapper[model.runtimeType], model.toMap());
//    } else {
//      return Future.error("no method mapper found[${model.runtimeType}]");
//    }
//  }
//
//  static Future pay({
//    @required String appId,
//    @required String partnerId,
//    @required String prepayId,
//    @required String packageValue,
//    @required String nonceStr,
//    @required String timeStamp,
//    @required String sign,
//    String signType: "",
//    String extData: "",
//  }) async {
//    return await _channel.invokeMethod("order", {
//      "appId": appId,
//      "partnerId": partnerId,
//      "prepayId": prepayId,
//      "packageValue": packageValue,
//      "nonceStr": nonceStr,
//      "timeStamp": timeStamp,
//      "sign": sign,
//      "signType": signType,
//      "extData": extData,
//    });
//  }
//
//  /// open mini-program
//  /// see [WeChatMiniAppType]
//  Future launchMiniProgram(
//      {@required String username,
//      String path,
//      String miniProgramType = WeChatMiniAppType.RELEASE}) async {
//    assert(username != null && username.trim().isNotEmpty);
//    return await _channel.invokeMethod("launchMiniProgram", {
//      "userName": username,
//      "path": path,
//      "miniProgramType": miniProgramType
//    });
//  }
//}
//
//const Map<Type, String> _shareModelMethodMapper = {
//  WeChatShareTextModel: "shareText",
//  WeChatShareImageModel: "shareImage",
//  WeChatShareMusicModel: "shareMusic",
//  WeChatShareVideoModel: "shareVideo",
//  WeChatShareWebPageModel: "shareWebPage",
//  WeChatShareMiniProgramModel: "shareMiniProgram"
//};

library FlutterSocial;

export 'src/flutter_social_iml.dart';
export 'src/models/wechat_auth_by_qr_code.dart';
export 'src/models/wechat_response.dart';
export 'src/models/wechat_share_models.dart';
export 'src/wechat_type.dart';
