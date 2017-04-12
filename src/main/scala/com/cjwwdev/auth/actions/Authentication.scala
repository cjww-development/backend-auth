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

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.auth.models.AuthContext
import play.api.mvc.{Request, Result}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Authentication extends BaseAuth {

  val authConnector: AuthConnector

  protected def authorised(userId: String)(f: AuthorisationResult => Future[Result])(implicit request: Request[_]): Future[Result] = {
    for {
      currentAuthority <- authConnector.getContext
      result <- f(mapToAuthResult(userId, currentAuthority))
    } yield {
      result
    }
  }

  private def mapToAuthResult(userId: String, context: Option[AuthContext])(implicit request: Request[_]): AuthorisationResult = {
    checkAppId match {
      case Authorised => context match {
        case Some(_) => Authorised
        case None => NotAuthorised
      }
      case NotAuthorised => NotAuthorised
    }
  }
}
