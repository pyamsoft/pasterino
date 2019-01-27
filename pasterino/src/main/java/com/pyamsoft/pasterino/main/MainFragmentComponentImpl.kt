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

package com.pyamsoft.pasterino.main

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.service.ServiceModule
import com.pyamsoft.pasterino.service.ServiceStateWorker
import com.pyamsoft.pasterino.settings.SettingsStateEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.loader.LoaderModule

internal class MainFragmentComponentImpl internal constructor(
  private val parent: ViewGroup,
  private val owner: LifecycleOwner,
  private val loaderModule: LoaderModule,
  private val serviceModule: ServiceModule,
  private val actionViewBus: EventBus<ActionViewEvent>,
  private val settingsStateBus: EventBus<SettingsStateEvent>
) : MainFragmentComponent {

  override fun inject(fragment: MainFragment) {
    val actionView = MainActionView(loaderModule.provideImageLoader(), owner, parent, actionViewBus)
    val frame = MainFrameView(parent)
    val serviceStateWorker = ServiceStateWorker(serviceModule.interactor)

    fragment.frameComponent = MainFrameUiComponent(frame, owner)
    fragment.actionComponent = MainActionUiComponent(
        settingsStateBus, serviceStateWorker, actionView, owner
    )
  }

}
