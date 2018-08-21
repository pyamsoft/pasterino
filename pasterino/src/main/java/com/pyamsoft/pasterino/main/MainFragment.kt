/*
 * Copyright (C) 2018 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pasterino.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.databinding.FragmentMainBinding
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarFragment
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled
import com.pyamsoft.pydroid.ui.util.show

class MainFragment : ToolbarFragment() {

  internal lateinit var imageLoader: ImageLoader
  private lateinit var binding: FragmentMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentMainBinding.inflate(inflater, container, false)
    setupFAB()
    displayPreferenceFragment()
    return binding.root
  }

  private fun setupFAB() {
    binding.mainSettingsFab.setOnDebouncedClickListener {
      if (PasteService.isRunning) {
        ServiceInfoDialog().show(requireActivity(), "service_info")
      } else {
        AccessibilityRequestDialog().show(requireActivity(), "accessibility")
      }
    }
  }

  // Used by MainSettingsPreferenceFragment
  @CheckResult
  internal fun getFloatingActionButton(): FloatingActionButton {
    return binding.mainSettingsFab
  }

  override fun onResume() {
    super.onResume()
    toolbarActivity.withToolbar {
      it.setTitle(R.string.app_name)
      it.setUpEnabled(false)
    }

    imageLoader.apply {
      if (PasteService.isRunning) {
        fromResource(R.drawable.ic_help_24dp).into(binding.mainSettingsFab)
            .bind(viewLifecycleOwner)
      } else {
        fromResource(R.drawable.ic_service_start_24dp).into(binding.mainSettingsFab)
            .bind(viewLifecycleOwner)
      }
    }
  }

  private fun displayPreferenceFragment() {
    val fragmentManager = childFragmentManager
    if (fragmentManager.findFragmentByTag(MainSettingsFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .add(
              R.id.fragment_container, MainSettingsFragment(),
              MainSettingsFragment.TAG
          )
          .commit()
    }
  }

  companion object {

    const val TAG = "MainFragment"
  }
}
