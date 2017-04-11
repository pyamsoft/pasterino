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

package com.pyamsoft.pasterino.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pyamsoft.pasterino.Pasterino;
import com.pyamsoft.pasterino.R;
import com.pyamsoft.pasterino.databinding.FragmentMainBinding;
import com.pyamsoft.pasterino.service.PasteService;
import com.pyamsoft.pydroid.design.fab.HideScrollFABBehavior;
import com.pyamsoft.pydroid.design.util.FABUtil;
import com.pyamsoft.pydroid.ui.app.fragment.ActionBarFragment;
import com.pyamsoft.pydroid.ui.loader.DrawableLoader;
import com.pyamsoft.pydroid.ui.loader.DrawableMap;
import com.pyamsoft.pydroid.util.DialogUtil;

public class MainSettingsFragment extends ActionBarFragment {

  @NonNull public static final String TAG = "MainSettingsFragment";
  @NonNull private final DrawableMap drawableMap = new DrawableMap();
  private FragmentMainBinding binding;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentMainBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupFAB();
    displayPreferenceFragment();
  }

  private void setupFAB() {
    binding.mainSettingsFab.setOnClickListener(view -> {
      if (PasteService.isRunning()) {
        DialogUtil.guaranteeSingleDialogFragment(getActivity(), new ServiceInfoDialog(),
            "servce_info");
      } else {
        DialogUtil.guaranteeSingleDialogFragment(getActivity(), new AccessibilityRequestDialog(),
            "accessibility");
      }
    });
    FABUtil.setupFABBehavior(binding.mainSettingsFab, new HideScrollFABBehavior(10));
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    drawableMap.clear();
    binding.unbind();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Pasterino.getRefWatcher(this).watch(this);
  }

  @Override public void onResume() {
    super.onResume();
    setActionBarUpEnabled(false);

    if (PasteService.isRunning()) {
      final DrawableLoader.Loaded task =
          DrawableLoader.load(R.drawable.ic_help_24dp).into(binding.mainSettingsFab);
      drawableMap.put("fab", task);
    } else {
      final DrawableLoader.Loaded task =
          DrawableLoader.load(R.drawable.ic_service_start_24dp).into(binding.mainSettingsFab);
      drawableMap.put("fab", task);
    }
  }

  private void displayPreferenceFragment() {
    final FragmentManager fragmentManager = getChildFragmentManager();
    if (fragmentManager.findFragmentByTag(MainSettingsPreferenceFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.fragment_container, new MainSettingsPreferenceFragment(),
              MainSettingsPreferenceFragment.TAG)
          .commit();
    }
  }
}
