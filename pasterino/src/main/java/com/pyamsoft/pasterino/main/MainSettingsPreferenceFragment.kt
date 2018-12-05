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

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pyamsoft.pasterino.Injector
import com.pyamsoft.pasterino.PasterinoComponent
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pasterino.service.PasteServiceNotification
import com.pyamsoft.pasterino.service.SinglePasteService
import com.pyamsoft.pydroid.core.bus.Publisher
import com.pyamsoft.pydroid.core.singleDisposable
import com.pyamsoft.pydroid.core.tryDispose
import com.pyamsoft.pydroid.ui.app.fragment.SettingsPreferenceFragment
import com.pyamsoft.pydroid.ui.util.show
import timber.log.Timber

class MainSettingsPreferenceFragment : SettingsPreferenceFragment() {

  internal lateinit var viewModel: MainViewModel
  internal lateinit var publisher: Publisher<ServiceEvent>
  internal lateinit var settingsView: SettingsView

  private var clearDisposable by singleDisposable()
  private var scrollListenerDisposable by singleDisposable()

  override val rootViewContainer: Int = R.id.main_container

  override val preferenceXmlResId: Int = R.xml.preferences

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    Injector.obtain<PasterinoComponent>(requireContext().applicationContext)
        .plusMainComponent(viewLifecycleOwner, preferenceScreen, TAG)
        .inject(this)

    settingsView.create()
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)

    settingsView.onExplainClicked { HowToDialog().show(requireActivity(), "howto") }

    addScrollListener()
    clearDisposable = viewModel.onClearAllEvent { onClearAll() }
  }

  private fun addScrollListener() {
    scrollListenerDisposable = viewModel.onScrollListenerCreated {
      settingsView.addScrollListener(listView, it)
    }
    viewModel.publishScrollListenerCreateRequest()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    clearDisposable.tryDispose()
    scrollListenerDisposable.tryDispose()
  }

  private fun onClearAll() {
    requireContext().also {
      PasteServiceNotification.stop(it)
      SinglePasteService.stop(it)
      try {
        publisher.publish(ServiceEvent(ServiceEvent.Type.FINISH))
      } catch (e: NullPointerException) {
        Timber.e(e, "Expected exception when Service is NULL")
      }

      Timber.d("Clear application data")
      val activityManager = it.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
      activityManager.clearApplicationUserData()
    }
  }

  override fun onClearAllClicked() {
    ConfirmationDialog().show(requireActivity(), "confirm")
  }

  companion object {

    const val TAG = "MainSettingsPreferenceFragment"
  }
}
