# Data Injector

[![CI Status](http://img.shields.io/travis/crescentflare/DataInjector.svg?style=flat)](https://travis-ci.org/crescentflare/DataInjector)
[![License](https://img.shields.io/cocoapods/l/DataInjector.svg?style=flat)](http://cocoapods.org/pods/DataInjector)
[![Version](https://img.shields.io/cocoapods/v/DataInjector.svg?style=flat)](http://cocoapods.org/pods/DataInjector)
[![Version](https://img.shields.io/maven-central/v/com.crescentflare.datainjector/DataInjectorLib.svg?style=flat)](https://repo1.maven.org/maven2/com/crescentflare/datainjector/DataInjectorLib)

Data injector is a project to easily manipulate JSON data before being handled by the application. For example, to map restful API responses into view models together with view content or to fix API responses.

Use the library together with [JsonInflator](https://github.com/crescentflare/JsonInflator) and [UniLayout](https://github.com/crescentflare/UniLayout) to share logic between iOS and Android and to develop them real-time simultaneously.


### Features

* Provides an easy way to access and modify nested data structures (like lists and maps on Android, or arrays and dictionaries on iOS)
* Convert between data types easily
* A set of injectors to help modify target data based on source data
* A set of transformers to convert source data (also can be used together with injectors)


### iOS integration guide

The library is available through [CocoaPods](http://cocoapods.org). To install it, simply add the following line to your Podfile.

```ruby
pod "DataInjector", '~> 0.3.1'
```


### Android integration guide

When using gradle, the library can easily be imported into the build.gradle file of your project. Add the following dependency:

```
compile 'com.crescentflare.datainjector:DataInjectorLib:0.3.1'
```


### Example

The provided example shows how to map a structured table of customers into a list suitable for display in a table view (iOS) or recycler view (Android).


### Status

The library is in its initial state but has basic functionality which can already be useful. However, there may be bugs. More features and utilities will be added in the future.
