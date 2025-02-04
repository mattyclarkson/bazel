// Copyright 2021 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.lib.skyframe;

import com.google.devtools.build.lib.actions.ActionLookupKey;
import com.google.devtools.build.lib.analysis.TopLevelArtifactContext;
import com.google.devtools.build.skyframe.SkyFunctionName;
import com.google.devtools.build.skyframe.SkyKey;
import java.util.Objects;

/**
 * Wraps an {@link ActionLookupKey}. The evaluation of this SkyKey is the entry point of analyzing
 * the {@link ActionLookupKey} and executing the associated actions.
 */
public final class BuildDriverKey implements SkyKey {
  private final ActionLookupKey actionLookupKey;
  private final TopLevelArtifactContext topLevelArtifactContext;
  private final TestType testType;
  private final boolean strictActionConflictCheck;

  public boolean isTopLevelAspectDriver() {
    return isTopLevelAspectDriver;
  }

  private final boolean isTopLevelAspectDriver;

  private BuildDriverKey(
      ActionLookupKey actionLookupKey,
      TopLevelArtifactContext topLevelArtifactContext,
      boolean strictActionConflictCheck,
      TestType testType,
      boolean isTopLevelAspectDriver) {
    this.actionLookupKey = actionLookupKey;
    this.topLevelArtifactContext = topLevelArtifactContext;
    this.strictActionConflictCheck = strictActionConflictCheck;
    this.testType = testType;
    this.isTopLevelAspectDriver = isTopLevelAspectDriver;
  }

  public static BuildDriverKey ofTopLevelAspect(
      ActionLookupKey actionLookupKey,
      TopLevelArtifactContext topLevelArtifactContext,
      boolean strictActionConflictCheck) {
    return new BuildDriverKey(
        actionLookupKey,
        topLevelArtifactContext,
        strictActionConflictCheck,
        TestType.NOT_TEST,
        /*isTopLevelAspectDriver=*/ true);
  }

  public static BuildDriverKey ofConfiguredTarget(
      ActionLookupKey actionLookupKey,
      TopLevelArtifactContext topLevelArtifactContext,
      boolean strictActionConflictCheck,
      TestType testType) {
    return new BuildDriverKey(
        actionLookupKey,
        topLevelArtifactContext,
        strictActionConflictCheck,
        testType,
        /*isTopLevelAspectDriver=*/ false);
  }

  public TopLevelArtifactContext getTopLevelArtifactContext() {
    return topLevelArtifactContext;
  }

  public ActionLookupKey getActionLookupKey() {
    return actionLookupKey;
  }

  public boolean isTest() {
    return TestType.NOT_TEST.equals(testType);
  }

  public TestType getTestType() {
    return testType;
  }

  public boolean strictActionConflictCheck() {
    return strictActionConflictCheck;
  }

  @Override
  public SkyFunctionName functionName() {
    return SkyFunctions.BUILD_DRIVER;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof BuildDriverKey) {
      BuildDriverKey otherBuildDriverKey = (BuildDriverKey) other;
      return actionLookupKey.equals(otherBuildDriverKey.actionLookupKey)
          && topLevelArtifactContext.equals(otherBuildDriverKey.topLevelArtifactContext);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(actionLookupKey, topLevelArtifactContext);
  }

  @Override
  public final String toString() {
    return String.format("ActionLookupKey: %s; TestType: %s", actionLookupKey, testType);
  }

  enum TestType {
    NOT_TEST,
    PARALLEL,
    EXCLUSIVE
  }
}
