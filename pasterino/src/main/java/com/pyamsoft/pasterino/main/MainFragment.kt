/*
 * Copyright 2019 Peter Kenji Yamanaka
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
 *
 */

package com.pyamsoft.pasterino.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.settings.MainSettingsFragment
import com.pyamsoft.pasterino.widget.ToolbarView
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.show

class MainFragment : Fragment(), MainFragmentUiComponent.Callback {

  internal lateinit var toolbar: ToolbarView
  internal lateinit var component: MainFragmentUiComponent

  private var layoutRoot: CoordinatorLayout? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.layout_coordinator, container, false)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    layoutRoot = view.findViewById(R.id.layout_coordinator)
    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .plusMainFragmentComponent(requireNotNull(layoutRoot))
        .inject(this)

    toolbar.inflate(savedInstanceState)
    component.bind(viewLifecycleOwner, savedInstanceState, this)

    displayPreferenceFragment()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    toolbar.saveState(outState)
    component.saveState(outState)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    toolbar.teardown()
    layoutRoot = null
  }

  override fun onShowServiceInfo() {
    ServiceInfoDialog()
        .show(requireActivity(), "service_info")
  }

  override fun onShowPermissionDialog() {
    AccessibilityRequestDialog()
        .show(requireActivity(), "accessibility")
  }

  private fun displayPreferenceFragment() {
    val fragmentManager = childFragmentManager
    if (fragmentManager.findFragmentByTag(MainSettingsFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .add(requireNotNull(layoutRoot).id, MainSettingsFragment(), MainSettingsFragment.TAG)
          .commit(viewLifecycleOwner)
    }
  }

  companion object {

    const val TAG = "MainFragment"
  }
}

