/*
 *     Copyright (C) 2017 Peter Kenji Yamanaka
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.pyamsoft.pasterino.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.databinding.FragmentMainBinding
import com.pyamsoft.pasterino.service.PasteService
import com.pyamsoft.pasterino.uicore.CanaryFragment
import com.pyamsoft.pydroid.design.fab.HideScrollFABBehavior
import com.pyamsoft.pydroid.design.util.FABUtil
import com.pyamsoft.pydroid.loader.ImageLoader
import com.pyamsoft.pydroid.loader.LoaderMap
import com.pyamsoft.pydroid.ui.util.DialogUtil

class MainSettingsFragment : CanaryFragment() {

  internal lateinit var imageLoader: ImageLoader
  private lateinit var binding: FragmentMainBinding
  private val drawableMap = LoaderMap()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupFAB()
    displayPreferenceFragment()
  }

  private fun setupFAB() {
    FABUtil.setupFABBehavior(binding.mainSettingsFab, HideScrollFABBehavior(10))
    binding.mainSettingsFab.setOnClickListener {
      if (PasteService.isRunning) {
        DialogUtil.guaranteeSingleDialogFragment(activity, ServiceInfoDialog(),
            "servce_info")
      } else {
        DialogUtil.guaranteeSingleDialogFragment(activity, AccessibilityRequestDialog(),
            "accessibility")
      }
    }
  }

  override fun onResume() {
    super.onResume()
    setActionBarUpEnabled(false)
    setActionBarTitle(R.string.app_name)

    if (PasteService.isRunning) {
      val task = imageLoader.fromResource(R.drawable.ic_help_24dp).into(binding.mainSettingsFab)
      drawableMap.put("fab", task)
    } else {
      val task = imageLoader.fromResource(R.drawable.ic_service_start_24dp).into(
          binding.mainSettingsFab)
      drawableMap.put("fab", task)
    }
  }

  override fun onPause() {
    super.onPause()
    drawableMap.clear()
  }

  private fun displayPreferenceFragment() {
    val fragmentManager = childFragmentManager
    if (fragmentManager.findFragmentByTag(MainSettingsPreferenceFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .replace(R.id.fragment_container, MainSettingsPreferenceFragment(),
              MainSettingsPreferenceFragment.TAG)
          .commit()
    }
  }

  companion object {

    const val TAG = "MainSettingsFragment"
  }
}
