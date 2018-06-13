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

package com.pyamsoft.pasterino.service

import com.pyamsoft.pasterino.api.PasteServiceInteractor
import com.pyamsoft.pydroid.core.presenter.Presenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SinglePastePresenter internal constructor(
  private val interactor: PasteServiceInteractor
) : Presenter<SinglePastePresenter.View>() {

  private var postDisposable: Disposable = Disposables.empty()

  override fun onDestroy() {
    super.onDestroy()
    postDisposable.dispose()
  }

  fun postDelayedEvent() {
    postDisposable.dispose()
    postDisposable = interactor.getPasteDelayTime()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ view?.onPost(it) }, { Timber.e(it, "onError postDelayedEvent") })
  }

  interface View : PostCallback

  interface PostCallback {

    fun onPost(delay: Long)
  }
}
