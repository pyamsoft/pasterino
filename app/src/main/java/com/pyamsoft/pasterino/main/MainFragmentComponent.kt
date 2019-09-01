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
import androidx.annotation.CheckResult
import androidx.lifecycle.ViewModelProvider
import com.pyamsoft.pasterino.PasterinoViewModelFactory
import com.pyamsoft.pasterino.ViewModelKey
import com.pyamsoft.pasterino.main.MainFragmentComponent.ViewModelModule
import com.pyamsoft.pydroid.arch.UiViewModel
import com.pyamsoft.pydroid.ui.app.ToolbarActivity
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Subcomponent(modules = [ViewModelModule::class])
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
    abstract class ViewModelModule {

        @Binds
        internal abstract fun bindViewModelFactory(factory: PasterinoViewModelFactory): ViewModelProvider.Factory

        @Binds
        @IntoMap
        @ViewModelKey(MainViewModel::class)
        internal abstract fun mainViewModel(viewModel: MainViewModel): UiViewModel<*, *, *>
    }
}
