[![Build Status](https://circleci.com/gh/SmartThingsOSS/smartthings-scala-toolkit.svg?style=svg)](https://circleci.com/gh/SmartThingsOSS/smartthings-scala-toolkit)

# Scala Toolkit

A collection of small libraries, generally based on [Typelevel](https://typelevel.org/projects/) libraries that
provide useful building blocks for higher level tools and applications.

## Sub Modules

### Core

Provides basic abstractions that other modules use.

- Effect monad based on Cats Effect Async with implicit conversions for IO and Future.

### Config

Simple configuration pattern using [Scopt](https://github.com/scopt/scopt), [PureConfig](https://github.com/pureconfig/pureconfig) and [Typesafe Config](https://github.com/lightbend/config)

#### Usage
```scala
val config = Configuration() { config =>
  // PureConfig isn't strictly required and this line can be swapped out with what
  // ever configuration parsing abstraction you desire.
  pureconfig.loadConfig[TestConfig](config, "test").left.map(ConfigurationReadError)
}
```

### Modules

We try not to be opinionated about how projects do DI and IoC.  Regardless of the approach
its typically helpful that there are some lifecycle hooks and this library provides a single
trait to help define a common contract for startup/shutdown lifecycle.
