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

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.Singleton;
import com.pyamsoft.pasterino.app.notification.PasteServiceNotification;
import com.pyamsoft.pasterino.dagger.main.MainSettingsPresenter;
import com.pyamsoft.pydroid.base.fragment.ActionBarSettingsPreferenceFragment;
import com.pyamsoft.pydroid.util.AppUtil;
import javax.inject.Inject;
import timber.log.Timber;

public final class MainSettingsFragment extends ActionBarSettingsPreferenceFragment
    implements MainSettingsPresenter.MainSettingsView {

  @Inject MainSettingsPresenter presenter;

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    Singleton.Dagger.with(getContext()).plusMainSettingsComponent().inject(this);
    addPreferencesFromResource(R.xml.preferences);
    presenter.bindView(this);

    final Preference explain = findPreference(getString(R.string.explain_key));
    explain.setOnPreferenceClickListener(preference -> {
      AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new HowToDialog(), "howto");
      return true;
    });

    final Preference resetAll = findPreference(getString(R.string.clear_all_key));
    resetAll.setOnPreferenceClickListener(preference -> {
      Timber.d("Reset settings onClick");
      assert presenter != null;
      presenter.clearAll();
      return true;
    });

    final Preference upgradeInfo = findPreference(getString(R.string.upgrade_info_key));
    upgradeInfo.setOnPreferenceClickListener(preference -> showChangelog());

    final SwitchPreferenceCompat showAds =
        (SwitchPreferenceCompat) findPreference(getString(R.string.adview_key));
    showAds.setOnPreferenceChangeListener((preference, newValue) -> toggleAdVisibility(newValue));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    presenter.unbindView();
  }

  @Override public void onResume() {
    super.onResume();
    presenter.resume();
  }

  @Override public void onPause() {
    super.onPause();
    presenter.pause();
  }

  @Override public void showConfirmDialog() {
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new ConfirmationDialog(),
        "confirm");
  }

  @Override public void onClearAll() {
    PasteServiceNotification.stop(getContext());
    android.os.Process.killProcess(android.os.Process.myPid());
  }
}
