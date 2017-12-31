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

import javax.inject.Inject

import com.cjwwdev.auth.models.AuthContext
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.exceptions.NotFoundException
import com.cjwwdev.http.utils.BackendHeaderUtils
import com.cjwwdev.http.verbs.Http
import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json.JsValue
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthConnectorImpl @Inject()(val http: Http, val loadedConfig: ConfigurationLoader) extends AuthConnector {
  val authMicroservice = loadedConfig.buildServiceUrl("auth-microservice")
  val sessionStore     = loadedConfig.buildServiceUrl("session-store")
}

trait AuthConnector extends BackendHeaderUtils {
  val http: Http

  val authMicroservice: String
  val sessionStore: String

  def getContext(implicit request: Request[_]): Future[Option[AuthContext]] = {
    http.GET[JsValue](s"$sessionStore/session/$getSessionId/context") flatMap { response =>
      val contextId = DataSecurity.decryptString(response.\("contextId").as[String])
      http.GET[AuthContext](s"$authMicroservice/get-context/$contextId") map {
        context => Some(context)
      } recover {
        case _: NotFoundException => None
      }
    }
  }
}
