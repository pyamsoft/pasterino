/*
 * Copyright 2016 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pasterino;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.base.PasterinoModule;
import com.pyamsoft.pasterino.main.MainComponent;
import com.pyamsoft.pasterino.main.MainSettingsModule;
import com.pyamsoft.pasterino.service.PasteComponent;
import com.pyamsoft.pasterino.service.PasteServiceModule;

public class PasterinoComponent {

  @NonNull private final MainComponent mainComponent;
  @NonNull private final PasteComponent pasteComponent;

  private PasterinoComponent(@NonNull PasterinoModule module) {
    MainSettingsModule mainSettingsModule = new MainSettingsModule(module);
    PasteServiceModule pasteServiceModule = new PasteServiceModule(module);
    mainComponent = new MainComponent(mainSettingsModule);
    pasteComponent = new PasteComponent(pasteServiceModule);
  }

  @CheckResult @NonNull static PasterinoComponent withModule(@NonNull PasterinoModule module) {
    return new PasterinoComponent(module);
  }

  @CheckResult @NonNull public MainComponent plusMainComponent() {
    return mainComponent;
  }

  @CheckResult @NonNull public PasteComponent plusPasteComponent() {
    return pasteComponent;
  }
}
