package cn.fb.flutter_social

import cn.fb.flutter_social.constant.WeChatPluginMethods
import cn.fb.flutter_social.constant.WeChatPluginMethods.IS_WE_CHAT_INSTALLED
import cn.fb.flutter_social.handler.*
import cn.fb.flutter_social.handler.FluwxAuthHandler
import cn.fb.flutter_social.handler.FluwxLaunchMiniProgramHandler
import cn.fb.flutter_social.handler.FluwxShareHandler
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
            FluwxResponseHandler.setMethodChannel(channel)
            channel.setMethodCallHandler(FlutterSocialPlugin(registrar, channel))
        }
    }

    private val fluwxShareHandler = FluwxShareHandler()
    private val fluwxAuthHandler = FluwxAuthHandler(channel)
    private val fluwxPayHandler = FluwxPayHandler()
    private val fluwxLaunchMiniProgramHandler = FluwxLaunchMiniProgramHandler()
    private val fluwxSubscribeMsgHandler = FluwxSubscribeMsgHandler()

    init {
        fluwxShareHandler.setRegistrar(registrar)
        fluwxShareHandler.setMethodChannel(channel)
        registrar.addViewDestroyListener {
            fluwxAuthHandler.removeAllListeners()
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
            fluwxAuthHandler.sendAuth(call, result)
            return
        }

        if ("authByQRCode" == call.method) {
            fluwxAuthHandler.authByQRCode(call, result)
            return
        }

        if ("stopAuthByQRCode" == call.method) {
            fluwxAuthHandler.stopAuthByQRCode(result)
            return
        }

        if (call.method == WeChatPluginMethods.ORDER) {
            fluwxPayHandler.pay(call, result)
            return
        }

        if (call.method == WeChatPluginMethods.LAUNCH_MINI_PROGRAM) {
            fluwxLaunchMiniProgramHandler.launchMiniProgram(call, result)
            return
        }

        if (WeChatPluginMethods.SUBSCRIBE_MSG == call.method) {
            fluwxSubscribeMsgHandler.subScribeMsg(call, result)
            return
        }

        if (call.method.startsWith("share")) {
            fluwxShareHandler.handle(call, result)
        } else {
            result.notImplemented()
        }


    }
}
