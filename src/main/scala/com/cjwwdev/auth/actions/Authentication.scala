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
import play.api.Logger
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.Forbidden

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Authentication extends BaseAuth {

  val authConnector: AuthConnector

  protected def authenticated(id: String)(f: => Future[Result])(implicit request: Request[_]): Future[Result] = {
    authConnector.getContext flatMap { context =>
      mapToAuthResult(id, context) match {
        case Authenticated  => f
        case _              => Future.successful(Forbidden)
      }
    }
  }

  private def mapToAuthResult(id: String, context: Option[AuthContext])(implicit request: Request[_]): AuthorisationResult = {
    checkAppId match {
      case Authenticated => context match {
        case Some(_) =>
          Logger.info(s"[Authorisation] - [mapToAuthResult]: User authorised as $id")
          Authenticated
        case None =>
          Logger.warn("[Authorisation] - [mapToAuthResult]: User not authorised action deemed forbidden")
          NotAuthorised
      }
      case _ => NotAuthorised
    }
  }
}
