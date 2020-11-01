/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.pasterino.service.monitor.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.pyamsoft.pasterino.R
import com.pyamsoft.pasterino.service.single.SinglePasteReceiver
import com.pyamsoft.pydroid.notify.NotifyChannelInfo
import com.pyamsoft.pydroid.notify.NotifyData
import com.pyamsoft.pydroid.notify.NotifyDispatcher
import com.pyamsoft.pydroid.notify.NotifyId
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import com.pyamsoft.pydroid.ui.R as R2

@Singleton
internal class PasteNotificationDispatcher @Inject internal constructor(
    private val context: Context
) : NotifyDispatcher<PasteNotification> {

    private val notificationManager by lazy { requireNotNull(context.getSystemService<NotificationManager>()) }
    private val pendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            RC,
            Intent(context, SinglePasteReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun setupNotificationChannel(channelInfo: NotifyChannelInfo) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Timber.d("No channel below Android O")
            return
        }

        val channel: NotificationChannel? =
            notificationManager.getNotificationChannel(channelInfo.id)
        if (channel != null) {
            Timber.d("Channel already exists: ${channel.id}")
            return
        }

        val notificationGroup = NotificationChannelGroup(channelInfo.id, channelInfo.title)
        val notificationChannel =
            NotificationChannel(
                channelInfo.id,
                channelInfo.title,
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                group = notificationGroup.id
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                description = channelInfo.description
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }

        Timber.d("Create notification channel and group ${notificationChannel.id} ${notificationGroup.id}")
        notificationManager.apply {
            createNotificationChannelGroup(notificationGroup)
            createNotificationChannel(notificationChannel)
        }
    }

    override fun build(
        id: NotifyId,
        channelInfo: NotifyChannelInfo,
        notification: PasteNotification
    ): Notification {
        setupNotificationChannel(channelInfo)
        return NotificationCompat.Builder(context, channelInfo.id)
            .setSmallIcon(R.drawable.ic_paste_notification)
            .setContentText("Pasterino Plzarino")
            .setContentIntent(pendingIntent)
            .setWhen(0)
            .setOngoing(true)
            .setAutoCancel(false)
            .setNumber(0)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setColor(ContextCompat.getColor(context, R2.color.green500)).build()
    }

    override fun canShow(notification: NotifyData): Boolean {
        return notification is PasteNotification
    }

    companion object {

        private const val RC = 1005
    }
}
