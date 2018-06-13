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

package com.pyamsoft.pasterino.base

import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.core.bus.EventBus
import com.pyamsoft.pydroid.core.bus.RxBus
import io.reactivex.Observable

/**
 * This is in base because it is used by both main and service modules and is a singleton
 * so it should only be initialized once
 *
 * We do not use an object so that creation of buses is controlled, but access and usage is public
 */
internal class PasteBus internal constructor() : EventBus<ServiceEvent> {

  private val bus = RxBus.create<ServiceEvent>()

  override fun publish(event: ServiceEvent) {
    bus.publish(event)
  }

  override fun listen(): Observable<ServiceEvent> = bus.listen()
}
