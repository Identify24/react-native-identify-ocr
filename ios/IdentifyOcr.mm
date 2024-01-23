#import <React/RCTBridgeModule.h>
#import <React/RCTConvert.h>

@interface RCT_EXTERN_MODULE(IdentifyOcr, NSObject)

 

RCT_EXTERN_METHOD(processImage:(NSString *)type
                  base64Image: (NSString *)base64Image
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
