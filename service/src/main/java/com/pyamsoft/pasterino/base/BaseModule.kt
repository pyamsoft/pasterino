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

package com.pyamsoft.pasterino.base

import androidx.annotation.CheckResult
import com.pyamsoft.pasterino.api.ClearPreferences
import com.pyamsoft.pasterino.api.MainInteractor
import com.pyamsoft.pasterino.api.PastePreferences
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import dagger.Binds
import dagger.Module

@Module
abstract class BaseModule {

  @Binds
  @CheckResult
  internal abstract fun bindServiceInteractor(
      impl: PasteServiceInteractorImpl
  ): PasteServiceInteractor

  @Binds
  @CheckResult
  internal abstract fun bindMainInteractor(impl: MainInteractorImpl): MainInteractor

  @Binds
  @CheckResult
  internal abstract fun bindPastePreferences(impl: PasterinoPreferencesImpl): PastePreferences

  @Binds
  @CheckResult
  internal abstract fun bindClearPreferences(impl: PasterinoPreferencesImpl): ClearPreferences
}
