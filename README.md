# InAppDevTools [![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=library&style=plastic)](https://bintray.com/rafaco/InAppDevTools/inappdevtools/_latestVersion) [![Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/es/rafaco/inappdevtools/es.rafaco.inappdevtools.gradle.plugin/maven-metadata.xml.svg?label=plugin&colorB=blue?style=plastic)](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) [![Maturity](https://img.shields.io/badge/maturity-experimental-red.svg?style=plastic)](https://github.com/rafaco/InAppDevTools/commits)

<p style="text-align: center;">
<img src="https://github.com/rafaco/InAppDevTools/wiki/images/social.png" width="200"></p>

**InAppDevTools is an library that make Android developers live easier and more productive. It allows to inspect, debug and report from within your own app, using the device screen!**


**Auto-logger, crash handler, source browser, layout inspector, storage editor, logcat viewer, network activity, info panels, flexible reports, class/method tracker, coding helpers and much more.**

- Inspectors: sources, logs, view layout, edit your storage (db, SharedPrefs and Files) and info panels
- Auto generate a FriendlyLog with basic reproduction steps as well as advanced entries (lifecycle events, network requests, errors, device events,...)
- See a crash detail immediately and navigate to the causing source lines.
- Send flexible reports by email or other apps
- Customize your own tools, easily run your task and use our dev helpers.
- Easy to install and configurable

<p>
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Home.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Info.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Crash.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Source.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Logs.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_View.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_View2.jpg" width="200">
<img src="https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Storage.jpg" width="200">
<!---![Home](https://github.com/rafaco/InAppDevTools/wiki/screenshots/Iadt_Home.jpg)-->
</p>

For extended feature description, visit our wiki: [Feature description](https://github.com/rafaco/InAppDevTools/wiki/Feature-description)

## Installation <a name="setup"/>

### Limitations <a name="req"/>
- minSdkVersion >= 16 (Jelly Bean). Check it at your app/build.gradle
- Minimun Gradle version: //TODO

### Basic set-up <a name="basic"/>
For standard Android projects you only need to modify 2 gradle files and rebuild your app.

On your root build.gradle, import our plugin (plugins closure must be just after buidscript). Then add the JitPack repository (TEMP, it's a transitive dependency):

```gradle
plugins {
    id "es.rafaco.inappdevtools" version "0.0.12"
}

allprojects {
    repositories {
        maven { url "https://jitpack.io"}
    }
}
```

On your app build.gradle, you have to add targetCompatibility with Java8. You can keep smaller values for sourceCompatibility. 

Then include our library artifacts in your dependencies. You can choose between AndroidX or Support flavor depending on the libraries you project use. Noop flavor is also available and recommended for release compilations.

```gradle

android {
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
dependencies {
    debugImplementation 'es.rafaco.inappdevtools:androidx:0.0.50'
    //debugImplementation 'es.rafaco.inappdevtools:support:0.0.50'
    
    releaseImplementation 'es.rafaco.inappdevtools:noop:0.0.50'
}
```


| Flavor | Version | Description |
|---|---|---|
|androidx | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=androidx&style=flat-square) | For modern projects using AndroidX libraries. Jetifier enabled is curretly needed.|
|support | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=support&style=flat-square) | For legacy projects using Android Support libraries.|
|noop | ![Library](https://img.shields.io/maven-metadata/v/http/jcenter.bintray.com/es/rafaco/inappdevtools/inappdevtools/maven-metadata.xml.svg?colorB=blue&label=noop&style=flat-square) | No operation flavor recommended for your release versions (androidx and support).|

Don't include the "v" character in dependencies. 

### Add network interceptor (optional) <a name="network"/>
If your app use Retrofit, we can record all network network communications for you, allowing you to inspect and report them. To enable it, add our OkHttpClient to your Retrofit initialization class:

```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(DevTools.getOkHttpClient())
                .build();
```
For extended installation instructions, visit our wiki: [Extended installation](https://github.com/rafaco/InAppDevTools/wiki/Extended-installation)


## Usage <a name="usage"/>

After the [installation](#setup) you only need to rebuild your app and run it on a real device or emulator. 

On crash our UI will automatically popup but you can also invoke it at any time by using one of the following methods:
- Shake your device
- Touch our notification
- Tap our floating icon over your app (disabled by default)
- Or programmatically using our public interface (Iadt):
```java
Iadt.show();
Iadt.hide();
```


### Configuration <a name="configuration"/>
You can configure our library behaviour at build time by using our gradle extension on your app module's build.gradle. Our plugin get affected by this configuration and values will remain after cleaning app's data. 
```gradle
apply plugin: 'es.rafaco.inappdevtools'

inappdevtools {
    enabled = true
    email = 'yourmail@yourdomain.com'
}
```

You can override previous configurations at run time by using the ConfigScreen (Home, Config) or programmatically from your sources. Values will be lost after cleaning app's data. 
```java
Iadt.getConfig().setBoolean(Config.ENABLED, false);
Iadt.restartApp();
```

Available properties:

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `enabled` | boolean | true | Disable all and simulate the no-op library and plugin |
| `email` | String | null | Default email to use for reports |
| `overlay_enabled` | boolean | true | Disable our overlay interface  |
| `invocation_by_shake` | boolean | true | Disable opening our UI on device shake |
| `invocation_by_icon` | boolean | false | Enable a permanent overlay icon to open our UI |
| `invocation_by_notification` | boolean | true | Disable showing our notification to open the UI |
| `call_default_crash_handler` | boolean | false | Propagate unhandled exceptions to the default handler (for Crashlytics and similar) |
| `debug` | boolean | false | Enable debug mode for the library. It print extra logs and include our sources to your compilation  |
<!-- ## Customization <a name="customization"/> -->



## Contributing [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/rafaco/InAppDevTools/issues)

I'm really looking forward to create a small community around this library! You can use the [issues tab](https://github.com/rafaco/InAppDevTools/issues) to report bugs, request a feature or just to give me your feedback. Pull request are more than welcome.

### Building artifacts

Standard user don't need to manually download or build our artifacts as they are available at public repositories preconfigured by Android Studio. Just follow the [installation](#setup) process and rebuild your project.

All artifacts are generated from [this GitHub repo](https://github.com/rafaco/InAppDevTools), each one using different combinations of module and variant.

| Artifact | Module | Variant | Description | Publication |
| --- | --- | --- | --- | --- |
| es.rafaco.inappdevtools | plugin | - | IADT plugin for Gradle| [Gradle Plugin Portal](https://plugins.gradle.org/plugin/es.rafaco.inappdevtools) |
| es.rafaco.inappdevtools:support | library | support | IADT library for Support libraries | [Bintray](https://bintray.com/rafaco/InAppDevTools/support) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/support/) |
| es.rafaco.inappdevtools:androidx | library | androidx | IADT library for AndroidX libraries | [Bintray](https://bintray.com/rafaco/InAppDevTools/androidx) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/androidx/) |
| es.rafaco.inappdevtools:noop | noop | - | IADT library, no operational | [Bintray](https://bintray.com/rafaco/InAppDevTools/noop) / [jCenter](http://jcenter.bintray.com/es/rafaco/inappdevtools/noop/) |
| es.rafaco.compat:support | compat | support | Compat library for Support libraries | [Bintray](https://bintray.com/rafaco/Compat/support) / [jCenter](http://jcenter.bintray.com/es/rafaco/compat/support/) |
| es.rafaco.compat:androidx | compat | androidx | Compat library for AndroidX libraries | [Bintray](https://bintray.com/rafaco/Compat/androidx) / [jCenter](http://jcenter.bintray.com/es/rafaco/compat/androidx/) |
| es.rafaco.iadt.demo | sample | androidx/support | Demo app  | ~~[Google Play](https://play.google.com)~~ |


### Continuous Integration <a name="ci"/>
(Work in progress)
[![CircleCI](https://circleci.com/gh/rafaco/InAppDevTools/tree/master.svg?style=svg)](https://circleci.com/gh/rafaco/InAppDevTools/tree/master) 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=alert_status)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools) 
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=security_rating)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=bugs)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=code_smells)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=sqale_index)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=coverage)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=rafaco_InAppDevTools&metric=ncloc)](https://sonarcloud.io/dashboard?id=rafaco_InAppDevTools)

### About the author

I'm a Senior Software Engineer who has always been working on proprietary software around Spain and UK. This is my first open source contribution and I'm looking forward to create a small community around.

I started this project as a playground to learn Android, then it become a personal tool to support my daily duties and then I realise it could be interesting for every Android developer. After 2 years of overnight coding fun, I've left a permanent work position to make it flexible and polished for everyone joy. I hope to publish a fully functional version at the end of this summer (2019).

## Thanks <a name="thanks"/>
- To [@whataa](https://github.com/whataa) for [pandora](https://github.com/whataa/pandora): library used for storage and view inspector.
- To [@jgilfelt](https://github.com/jgilfelt) for [chuck](https://github.com/jgilfelt/chuck): library used for network inspector.
- To [@alorma](https://github.com/alorma) for [timelineview](https://github.com/alorma/timelineview): library used at stacktrace.
- To [@tiagohm](https://github.com/tiagohm) for [CodeView](https://github.com/tiagohm/CodeView): inspiration for my codeview.
- To [@Zsolt Kocsi](https://github.com/zsoltk) for [paperwork](https://github.com/zsoltk/paperwork): inspiration for CompileConfig.
- To [@valdesekamdem](https://github.com/valdesekamdem) for [MaterialDesign-Toast](https://github.com/valdesekamdem/MaterialDesign-Toast): inspiration for our CustomToast.
- //TODO: add com.github.anrwatchdog:anrwatchdog?
- //TODO: add replacement for com.opencsv:opencsvallo

## Apps using this library <a name="usages"/>
- Your app linked here! Just ask me for it

## License <a name="license"/>
Apache-2.0
