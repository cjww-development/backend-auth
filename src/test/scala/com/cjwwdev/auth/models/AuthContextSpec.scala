// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.cjwwdev.auth.models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class AuthContextSpec extends PlaySpec {

  val testContext = AuthContext(
    contextId = "testContextId",
    user = User(
      userId = "testUserId",
      firstName = "testFirstName",
      lastName = "testLastName"
    ),
    basicDetailsUri = "/test/uri",
    enrolmentsUri = "/test/uri",
    settingsUri = "/test/uri"
  )

  val testJson = Json.parse(
    """
      |{
      |   "contextId":"testContextId",
      |   "user": {
      |     "userId":"testUserId",
      |     "firstName":"testFirstName",
      |     "lastName":"testLastName"
      |   },
      |   "basicDetailsUri":"/test/uri",
      |   "enrolmentsUri":"/test/uri",
      |   "settingsUri":"/test/uri"
      |}
    """.stripMargin
  )

  "AuthContext" should {
    "transform into json" in {
      val result = Json.toJson(testContext)
      result mustBe testJson
    }

    "transform into an auth context from json" in {
      val result = Json.fromJson[AuthContext](testJson).get
      result mustBe testContext
    }
  }
}
