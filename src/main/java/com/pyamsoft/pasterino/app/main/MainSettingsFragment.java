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

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.app.notification.PasteServiceNotification;
import com.pyamsoft.pydroid.about.AboutLibrariesFragment;
import com.pyamsoft.pydroid.app.fragment.ActionBarSettingsPreferenceFragment;
import com.pyamsoft.pydroid.base.PersistLoader;
import com.pyamsoft.pydroid.model.Licenses;
import com.pyamsoft.pydroid.util.AppUtil;
import com.pyamsoft.pydroid.util.PersistentCache;
import timber.log.Timber;

public class MainSettingsFragment extends ActionBarSettingsPreferenceFragment
    implements MainSettingsPresenter.MainSettingsView {

  @NonNull public static final String TAG = "MainSettingsFragment";
  @NonNull private static final String KEY_PRESENTER = "key_main_presenter";
  MainSettingsPresenter presenter;
  private long loadedKey;

  @NonNull @Override protected AboutLibrariesFragment.BackStackState isLastOnBackStack() {
    return AboutLibrariesFragment.BackStackState.LAST;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    loadedKey = PersistentCache.load(KEY_PRESENTER, savedInstanceState,
        new PersistLoader.Callback<MainSettingsPresenter>() {
          @NonNull @Override public PersistLoader<MainSettingsPresenter> createLoader() {
            return new MainSettingsPresenterLoader(getContext());
          }

          @Override public void onPersistentLoaded(@NonNull MainSettingsPresenter persist) {
            presenter = persist;
          }
        });
  }

  @Override public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

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

    final Preference showAboutLicenses = findPreference(getString(R.string.about_license_key));
    showAboutLicenses.setOnPreferenceClickListener(
        preference -> showAboutLicensesFragment(R.id.main_container,
            AboutLibrariesFragment.Styling.LIGHT, Licenses.ANDROID, Licenses.ANDROID_SUPPORT,
            Licenses.PYDROID, Licenses.GOOGLE_PLAY_SERVICES, Licenses.ANDROID_IN_APP_BILLING,
            Licenses.AUTO_VALUE, Licenses.BUTTERKNIFE, Licenses.DAGGER, Licenses.FAST_ADAPTER,
            Licenses.FIREBASE, Licenses.LEAK_CANARY, Licenses.RETROFIT2, Licenses.RXANDROID,
            Licenses.RXJAVA));

    final Preference checkVersion = findPreference(getString(R.string.check_version_key));
    checkVersion.setOnPreferenceClickListener(preference -> checkForUpdate());
  }

  @Override public void onStart() {
    super.onStart();
    presenter.bindView(this);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.unbindView();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (!getActivity().isChangingConfigurations()) {
      PersistentCache.unload(loadedKey);
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    PersistentCache.saveKey(outState, KEY_PRESENTER, loadedKey);
    super.onSaveInstanceState(outState);
  }

  @Override public void showConfirmDialog() {
    AppUtil.guaranteeSingleDialogFragment(getFragmentManager(), new ConfirmationDialog(),
        "confirm");
  }

  @Override public void onClearAll() {
    PasteServiceNotification.stop(getContext());
    final ActivityManager activityManager = (ActivityManager) getContext().getApplicationContext()
        .getSystemService(Context.ACTIVITY_SERVICE);
    activityManager.clearApplicationUserData();
    android.os.Process.killProcess(android.os.Process.myPid());
  }
}
