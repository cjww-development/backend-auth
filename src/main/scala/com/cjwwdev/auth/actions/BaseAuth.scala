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
package com.cjwwdev.auth.actions

import com.cjwwdev.auth.models.AuthContext
import com.cjwwdev.config.ConfigurationLoader
import org.slf4j.{Logger, LoggerFactory}
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.Forbidden

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

sealed trait AuthorisationResult
case class Authorised(authContext: AuthContext) extends AuthorisationResult
case object Authenticated extends AuthorisationResult
case object NotAuthorised extends AuthorisationResult

trait BaseAuth {
  val config: ConfigurationLoader

  val DEVERSITY_FE_ID          = config.getApplicationId("deversity-frontend")
  val DEVERSITY_ID             = config.getApplicationId("deversity")
  val DIAG_ID                  = config.getApplicationId("diagnostics-frontend")
  val HUB_ID                   = config.getApplicationId("hub-frontend")
  val AUTH_SERVICE_ID          = config.getApplicationId("auth-service")
  val AUTH_MICROSERVICE_ID     = config.getApplicationId("auth-microservice")
  val ACCOUNTS_MICROSERVICE_ID = config.getApplicationId("accounts-microservice")
  val SESSION_STORE_ID         = config.getApplicationId("session-store")

  private val idSet = List(DEVERSITY_FE_ID, DEVERSITY_ID, DIAG_ID, HUB_ID, AUTH_SERVICE_ID, AUTH_MICROSERVICE_ID, ACCOUNTS_MICROSERVICE_ID, SESSION_STORE_ID)

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  protected def openActionVerification(f: => Future[Result])(implicit request: Request[_]): Future[Result] = {
    checkAppId match {
      case Authenticated  => f
      case _              => Future.successful(Forbidden)
    }
  }

  protected def checkAppId(implicit request: Request[_]): AuthorisationResult = {
    request.headers.get("appId").fold(notAuthorised("AppId not found in header"))(
      appId => if(idSet.contains(appId)) Authenticated else notAuthorised("API CALL FROM UNKNOWN SOURCE - ACTION DENIED")
    )
  }

  private def notAuthorised(msg: String): AuthorisationResult = {
    logger.error(s"[checkAuth] - $msg")
    NotAuthorised
  }
}
