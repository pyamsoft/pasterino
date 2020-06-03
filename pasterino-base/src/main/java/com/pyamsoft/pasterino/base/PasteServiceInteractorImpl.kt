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

package com.pyamsoft.pasterino.base

import com.pyamsoft.pasterino.api.PastePreferences
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pydroid.arch.EventBus
import com.pyamsoft.pydroid.arch.EventConsumer
import com.pyamsoft.pydroid.core.Enforcer
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
internal class PasteServiceInteractorImpl @Inject internal constructor(
    private val preferences: PastePreferences
) : PasteServiceInteractor {

    private var running = false

    private val runningStateBus = EventBus.create<Boolean>()

    override suspend fun setServiceState(start: Boolean) {
        Enforcer.assertNotOnMainThread()
        running = start
        runningStateBus.send(start)
    }

    override suspend fun observeServiceState(): EventConsumer<Boolean> =
        withContext(context = Dispatchers.Default) {
            Enforcer.assertNotOnMainThread()
            return@withContext object : EventConsumer<Boolean> {

                override suspend fun onEvent(emitter: suspend (event: Boolean) -> Unit) {
                    emitter(running)
                    runningStateBus.onEvent(emitter)
                }
            }
        }

    override suspend fun getPasteDelayTime(): Long = withContext(context = Dispatchers.Default) {
        Enforcer.assertNotOnMainThread()
        return@withContext preferences.getPasteDelayTime()
    }

    override suspend fun isDeepSearchEnabled(): Boolean =
        withContext(context = Dispatchers.Default) {
            Enforcer.assertNotOnMainThread()
            return@withContext preferences.isDeepSearchEnabled()
        }
}
