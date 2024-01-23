import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-identify-ocr' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

export type IdentOcrTypes = 'FrontId' | 'BackId' | 'PassportMrzKey';

const IdentifyOcr = NativeModules.IdentifyOcr
  ? NativeModules.IdentifyOcr
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export async function processImage(
  base64Image: string,
  type: IdentOcrTypes
): Promise<any> {
  return await IdentifyOcr.processImage(type, base64Image);
}
