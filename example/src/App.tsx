import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { processImage, type IdBackData, type IdFrontData } from 'react-native-identify-ocr';

export default function App() {
  const [resultIdBack, setIdBackResult] = React.useState<IdBackData | undefined>();
  const [resultIdFront, setIdFrontResult] = React.useState<IdFrontData | undefined>();




  React.useEffect(() => {
    const getData = async () => {
      const data: any = await processImage('some·base64·data', 'FrontId');
      setIdBackResult(data)
    }
    getData()
  }, []);
  return (
    <View style={styles.container}>
      <Text style={{ color: "white" }}>Result: {resultIdBack?.fullMrzKey}</Text>
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

 