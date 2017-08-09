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

package com.pyamsoft.pasterino.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pasterino.Injector
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

  private lateinit var binding: FragmentMainBinding
  private val drawableMap = LoaderMap()
  internal lateinit var presenter: MainPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Injector.with(context) {
      it.inject(this)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupFAB()
    displayPreferenceFragment()
  }

  private fun setupFAB() {
    FABUtil.setupFABBehavior(binding.mainSettingsFab, HideScrollFABBehavior(10))
  }

  override fun onStart() {
    super.onStart()
    presenter.start(Unit)
    presenter.clickEvent(binding.mainSettingsFab, {
      if (PasteService.isRunning) {
        DialogUtil.guaranteeSingleDialogFragment(activity, ServiceInfoDialog(),
            "servce_info")
      } else {
        DialogUtil.guaranteeSingleDialogFragment(activity, AccessibilityRequestDialog(),
            "accessibility")
      }
    })
  }

  override fun onStop() {
    super.onStop()
    presenter.stop()
  }

  override fun onResume() {
    super.onResume()
    setActionBarUpEnabled(false)

    if (PasteService.isRunning) {
      val task = ImageLoader.fromResource(activity, R.drawable.ic_help_24dp)
          .into(binding.mainSettingsFab)
      drawableMap.put("fab", task)
    } else {
      val task = ImageLoader.fromResource(activity, R.drawable.ic_service_start_24dp)
          .into(binding.mainSettingsFab)
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
