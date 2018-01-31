/*
 *  Copyright 2018 CJWW Development
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cjwwdev.auth.actions

import com.cjwwdev.http.headers.HeaderPackage
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest

class BaseAuthSpec extends PlaySpec {

  class Setup extends BaseAuth

  "validateAppId" should {
    "return NotAuthorised" when {
      "No appId is found in the request headers" in new Setup {
        val request = FakeRequest()

        val result = validateAppId(request)

        result mustBe NotAuthorised
      }

      "An unknown appId is found in the request header" in new Setup {
        val request = FakeRequest()
          .withHeaders("cjww-headers" -> HeaderPackage("unknown app id", "testCookieId").encryptType)

        val result = validateAppId(request)

        result mustBe NotAuthorised
      }
    }

    "return Authenticated" when {
      "a valid appId is found in the headers" in new Setup {
        val request = FakeRequest()
          .withHeaders("cjww-headers" -> HeaderPackage("testSessionStoreId", "testCookieId").encryptType)

        val result = validateAppId(request)

        result mustBe Authenticated
      }
    }
  }
}
