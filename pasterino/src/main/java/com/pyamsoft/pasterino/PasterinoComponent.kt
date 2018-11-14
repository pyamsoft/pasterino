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

import androidx.annotation.CheckResult
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.main.ConfirmationDialog
import com.pyamsoft.pasterino.main.MainActivity
import com.pyamsoft.pasterino.main.MainComponent
import com.pyamsoft.pasterino.service.ServiceComponent

interface PasterinoComponent {

  fun inject(mainActivity: MainActivity)

  fun inject(dialog: ConfirmationDialog)

  @CheckResult
  fun plusMainComponent(owner: LifecycleOwner): MainComponent

  @CheckResult
  fun plusServiceComponent(owner: LifecycleOwner): ServiceComponent
}
