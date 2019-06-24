# Android Sample app that demonstrate how proguard can break kotlin coroutines

- kotlin version 1.3.40
- coroutines version 1.2.1
- android build tool version 3.3.2
- reported here: https://github.com/Kotlin/kotlinx.coroutines/issues/1035

## Setup:

https://github.com/awenger/coroutinesobfuscationcrash/blob/75d79c59b68ef419826e04e07fa019ca29a0e7cf/app/src/main/java/com/github/awenger/coroutinesobfuscationcrash/MainActivity.kt#L38-L63

- suspend function `retrieve` in line 51-63 throws an exception or handels cancel during onresume
- suspend function `call` in line 37-48 calls the previous function via `async{ retrieve() }....await()` and should catch the exception

Without proguard minification (`minifyEnabled false`) the exception is handled properly:
```
D/A2: failing with IOException
E/Main: caught failed
D/Main: success: false
```

or (needs a few tries till everything aligns):

```
D/A2: failing with IOException
D/A1: cancel during on resume with kotlinx.coroutines.JobCancellationException: Parent job is Cancelling; job=ScopeCoroutine{Cancelling}@f2b7a58
E/Main: caught failed
D/Main: success: false
```

With proguard minification (`minifyEnabled true`) the exceptions are not caught and the whole app crashes with a `ClassCastException`:
```
D/A2: failing with IOException
D/Main: all successful retrieved
    
    --------- beginning of crash
E/AndroidRuntime: FATAL EXCEPTION: DefaultDispatcher-worker-3
    Process: com.github.awenger.coroutinesobfuscationcrash, PID: 3118
    java.lang.ClassCastException: a.g$b cannot be cast to java.lang.Boolean
        at com.github.awenger.coroutinesobfuscationcrash.MainActivity$g.a(Unknown Source:125)
        at a.b.b.a.a.b(Unknown Source:18)
        at kotlinx.coroutines.b.o.a(Unknown Source:103)
        at kotlinx.coroutines.bj.b(Unknown Source:81)
        at kotlinx.coroutines.bj.a(Unknown Source:177)
        at kotlinx.coroutines.bj.a(Unknown Source:159)
        at kotlinx.coroutines.bj.b(Unknown Source:4)
        at kotlinx.coroutines.a.b(Unknown Source:8)
        at a.b.b.a.a.b(Unknown Source:56)
        at kotlinx.coroutines.aq.run(Unknown Source:81)
        at kotlinx.coroutines.d.a.b(Unknown Source:0)
        at kotlinx.coroutines.d.a.a(Unknown Source:0)
        at kotlinx.coroutines.d.a$b.run(Unknown Source:397)
    
    
    --------- beginning of system
```

were `a.g$b` is `kotlin.Result$Failure`:
```
cat app/build/outputs/mapping/debug/mapping.txt | grep 'a.g$b'
android.support.constraint.solver.Pools$SimplePool -> android.support.constraint.a.g$b:
kotlin.Result$Failure -> a.g$b:
```

Just change the minifyEnabled property here and the behavior should change: https://github.com/awenger/coroutinesobfuscationcrash/blob/75d79c59b68ef419826e04e07fa019ca29a0e7cf/app/build.gradle#L19