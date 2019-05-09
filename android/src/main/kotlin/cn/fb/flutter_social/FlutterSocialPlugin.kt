package cn.fb.flutter_social

import cn.fb.flutter_social.constant.WeChatPluginMethods
import cn.fb.flutter_social.constant.WeChatPluginMethods.IS_WE_CHAT_INSTALLED
import cn.fb.flutter_social.handler.*
import cn.fb.flutter_social.handler.FlutterAuthHandler
import cn.fb.flutter_social.handler.FlutterLaunchMiniProgramHandler
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class FlutterSocialPlugin(private val registrar: Registrar, channel: MethodChannel) : MethodCallHandler {
    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "cn.fb/flutter_social")

            WXAPiHandler.setRegistrar(registrar)
            FlutterResponseHandler.setMethodChannel(channel)
            channel.setMethodCallHandler(FlutterSocialPlugin(registrar, channel))
        }
    }

    private val flutterShareHandler = FlutterShareHandler()
    private val flutterAuthHandler = FlutterAuthHandler(channel)
    private val flutterPayHandler = FlutterPayHandler()
    private val flutterLaunchMiniProgramHandler = FlutterLaunchMiniProgramHandler()
    private val flutterSubscribeMsgHandler = FlutterSubscribeMsgHandler()

    init {
        flutterShareHandler.setRegistrar(registrar)
        flutterShareHandler.setMethodChannel(channel)
        registrar.addViewDestroyListener {
            flutterAuthHandler.removeAllListeners()
            false
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result): Unit {
        print(call.method)
        if (call.method == WeChatPluginMethods.REGISTER_APP) {
            WXAPiHandler.registerApp(call, result)
            return
        }


        if (call.method == WeChatPluginMethods.UNREGISTER_APP) {
//            FluwxShareHandler.unregisterApp(call)
//            result.success(true)
            return
        }

        if (call.method == IS_WE_CHAT_INSTALLED) {
            WXAPiHandler.checkWeChatInstallation(result)
            return
        }

        if ("sendAuth" == call.method) {
            flutterAuthHandler.sendAuth(call, result)
            return
        }

        if ("authByQRCode" == call.method) {
            flutterAuthHandler.authByQRCode(call, result)
            return
        }

        if ("stopAuthByQRCode" == call.method) {
            flutterAuthHandler.stopAuthByQRCode(result)
            return
        }

        if (call.method == WeChatPluginMethods.ORDER) {
            flutterPayHandler.pay(call, result)
            return
        }

        if (call.method == WeChatPluginMethods.LAUNCH_MINI_PROGRAM) {
            flutterLaunchMiniProgramHandler.launchMiniProgram(call, result)
            return
        }

        if (WeChatPluginMethods.SUBSCRIBE_MSG == call.method) {
            flutterSubscribeMsgHandler.subScribeMsg(call, result)
            return
        }

        if (call.method.startsWith("share")) {
            flutterShareHandler.handle(call, result)
        } else {
            result.notImplemented()
        }


    }
}
