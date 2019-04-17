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
import androidx.lifecycle.LifecycleOwner
import com.pyamsoft.pasterino.main.MainFragmentViewModel.FragmentState
import com.pyamsoft.pasterino.service.ServiceStateViewModel
import com.pyamsoft.pasterino.service.ServiceStateViewModel.ServiceState
import com.pyamsoft.pydroid.arch.BaseUiComponent
import com.pyamsoft.pydroid.arch.doOnDestroy
import com.pyamsoft.pydroid.arch.renderOnChange
import com.pyamsoft.pydroid.ui.arch.InvalidIdException
import javax.inject.Inject

internal class MainFragmentUiComponentImpl @Inject internal constructor(
  private val viewModel: MainFragmentViewModel,
  private val serviceStateViewModel: ServiceStateViewModel,
  private val frameView: MainFrameView,
  private val actionView: MainActionView
) : BaseUiComponent<MainFragmentUiComponent.Callback>(),
    MainFragmentUiComponent {

  override fun id(): Int {
    throw InvalidIdException
  }

  override fun onBind(
    owner: LifecycleOwner,
    savedInstanceState: Bundle?,
    callback: MainFragmentUiComponent.Callback
  ) {
    owner.doOnDestroy {
      frameView.teardown()
      actionView.teardown()
      viewModel.unbind()
      serviceStateViewModel.unbind()
    }

    frameView.inflate(savedInstanceState)
    actionView.inflate(savedInstanceState)
    serviceStateViewModel.bind { state, oldState ->
      renderServiceState(state, oldState)
    }
    viewModel.bind { state, oldState ->
      renderStarted(state, oldState)
      renderVisible(state, oldState)
    }
  }

  private fun renderStarted(
    state: FragmentState,
    oldState: FragmentState?
  ) {
    state.renderOnChange(oldState, value = { it.isStarted }) { isStarted ->
      if (isStarted != null) {
        if (isStarted.isStarted) {
          callback.onShowServiceInfo()
        } else {
          callback.onShowPermissionDialog()
        }
      }
    }
  }

  private fun renderVisible(
    state: FragmentState,
    oldState: FragmentState?
  ) {
    state.renderOnChange(oldState, value = { it.isVisible }) { visible ->
      if (visible != null) {
        actionView.toggleVisibility(visible.isVisible)
      }
    }
  }

  private fun renderServiceState(
    state: ServiceState,
    oldState: ServiceState?
  ) {
    state.renderOnChange(oldState, value = { it.isStarted }) { isStarted ->
      if (isStarted != null) {
        actionView.setFabFromServiceState(isStarted.isStarted)
      }
    }
  }

  override fun onSaveState(outState: Bundle) {
    frameView.saveState(outState)
    actionView.saveState(outState)
  }

}