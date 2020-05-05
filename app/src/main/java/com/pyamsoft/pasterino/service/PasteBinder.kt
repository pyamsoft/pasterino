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

package com.pyamsoft.pasterino.service

import com.pyamsoft.highlander.highlander
import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pasterino.service.ServiceControllerEvent.Finish
import com.pyamsoft.pasterino.service.ServiceControllerEvent.PasteEvent
import com.pyamsoft.pydroid.arch.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PasteBinder @Inject internal constructor(
    private val finishBus: EventBus<ServiceFinishEvent>,
    private val pasteRequestBus: EventBus<PasteRequestEvent>,
    private val interactor: PasteServiceInteractor
) : Binder<ServiceControllerEvent>() {

    private val pasteRunner = highlander<Unit, (event: ServiceControllerEvent) -> Unit> { onEvent ->
        val delayTime = interactor.getPasteDelayTime()
        delay(delayTime)
        val isDeepSearchEnabled = interactor.isDeepSearchEnabled()
        onEvent(PasteEvent(isDeepSearchEnabled))
    }

    override fun onBind(onEvent: (event: ServiceControllerEvent) -> Unit) {
        binderScope.launch {
            listenFinish(onEvent)
            listenPaste(onEvent)
        }
    }

    private inline fun CoroutineScope.listenFinish(crossinline onEvent: (event: ServiceControllerEvent) -> Unit) =
        launch(context = Dispatchers.Default) {
            finishBus.onEvent { withContext(context = Dispatchers.Main) { onEvent(Finish) } }
        }

    private inline fun CoroutineScope.listenPaste(crossinline onEvent: (event: ServiceControllerEvent) -> Unit) =
        launch(context = Dispatchers.Default) {
            pasteRequestBus.onEvent { paste(onEvent) }
        }

    private inline fun CoroutineScope.paste(crossinline onEvent: (event: ServiceControllerEvent) -> Unit) =
        launch {
            pasteRunner.call { event ->
                launch(context = Dispatchers.Main) { onEvent(event) }
            }
        }

    fun start() {
        interactor.setServiceState(true)
    }

    fun stop() {
        interactor.setServiceState(false)
    }
}
