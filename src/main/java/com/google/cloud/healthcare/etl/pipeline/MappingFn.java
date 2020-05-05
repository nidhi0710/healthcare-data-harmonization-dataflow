// Copyright 2020 Google LLC.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.healthcare.etl.pipeline;

import com.google.cloud.healthcare.etl.util.library.TransformWrapper;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The core function of the mapping pipeline. Inputs are expected to be a list in order to guarantee
 * relative order between individual source. At this moment, only higher level language (whistle) is
 * supported.
 */
public class MappingFn extends ErrorEnabledDoFn<List<String>, List<String>> {

  private final String mappingConfig;
  private TransformWrapper engine;

  // The config parameter should be the string representation of the whole mapping config, including
  // harmonization and libraries.
  public MappingFn(String config) {
    this.mappingConfig = config;
  }

  @Setup
  public void initialize() {
    engine = TransformWrapper.getInstance();
    engine.initializeWhistler(mappingConfig);
  }

  @Override
  public List<String> process(List<String> strings) {
    return strings.parallelStream().map(i -> engine.transform(i)).collect(Collectors.toList());
  }
}
