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
import com.cjwwdev.logging.Logger
import play.api.mvc.{Request, Result}
import play.api.mvc.Results.Forbidden

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait Authorisation extends BaseAuth {

  val authConnector: AuthConnector

  protected def authorised(userId: String)(f: => Result)(implicit request: Request[_]): Future[Result] = {
    for {
      context <- authConnector.getContext
      result  = mapToAuthResult(userId, context)
    } yield result match {
      case Authorised    => f
      case NotAuthorised => Forbidden
    }
  }

  private def mapToAuthResult(userId: String, context: Option[AuthContext])(implicit request: Request[_]): AuthorisationResult = {
    checkAppId match {
      case Authorised => context match {
        case Some(authority) =>
          if(userId == authority.user.userId){
            Logger.info(s"[Authorisation] - [mapToAuthResult]: User authorised as ${authority.user.userId}")
            Authorised
          } else {
            Logger.warn("[Authorisation] - [mapToAuthResult]: User not authorised action deemed forbidden")
            NotAuthorised
          }
        case None =>
          Logger.warn("[Authorisation] - [mapToAuthResult]: User not authorised action deemed forbidden")
          NotAuthorised
      }
      case NotAuthorised => NotAuthorised
    }
  }
}
