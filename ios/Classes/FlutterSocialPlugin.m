#import "FlutterSocialPlugin.h"
#import <flutter_social/flutter_social-Swift.h>

@implementation FlutterSocialPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterSocialPlugin registerWithRegistrar:registrar];
}
@end
