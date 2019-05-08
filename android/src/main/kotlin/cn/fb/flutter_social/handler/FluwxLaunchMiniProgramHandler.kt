package cn.fb.flutter_social.handler

import cn.fb.flutter_social.constant.WechatPluginKeys

import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

internal class FluwxLaunchMiniProgramHandler {

    fun launchMiniProgram(call: MethodCall, result: MethodChannel.Result) {
        val req = WXLaunchMiniProgram.Req()
        req.userName = call.argument<String?>("userName") // 填小程序原始id
        req.path = call.argument<String?>("path") ?: "" //拉起小程序页面的可带参路径，不填默认拉起小程序首页

        // 可选打开 开发版，体验版和正式版
        val type = call.argument("miniProgramType") ?: "release"
        req.miniprogramType = when (type) {
            "test" -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
            "preview" -> WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
            else -> WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
        }

        val done = WXAPiHandler.wxApi?.sendReq(req)
        result.success(mapOf(
                WechatPluginKeys.PLATFORM to WechatPluginKeys.ANDROID,
                WechatPluginKeys.RESULT to done
        ))
    }
}