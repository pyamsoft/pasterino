/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.pasterino.main;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.view.View;
import com.pyamsoft.pasterino.Injector;
import com.pyamsoft.pasterino.Pasterino;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.service.PasteService;
import com.pyamsoft.pasterino.service.PasteServiceNotification;
import com.pyamsoft.pasterino.service.SinglePasteService;
import com.pyamsoft.pydroid.ui.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarSettingsPreferenceFragment;
import com.pyamsoft.pydroid.util.DialogUtil;
import timber.log.Timber;

public class MainSettingsPreferenceFragment extends ActionBarSettingsPreferenceFragment {

  @NonNull public static final String TAG = "MainSettingsPreferenceFragment";
  @SuppressWarnings("WeakerAccess") MainSettingsPreferencePresenter presenter;

  @NonNull @Override protected AboutLibrariesFragment.BackStackState isLastOnBackStack() {
    return AboutLibrariesFragment.BackStackState.LAST;
  }

  @Override protected int getRootViewContainer() {
    return R.id.main_container;
  }

  @NonNull @Override protected String getApplicationName() {
    return getString(R.string.app_name);
  }

  @Override protected int getPreferenceXmlResId() {
    return R.xml.preferences;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Injector.get().provideComponent().plusMainComponent().inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final Preference explain = findPreference(getString(R.string.explain_key));
    explain.setOnPreferenceClickListener(preference -> {
      DialogUtil.guaranteeSingleDialogFragment(getActivity(), new HowToDialog(), "howto");
      return true;
    });
  }

  @Override public void onStart() {
    super.onStart();
    presenter.registerOnEventBus(() -> {
      PasteServiceNotification.stop(getContext());
      SinglePasteService.stop(getContext());
      try {
        PasteService.finish();
      } catch (NullPointerException e) {
        Timber.e(e, "Expected exception when Service is NULL");
      }

      Timber.d("Clear application data");
      final ActivityManager activityManager = (ActivityManager) getContext().getApplicationContext()
          .getSystemService(Context.ACTIVITY_SERVICE);
      activityManager.clearApplicationUserData();
    });

  }

  @Override public void onStop() {
    super.onStop();
    presenter.stop();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    presenter.destroy();
    Pasterino.getRefWatcher(this).watch(this);
  }

  @Override protected void onClearAllClicked() {
    DialogUtil.guaranteeSingleDialogFragment(getActivity(), new ConfirmationDialog(), "confirm");
  }
}
