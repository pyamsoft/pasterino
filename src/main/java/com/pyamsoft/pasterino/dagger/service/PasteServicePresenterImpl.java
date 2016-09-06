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

import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityNodeInfo;
import com.pyamsoft.pasterino.app.service.PasteServicePresenter;
import com.pyamsoft.pydroid.dagger.presenter.PresenterBase;
import javax.inject.Inject;
import timber.log.Timber;

class PasteServicePresenterImpl extends PresenterBase<PasteServicePresenter.PasteServiceProvider>
    implements PasteServicePresenter {

  @Inject PasteServicePresenterImpl() {
  }

  @Override public void pasteClipboardIntoFocusedView(@Nullable AccessibilityNodeInfo target) {
    if (target != null && target.isEditable()) {
      Timber.d("Got valid paste target, attempt paste");
      getView().onPaste(target);
    } else {
      Timber.e("No valid paste target exists");
    }
  }
}