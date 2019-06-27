# Android Sample app that demonstrate how R8 builds can crash on Android 4.x devices

- setup a keystore to build a prod apk
- build release apk `./gradlew assembleRelease`
- install apk on a Android 4.x.y device `adb install app/build/outputs/apk/release/app-release.apk`
- click the big button to get a
```
 AndroidRuntime  E  FATAL EXCEPTION: DefaultDispatcher-worker-1
                 E  Process: com.github.awenger.coroutinesobfuscationcrash, PID: 4883
                 E  java.lang.IllegalAccessError: tried to access class b.a.d$a[] from class c.b.b.i.i.a
                 E      at c.b.b.i.i.a.a(:34)
                 E      at com.github.awenger.coroutinesobfuscationcrash.MainActivity$b.b(:7)
                 E      at com.github.awenger.coroutinesobfuscationcrash.MainActivity$b.a()
                 E      at c.b.b.i.i.a.a(:57)
                 E      at com.github.awenger.coroutinesobfuscationcrash.MainActivity.a(:20)
                 E      at e.b.a.a.a.b(:27)
                 E      at f.k.i.a.a.a()
                 E      at b.a.p0.run(:2)
                 E      at b.a.w1.b.a()
                 E      at b.a.w1.b$a.run(:18)
```