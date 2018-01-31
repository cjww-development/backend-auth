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

import sbt._

object LibraryDependencies {
  def apply(): Seq[ModuleID] = CompileDependencies() ++ TestDependencies()
}

private object CompileDependencies {
  private val httpVerbsVersion = "2.14.0"
  private val appUtilsVersion  = "2.14.0"
  private val playVersion      = "2.5.16"

  private val compileDependencies: Seq[ModuleID] = Seq(
    "com.cjww-dev.libs" %% "http-verbs"            % httpVerbsVersion,
    "com.cjww-dev.libs" %% "application-utilities" % appUtilsVersion,
    "com.typesafe.play" %  "play_2.11"             % playVersion
  )

  def apply(): Seq[ModuleID] = compileDependencies
}

private object TestDependencies {
  private val scalaTestVersion = "2.0.1"
  private val mockitoVersion   = "2.13.0"

  private val testDependencies: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" % "scalatestplus-play_2.11" % scalaTestVersion  % Test,
    "org.mockito"            % "mockito-core"            % mockitoVersion    % Test
  )

  def apply(): Seq[ModuleID] = testDependencies
}
