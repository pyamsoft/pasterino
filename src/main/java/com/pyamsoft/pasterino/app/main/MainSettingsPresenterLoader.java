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

package com.pyamsoft.pasterino.app.main;

import android.content.Context;
import android.support.annotation.NonNull;
import com.pyamsoft.pasterino.Singleton;
import com.pyamsoft.pydroid.base.PersistLoader;
import javax.inject.Inject;
import javax.inject.Provider;

public class MainSettingsPresenterLoader extends PersistLoader<MainSettingsPresenter> {

  @Inject Provider<MainSettingsPresenter> presenterProvider;

  MainSettingsPresenterLoader(@NonNull Context context) {
    super(context);
  }

  @NonNull @Override public MainSettingsPresenter loadPersistent() {
    Singleton.Dagger.with(getContext()).plusMainSettingsComponent().inject(this);
    return presenterProvider.get();
  }
}