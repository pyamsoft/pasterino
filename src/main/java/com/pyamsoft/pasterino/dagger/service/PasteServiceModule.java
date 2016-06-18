/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pasterino.dagger.service;

import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.app.service.PasteServicePresenter;
import com.pyamsoft.pasterino.app.service.SinglePastePresenter;
import com.pyamsoft.pasterino.dagger.ActivityScope;
import dagger.Module;
import dagger.Provides;

@Module public class PasteServiceModule {

  @ActivityScope @Provides PasteServicePresenter providePasteServicePresenter(
      @NonNull PasteServicePresenterImpl presenter) {
    return presenter;
  }

  @ActivityScope @Provides SinglePastePresenter provideSinglePastePresenter(
      @NonNull SinglePastePresenterImpl presenter) {
    return presenter;
  }

  @ActivityScope @Provides PasteServiceInteractor providePasteServiceInteractor(
      @NonNull PasteServiceInteractorImpl interactor) {
    return interactor;
  }
}
