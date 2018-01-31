/*
 *   Copyright 2018 CJWW Development
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.cjwwdev.auth.actions

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.auth.models.{AuthContext, User}
import com.cjwwdev.http.headers.HeaderPackage
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.mvc.Request
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class AuthorisationSpec extends PlaySpec with MockitoSugar{

  val mockAuthConnector = mock[AuthConnector]

  class Setup extends Authorisation {
    override val authConnector = mockAuthConnector

    def testAuthorisation(id: String)(implicit request: Request[_]) = authorised(id) { context =>
      Future.successful(Ok("testUserId"))
    }
  }

  val now = new DateTime(DateTimeZone.UTC)

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

  "authorised" should {
    "return an Ok" when {
      "an AuthContext has been found and the user id's match" in new Setup {
        implicit val request = FakeRequest()
          .withHeaders("cjww-headers" -> HeaderPackage("testSessionStoreId", "testCookieId").encryptType)

        when(mockAuthConnector.getContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testContext)))

        val result = testAuthorisation("testUserId")

        status(result) mustBe OK
        contentAsString(result) mustBe "testUserId"
      }
    }

    "return a forbidden" when {
      "no AuthContext has been found" in new Setup {
        implicit val request = FakeRequest()
          .withHeaders("cjww-headers" -> HeaderPackage("testSessionStoreId", "testCookieId").encryptType)

        when(mockAuthConnector.getContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        val result = testAuthorisation("testMismatchId")

        status(result) mustBe FORBIDDEN
      }

      "the users id don't match" in new Setup {
        implicit val request = FakeRequest()
          .withHeaders("cjww-headers" -> HeaderPackage("testSessionStoreId", "testCookieId").encryptType)

        when(mockAuthConnector.getContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testContext)))

        val result = testAuthorisation("testMismatchId")

        status(result) mustBe FORBIDDEN
      }
    }
  }
}
