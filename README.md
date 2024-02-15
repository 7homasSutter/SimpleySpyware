# SimpleSpyware - Black HatEurope 2019 - Demo App 

This repo contains a demonstration spyware app. The app shows how Androids foreground services can be used to collect user data. More technical information can be found within the presentation slides.

### Download
The latest prebuilt apk can be downloaded from the releases page:
* [APKs](https://github.com/7homasSutter/SimpleSpyware/releases)
* [WhitePaper](https://arxiv.org/abs/2011.14117)

### General Notes
Please do not use or redistribute parts of this code for criminal activity. Spying on others is a crime! This code is only intended for educational purposes and should only be used on your own devices for testing.

All the data collected by this app is stored in the internal storage of your device. Moreover, the app does not have the internet permission and therefore everything stays within the apps context and is safe for testing.

Some notes about the features:
- Timings: Executation may change with your phones state.
- Cam: The gallery deletes all the photos after 100 images.
- Location: Remember that GPS data drains a lot of battery power.
- Micrphone: Remember that only one app can use the microphone. So you can't record phone calls and spy on the mic at the same time. A maximum recording time of one hour is set for every media file. 
- Visibility: Some permission usage monitors may detect the app. If you overuse the scheduling, you may get a battery power notification after some hours.

### Patch - CVE-2019-2219 

Patch for this attack was released in [december 2019](https://android.googlesource.com/platform/frameworks/base/+/055976434b5d0ec5f6a3746124f4e81aecf8a426). Foreground Services will now have to show a notification for at least 5 seconds.

### Build
The ExampleA folder contains a Android Studio 3.2 project. The project can be imported in Android Studio or directly build with gradle:

```
./gradlew build
```

You can install and debug via Android Studio or over adb with:
```
adb install ch.zhaw.init.orwell_a
```

### Attributions 
App Material Icons: Attribution: Google Inc. - [Licencse](https://creativecommons.org/licenses/by/4.0/deed.en)

- Thanks to all Security Teams for resolving this issue on future Android versions.
