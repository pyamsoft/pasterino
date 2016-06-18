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

package com.pyamsoft.pasterino.app.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import com.pyamsoft.pasterino.Pasterino;
import com.pyamsoft.pasterino.app.notification.PasteServiceNotification;
import com.pyamsoft.pasterino.dagger.service.DaggerPasteServiceComponent;
import javax.inject.Inject;
import timber.log.Timber;

public class PasteService extends AccessibilityService
    implements PasteServicePresenter.PasteServiceProvider {

  @Nullable private static volatile PasteService instance = null;
  @Nullable @Inject PasteServicePresenter presenter;

  @NonNull @CheckResult public static synchronized PasteService getInstance() {
    if (instance == null) {
      throw new NullPointerException("PasteService instance is NULL");
    } else {
      //noinspection ConstantConditions
      return instance;
    }
  }

  @CheckResult public static boolean isRunning() {
    return instance != null;
  }

  public static synchronized void setInstance(@Nullable PasteService instance) {
    PasteService.instance = instance;
  }

  public final void pasteIntoTarget() {
    Timber.d("Call pasteIntoTarget()");
    assert presenter != null;
    presenter.pasteClipboardIntoFocusedView();
  }

  @Override public void onAccessibilityEvent(AccessibilityEvent event) {
    Timber.d("New view related event");
    final AccessibilityNodeInfoCompat potentialTarget =
        AccessibilityEventCompat.asRecord(event).getSource();
    assert presenter != null;
    presenter.storeEditableViewForPasting(potentialTarget);
  }

  @Override public void onInterrupt() {
    Timber.e("onInterrupt");
  }

  @Override protected void onServiceConnected() {
    super.onServiceConnected();
    Timber.d("onServiceConnected");

    DaggerPasteServiceComponent.builder()
        .pasterinoComponent(Pasterino.getInstance().getPasterinoComponent())
        .build()
        .inject(this);

    assert presenter != null;
    presenter.bindView(this);

    PasteServiceNotification.start(this);

    setInstance(this);
  }

  @Override public boolean onUnbind(Intent intent) {
    Timber.d("onUnbind");
    assert presenter != null;
    presenter.unbindView();

    PasteServiceNotification.stop(this);

    setInstance(null);
    return super.onUnbind(intent);
  }

  @Override public void onPaste(@NonNull AccessibilityNodeInfoCompat target) {
    Timber.d("Perform paste on target: %s", target.getViewIdResourceName());
    target.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE);
    Toast.makeText(getApplicationContext(), "Pasting text into current input focus.",
        Toast.LENGTH_SHORT).show();
  }

  @Override public final void stopPasteService() {
    Timber.e("API 24 only");
  }
}
