/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pasterino

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceScreen
import com.pyamsoft.pasterino.main.ConfirmationDialog
import com.pyamsoft.pasterino.main.MainActivity
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.main.MainFragmentComponent
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.service.ServiceComponent
import com.pyamsoft.pasterino.service.SinglePasteService

interface PasterinoComponent {

  fun inject(activity: MainActivity)

  fun inject(dialog: ConfirmationDialog)

  fun inject(service: PasteService)

  fun inject(service: SinglePasteService)

  @CheckResult
  fun plusMainComponent(
    owner: LifecycleOwner,
    preferenceScreen: PreferenceScreen,
    tag: String
  ): MainComponent

  @CheckResult
  fun plusMainFragmentComponent(
    owner: LifecycleOwner,
    inflater: LayoutInflater,
    container: ViewGroup?
  ): MainFragmentComponent

  @CheckResult
  fun plusServiceComponent(owner: LifecycleOwner): ServiceComponent
}
