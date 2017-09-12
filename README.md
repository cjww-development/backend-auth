[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[ ![Download](https://api.bintray.com/packages/cjww-development/releases/backend-auth/images/download.svg) ](https://bintray.com/cjww-development/releases/backend-auth/_latestVersion)

backend auth
============

Mechanisms to determine if a user is authenticated and subsequently authorised to make api calls

To utilise this library add this to your sbt build file

```sbtshell
    "com.cjww-dev.libs" % "backend-auth_2.11" % "2.9.0" 
```

## About
#### Authorisation.scala

Determines whether the user is authorised to access the requested resource.

```scala
    class ExampleController @Inject()() extends Controller with Authorisation {
      def exampleAction(id: String): Action[AnyContent] = Action.async {
        implicit request =>
          authorised(id) { context =>
            Ok
          }
      }
    }
```

#### Authentication.scala
Validates whether the user has an active session.

```scala
    class ExampleController @Inject()() extends Controller with Authentication {
      def exampleAction(id: String): Action[AnyContent] = Action.async {
        implicit request =>
          authenticated(id) {
            Ok
          }
      }
    }
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")

