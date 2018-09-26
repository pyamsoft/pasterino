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
import com.pyamsoft.pasterino.service.PasteViewModel
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.ui.app.fragment.ToolbarFragment
import com.pyamsoft.pydroid.ui.app.fragment.requireToolbarActivity
import com.pyamsoft.pydroid.ui.util.setOnDebouncedClickListener
import com.pyamsoft.pydroid.ui.util.setUpEnabled
import com.pyamsoft.pydroid.ui.util.show

class MainFragment : ToolbarFragment() {

  internal lateinit var imageLoader: ImageLoader
  private lateinit var binding: FragmentMainBinding
  internal lateinit var pasteViewModel: PasteViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .plusMainComponent(viewLifecycleOwner)
        .inject(this)

    binding = FragmentMainBinding.inflate(inflater, container, false)
    displayPreferenceFragment()

    pasteViewModel.onServiceStateChanged {
      setupFAB(it)
    }

    return binding.root
  }

  private fun setupFAB(running: Boolean) {
    binding.mainSettingsFab.setOnDebouncedClickListener {
      if (running) {
        ServiceInfoDialog().show(requireActivity(), "service_info")
      } else {
        AccessibilityRequestDialog().show(requireActivity(), "accessibility")
      }
    }

    imageLoader.apply {
      if (running) {
        load(R.drawable.ic_help_24dp).into(binding.mainSettingsFab)
            .bind(viewLifecycleOwner)
      } else {
        load(R.drawable.ic_service_start_24dp).into(binding.mainSettingsFab)
            .bind(viewLifecycleOwner)
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
    requireToolbarActivity().withToolbar {
      it.setTitle(R.string.app_name)
      it.setUpEnabled(false)
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
