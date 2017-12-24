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
import com.cjwwdev.http.exceptions.NotFoundException
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.security.encryption.DataSecurity
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Mockito.when
import org.mockito.Mockito.reset
import org.mockito.ArgumentMatchers
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class AuthConnectorSpec extends PlaySpec with MockitoSugar {

  final val now = new DateTime(DateTimeZone.UTC)

  val testContext = AuthContext(
    contextId = "testContextId",
    user = User(
      id = "testUserId",
      firstName = Some("testFirstName"),
      lastName = Some("testLastName"),
      orgName = None,
      credentialType = "testType",
      role = Some("testRole")
    ),
    basicDetailsUri = "/test/uri",
    enrolmentsUri = "/test/uri",
    settingsUri = "/test/uri",
    now
  )

  val mockHttp = mock[Http]

  val testConnector = new AuthConnector {
    override val http = mockHttp
    override val authMicroservice = "http://test.com/auth-microservice"
    override val sessionStore = "http://test.com/session-store"
  }

  "getContext" ignore {
    "return an AuthContext" when {
      "given a request with a valid context id in the session" in {
        implicit val request: FakeRequest[_] = FakeRequest().withHeaders("cookieId" -> "testCookieId")

        when(mockHttp.GET[JsValue](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(Json.parse("""{"contextId" : "testContextId"}""")))

        //reset(mockHttp)

        when(mockHttp.GET[AuthContext](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(testContext))

        val result = Await.result(testConnector.getContext(request), 5.seconds)
        result mustBe Some(testContext)
      }
    }

    "return none" when {
      "no auth context was found" in {
        implicit val request: FakeRequest[_] = FakeRequest().withHeaders("cookieId" -> "testCookieId")

        when(mockHttp.GET[JsValue](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(Json.parse("""{"contextId" : "testContextId"}""")))

        //reset(mockHttp)

        when(mockHttp.GET[AuthContext](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.failed(new NotFoundException("test message")))

        val result = Await.result(testConnector.getContext, 5.seconds)
        result mustBe None
      }
    }
  }
}
