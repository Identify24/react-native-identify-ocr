import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { processImage } from 'react-native-identify-ocr';

export default function App() {
  const [result, setResult] = React.useState<String | undefined>();

  React.useEffect(() => {
    const getData = async () => {
      const data: any = await processImage('some·base64·data', 'FrontId');
      console.log(data);

      setResult(data);
    };
    getData();
  }, []);
  return (
    <View style={styles.container}>
      <Text style={{ color: 'white' }}>Result: {result?.toString()}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
