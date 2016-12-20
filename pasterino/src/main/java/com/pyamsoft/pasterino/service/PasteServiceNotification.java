/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.pasterino.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import com.pyamsoft.pasterino.R;
import timber.log.Timber;

public final class PasteServiceNotification {

  private static final int ID = 1005;
  private static final int RC = 1005;

  private PasteServiceNotification() {
    throw new RuntimeException("No instances");
  }

  static void start(@NonNull Context context) {
    if (PasteService.isRunning()) {
      Timber.d("Start notification %d", ID);
      getNotificationManager(context).notify(ID, createNotification(context));
    }
  }

  public static void stop(@NonNull Context context) {
    Timber.d("Stop notification %d", ID);
    getNotificationManager(context).cancel(ID);
  }

  @CheckResult @NonNull
  private static NotificationManager getNotificationManager(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    return (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  @CheckResult @NonNull private static Notification createNotification(@NonNull Context context) {
    final Context appContext = context.getApplicationContext();
    final Intent singlePasteIntent = new Intent(appContext, SinglePasteService.class);
    return new NotificationCompat.Builder(appContext).setContentTitle(
        appContext.getString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_notification)
        .setContentText("Pasterino Plzarino")
        .setContentIntent(PendingIntent.getService(appContext, RC, singlePasteIntent, 0))
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setWhen(0)
        .setOngoing(true)
        .setAutoCancel(false)
        .setColor(ContextCompat.getColor(appContext, R.color.green500))
        .setNumber(0)
        .build();
  }
}
