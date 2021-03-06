/*
 * Copyright 2020 Peter Kenji Yamanaka
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

package com.pyamsoft.pasterino.main

import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.lifecycle.ViewModel
import com.pyamsoft.pasterino.ViewModelFactoryModule
import com.pyamsoft.pydroid.ui.app.ToolbarActivity
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Subcomponent(
    modules = [MainFragmentComponent.ComponentModule::class, ViewModelFactoryModule::class])
interface MainFragmentComponent {

  fun inject(fragment: MainFragment)

  @Subcomponent.Factory
  interface Factory {

    @CheckResult
    fun create(
        @BindsInstance toolbarActivity: ToolbarActivity,
        @BindsInstance parent: ViewGroup
    ): MainFragmentComponent
  }

  @Module
  abstract class ComponentModule {
    @Binds
    @IntoMap
    @ClassKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel
  }
}
