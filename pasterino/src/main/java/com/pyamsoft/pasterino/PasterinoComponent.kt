/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pasterino

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.main.MainFragmentComponent
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pasterino.settings.ConfirmationDialog
import com.pyamsoft.pasterino.settings.SettingsComponent

interface PasterinoComponent {

  fun inject(dialog: ConfirmationDialog)

  fun inject(service: PasteService)

  fun inject(service: SinglePasteService)

  @CheckResult
  fun plusMainComponent(parent: ConstraintLayout): MainComponent

  @CheckResult
  fun plusMainFragmentComponent(parent: ViewGroup): MainFragmentComponent

  @CheckResult
  fun plusSettingsComponent(
    recyclerView: RecyclerView,
    preferenceScreen: PreferenceScreen
  ): SettingsComponent
}
