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
package com.cjwwdev.auth.connectors

import com.cjwwdev.auth.models.{AuthContext, User}
import com.cjwwdev.http.verbs.Http
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.test.FakeRequest

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class AuthConnectorSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  val mockHttp = mock[Http]

  val testContext = AuthContext(
    _id = "testContextId",
    user = User(
      userId = "testUserId",
      firstName = "testFirstName",
      lastName = "testLastName"
    ),
    basicDetailsUri = "/test/uri",
    enrolmentsUri = "/test/uri",
    settingsUri = "/test/uri"
  )

  class Setup {
    val testConnector = new AuthConnector(mockHttp)
  }

  "getContext" ignore {
    "return an AuthContext" when {
      "given a request with a valid context id in the session" in new Setup {
        implicit val request = FakeRequest().withSession("contextId" -> "testContextId")

        when(mockHttp.GET[AuthContext](ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testContext))

        val result = Await.result(testConnector.getContext, 5.seconds)
        result mustBe testContext
      }
    }
  }
}
