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
import com.pyamsoft.pasterino.service.ServiceStatePresenter
import com.pyamsoft.pasterino.settings.MainSettingsFragment
import com.pyamsoft.pasterino.widget.ToolbarView
import com.pyamsoft.pydroid.ui.util.commit
import com.pyamsoft.pydroid.ui.util.show

class MainFragment : Fragment(), ServiceStatePresenter.Callback, MainFragmentPresenter.Callback {

  internal lateinit var presenter: MainFragmentPresenter
  internal lateinit var serviceStatePresenter: ServiceStatePresenter

  internal lateinit var toolbarView: ToolbarView
  internal lateinit var frameView: MainFrameView
  internal lateinit var actionView: MainActionView

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

    val layoutRoot = view.findViewById<CoordinatorLayout>(R.id.layout_coordinator)
    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .plusMainFragmentComponent(layoutRoot)
        .inject(this)

    toolbarView.inflate(savedInstanceState)
    frameView.inflate(savedInstanceState)
    actionView.inflate(savedInstanceState)

    displayPreferenceFragment()

    presenter.bind(viewLifecycleOwner, this)
    serviceStatePresenter.bind(viewLifecycleOwner, this)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    toolbarView.teardown()
    frameView.teardown()
    actionView.teardown()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    toolbarView.saveState(outState)
    frameView.saveState(outState)
    actionView.saveState(outState)
  }

  override fun onServiceStarted() {
    actionView.setFabFromServiceState(true)
  }

  override fun onServiceStopped() {
    actionView.setFabFromServiceState(false)
  }

  override fun onServiceRunningAction() {
    ServiceInfoDialog()
        .show(requireActivity(), "service_info")
  }

  override fun onServiceStoppedAction() {
    AccessibilityRequestDialog()
        .show(requireActivity(), "accessibility")
  }

  override fun onSignificantScrollEvent(visible: Boolean) {
    actionView.toggleVisibility(visible)
  }

  private fun displayPreferenceFragment() {
    val fragmentManager = childFragmentManager
    if (fragmentManager.findFragmentByTag(MainSettingsFragment.TAG) == null) {
      fragmentManager.beginTransaction()
          .add(frameView.id(), MainSettingsFragment(), MainSettingsFragment.TAG)
          .commit(viewLifecycleOwner)
    }
  }

  companion object {

    const val TAG = "MainFragment"
  }
}

