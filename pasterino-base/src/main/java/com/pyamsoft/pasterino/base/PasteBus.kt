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

package com.pyamsoft.pasterino.base

import com.pyamsoft.pasterino.model.ServiceEvent
import com.pyamsoft.pydroid.bus.EventBus
import com.pyamsoft.pydroid.bus.RxBus
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
