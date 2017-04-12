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

import com.cjwwdev.bootstrap.config.BaseConfiguration
import com.cjwwdev.logging.Logger
import play.api.mvc.{Request, Result}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

sealed trait AuthorisationResult
case object Authorised extends AuthorisationResult
case object NotAuthorised extends AuthorisationResult

trait BaseAuth extends BaseConfiguration {

  protected def openActionVerification(f: AuthorisationResult => Future[Result])(implicit request: Request[_]): Future[Result] = {
    f(checkAppId)
  }

  private[actions] def checkAppId(implicit request: Request[_]) = {
    Try(request.headers("appId")) match {
      case Success(appId) => appId match {
        case AUTH_SERVICE_ID | AUTH_MIRCOSERVICE_ID | ACCOUNTS_MIRCOSERVICE_ID | SESSION_STORE_ID => Authorised
        case _ =>
          Logger.warn("[BackendController] - [checkAuth] : API CALL FROM UNKNOWN SOURCE - ACTION DENIED")
          NotAuthorised
      }
      case Failure(_) =>
        Logger.error("[BackendController] - [checkAuth] : AppId not found in header")
        Logger.warn("[BackendController] - [checkAuth] : API CALL FROM UNKNOWN SOURCE - ACTION DENIED")
        NotAuthorised
    }
  }
}
