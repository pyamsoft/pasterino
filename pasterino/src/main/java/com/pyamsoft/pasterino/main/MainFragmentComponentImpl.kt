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
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pasterino.service.ServiceStatePresenterImpl
import com.pyamsoft.pasterino.settings.SignificantScrollEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.loader.ImageLoader

internal class MainFragmentComponentImpl internal constructor(
  private val parent: ViewGroup,
  private val imageLoader: ImageLoader,
  private val interactor: PasteServiceInteractor,
  private val scrollBus: EventBus<SignificantScrollEvent>
) : MainFragmentComponent {

  override fun inject(fragment: MainFragment) {
    val mainPresenter = MainFragmentPresenterImpl(scrollBus)

    fragment.apply {
      this.actionView = MainActionView(imageLoader, parent, mainPresenter)
      this.frameView = MainFrameView(parent)
      this.presenter = mainPresenter
      this.serviceStatePresenter = ServiceStatePresenterImpl(interactor)
    }
  }

}


