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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.service.PasteServiceModule
import com.pyamsoft.pydroid.loader.LoaderModule

internal class MainFragmentComponentImpl internal constructor(
  private val owner: LifecycleOwner,
  private val inflater: LayoutInflater,
  private val container: ViewGroup?,
  private val loaderModule: LoaderModule,
  private val serviceModule: PasteServiceModule,
  private val mainModule: MainModule
) : MainFragmentComponent {

  override fun inject(fragment: MainFragment) {
    fragment.pasteViewModel = serviceModule.getViewModel()
    fragment.mainViewModel = mainModule.getFragmentViewModel()
    fragment.mainView = MainFragmentViewImpl(
        owner, inflater,
        container, loaderModule.provideImageLoader()
    )
  }

}
