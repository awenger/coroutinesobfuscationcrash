# Android Sample app that demonstrate how proguard can break kotlin coroutines

- kotlin version 1.3.40
- coroutines version 1.2.1
- android build tool version 3.3.2

## Setup:

https://github.com/awenger/coroutinesobfuscationcrash/blob/bdb0df11a856bff0ad3be2d96449c3ed06a153a3/app/src/main/java/com/github/awenger/coroutinesobfuscationcrash/MainActivity.kt#L37-L55

suspend function `retrieve` in line 50-55 throws an exception
suspend function `call` in line 37-48 calls the previous function via `async{ retrieve() }....await()` and should catch the exception

Without proguard minification (`minifyEnabled false`) the exception is handled properly:
```
D/Main: waited for 100ms, throwing IOException
E/Main: caught could not contact server
D/Main: success: false
```

With proguard minification (`minifyEnabled true`) the exceptions just disappear and the outcome of the program changes to:
```
D/Main: waited for 100ms, throwing IOException
D/Main: waited for 100ms, throwing IOException
D/Main: all successful retrieved
D/Main: success: true
```

Just change the minifyEnabled property here and the behavior should change: https://github.com/awenger/coroutinesobfuscationcrash/blob/bdb0df11a856bff0ad3be2d96449c3ed06a153a3/app/build.gradle#L19