/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.artifact.resolver.filter

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.resolver.filter.ExclusionArtifactFilterScalaTest._
import org.apache.maven.model.Exclusion
import org.mockito.Mockito.{mock, when}
import org.scalatest.funsuite.AnyFunSuite

import java.util

final class ExclusionArtifactFilterScalaTest extends AnyFunSuite {

  test("Directly excluded one excludes") {
    assert(filter("org.apache.maven" -> "maven-core").excludes(MavenCore))
  }
  test("Not matching artifactId is not excluded") {
    assert(filter("org.apache.maven" -> "maven-model").includes(MavenCore))
  }
  test("Wildcard-excluded GroupID excludes") {
    assert(filter("*" -> "maven-core").excludes(MavenCore))
  }
  test("Wildcard, but on a different artifact, does not exclude") {
    assert(filter("*" -> "maven-compat").includes(MavenCore))
  }
  test("Wildcard on the artifact, with the right groupId, excludes") {
    assert(filter("org.apache.maven" -> "*").excludes(MavenCore))
  }
  test("Wildcard on the artifact, with the wrong groupId, includes") {
    assert(filter("org.apache.groovy" -> "*").includes(MavenCore))
  }
  test("All wildcards excludes") {
    assert(filter("*" -> "*").excludes(MavenCore))
  }
  test("Multiple wildcards excludes") {
    assert(filter("org.apache.groovy" -> "*", "org.apache.maven" -> "maven-core").excludes(MavenCore))
  }
  test("Multiple wildcards excludes (2)") {
    assert(filter("*" -> "maven-model", "org.apache.maven" -> "maven-core").excludes(MavenCore))
  }

}

object ExclusionArtifactFilterScalaTest {
  val MavenCore: Artifact = mock(classOf[Artifact])
  when(MavenCore.getGroupId).thenReturn("org.apache.maven")
  when(MavenCore.getArtifactId).thenReturn("maven-core")

  private def filter(exclusions: (String, String)*): ExclusionArtifactFilter = {
    new ExclusionArtifactFilter(util.Arrays.asList(exclusions.map { case (groupId, artifactId) =>
      val exclusion = new Exclusion
      exclusion.setGroupId(groupId)
      exclusion.setArtifactId(artifactId)
      exclusion
    }: _*))
  }

  extension (filter: ExclusionArtifactFilter) {
    def excludes(artifact: Artifact): Boolean = !filter.include(artifact)
    def includes(artifact: Artifact): Boolean = filter.include(artifact)
  }

}