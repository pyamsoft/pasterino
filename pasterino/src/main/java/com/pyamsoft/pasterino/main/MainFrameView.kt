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
import android.view.ViewGroup
import android.widget.FrameLayout
import com.pyamsoft.pasterino.R
import com.pyamsoft.pydroid.ui.arch.UiView
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EMPTY
import com.pyamsoft.pydroid.ui.arch.ViewEvent.EmptyPublisher

internal class MainFrameView internal constructor(
  private val parent: ViewGroup
) : UiView<EMPTY>(EmptyPublisher) {

  private lateinit var frameLayout: FrameLayout

  override fun id(): Int {
    return frameLayout.id
  }

  override fun inflate(savedInstanceState: Bundle?) {
    parent.inflateAndAdd(R.layout.layout_frame) {
      frameLayout = findViewById(R.id.layout_frame)
    }
  }

  override fun saveState(outState: Bundle) {
  }

  override fun teardown() {
  }

}

