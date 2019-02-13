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
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationEvent
import com.pyamsoft.pydroid.ui.navigation.FailedNavigationPresenterImpl
import com.pyamsoft.pydroid.ui.widget.shadow.DropshadowView

internal class MainComponentImpl internal constructor(
  private val parent: ViewGroup,
  private val owner: LifecycleOwner,
  private val failedBus: EventBus<FailedNavigationEvent>
) : MainComponent {

  override fun inject(activity: MainActivity) {
    val dropshadowView = DropshadowView(parent)
    val mainFrame = MainFrameView(parent)
    val mainPresenter = MainPresenterImpl(owner)
    val toolbarView = MainToolbarView(activity, parent, mainPresenter)

    activity.apply {
      this.dropshadow = dropshadowView
      this.failedNavigationPresenter = FailedNavigationPresenterImpl(owner, failedBus)
      this.frameView = mainFrame
      this.presenter = mainPresenter
      this.toolbar = toolbarView
    }
  }
}

