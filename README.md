# react-native-identify-ocr

Ocr for Identify sdk in the React Native

## Installation

```sh
npm install react-native-identify-ocr
```

## Usage

```js
import { multiply } from 'react-native-identify-ocr';

// ...


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

// ...
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
