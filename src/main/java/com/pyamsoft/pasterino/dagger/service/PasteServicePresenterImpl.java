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
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.pyamsoft.pasterino.app.service.PasteServicePresenter;
import com.pyamsoft.pydroid.base.PresenterImpl;
import javax.inject.Inject;
import timber.log.Timber;

final class PasteServicePresenterImpl
    extends PresenterImpl<PasteServicePresenter.PasteServiceProvider>
    implements PasteServicePresenter {

  @NonNull private final PasteServiceInteractor interactor;

  @Inject PasteServicePresenterImpl(@NonNull PasteServiceInteractor interactor) {
    this.interactor = interactor;
  }

  @Override
  public void storeEditableViewForPasting(@Nullable AccessibilityNodeInfoCompat potentialTarget) {
    Timber.d("Store target if it is editable...");
    if (potentialTarget != null && potentialTarget.isEditable()) {
      interactor.storePasteView(potentialTarget);
    } else {
      Timber.e("Target is not editable, ignore");
      interactor.clearPasteView();
    }
  }

  @Override public void pasteClipboardIntoFocusedView() {
    final AccessibilityNodeInfoCompat target = interactor.getPasteView();
    if (target != null) {
      Timber.d("Got valid paste target, attempt paste");
      getView().onPaste(target);
    } else {
      Timber.e("No valid paste target exists");
    }
  }
}
