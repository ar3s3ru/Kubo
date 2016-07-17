package com.github.ar3s3ru.kubo.backend.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.R;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableThread;
import com.github.ar3s3ru.kubo.backend.models.Modification;
import com.github.ar3s3ru.kubo.backend.models.ModificationList;
import com.github.ar3s3ru.kubo.views.ContentsActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import retrofit2.Response;

// TODO: Javadoc
public class KuboPushService extends Service {

    private static final String TAG = "KuboPushService";

    @Inject KuboAPInterface mAPInterface;
    @Inject KuboSQLHelper   mHelper;

    private PushThread mThread;
    private FavoriteThreadChangedReceiver mReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        // Not a Bound Service, so returns null on bindService();
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Perform dependency injection
        ((KuboApp) getApplication()).getNetComponent().inject(this);

        // Create new Thread and BroadcastReceiver
        mThread   = new PushThread(this, mAPInterface, mHelper);
        mReceiver = new FavoriteThreadChangedReceiver(mThread);

        // Register BroadcastReceiver
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(KuboEvents.FOLLOWING_UPDATE));

        mThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister BroadcastReceiver
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);

        // Interrupt thread
        mThread.interrupt();
    }

    private static class FavoriteThreadChangedReceiver extends BroadcastReceiver {

        private final WeakReference<PushThread> rThread;

        FavoriteThreadChangedReceiver(@NonNull PushThread thread) {
            super();
            rThread = new WeakReference<>(thread);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final PushThread thread = rThread.get();

            if (thread != null) {
                thread.notifyChangedFollowedThreads();
            }
        }
    }

    private static class PushThread extends Thread {

        private static final int    WAITING_TIME    = 10000;  // 10 seconds
        private static final long[] VIBRATE_PATTERN = { 0, 300, 200, 300 };

        private final Context             sContext;
        private final KuboAPInterface     mAPInterface;
        private final KuboSQLHelper       mHelper;
        private final NotificationManager mNotifManager;

        private HashMap<String, ArrayList<Modification>> mReferences;

        private boolean    changedFavorites = true;
        private final Lock lock             = new ReentrantLock();

        private Cursor actualCursor;

        PushThread(@NonNull Context serviceContext,
                   @NonNull KuboAPInterface api,
                   @NonNull KuboSQLHelper helper) {

            sContext     = serviceContext;
            mAPInterface = api;
            mHelper      = helper;

            mNotifManager = (NotificationManager) sContext.getSystemService(
                    Context.NOTIFICATION_SERVICE
            );
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                /** Locking */  lock.lock();
                // Some thread has been unfollowed... or it is first cycle run
                if (changedFavorites) {
                    // Closing actual cursor if is not null
                    if (actualCursor != null) { actualCursor.close(); }
                    // Grabbing initial followed threads
                    actualCursor = KuboTableThread.getFollowedThreads(mHelper);
                    // Building initial references
                    buildReferences();
                    // Not changed at all
                    changedFavorites = false;
                }
                /** Unlocking */ lock.unlock();

                // Traverse all the references map and update
                for (String board : mReferences.keySet()) {
                    // TODO: probably cpu inefficient, keep it in mind...
                    try {
                        // Download modification list
                        handleApiCalling(
                                mAPInterface.getUpdates(board).execute(),
                                board
                        );
                    } catch (IOException ioe) {
                        // Retrofit exception... print stacktrace (for now)
                        ioe.printStackTrace();
                    }
                }

                try {
                    // Go to sleep!
                    Thread.sleep(WAITING_TIME);
                } catch (InterruptedException ie) {
                    // Thread interrupted, exit the loop
                    return;
                }
            }
        }

        // TODO: move it to KuboTableThread?
        private void buildReferences() {
            // New HashMap to use
            mReferences = new HashMap<>();
            // Looking up the actual cursor
            for (int i = 0; i < actualCursor.getCount(); i++) {
                final String            board = KuboTableThread.getThreadBoard(actualCursor, i);
                ArrayList<Modification> value = mReferences.get(board);

                if (value == null) {
                    value = new ArrayList<>();
                    mReferences.put(board, value);
                }

                value.add(new Modification(
                        KuboTableThread.getThreadNumber(actualCursor, i),
                        KuboTableThread.getLastUpdate(actualCursor, i)
                ));
            }
        }

        private void sendNotification(@NonNull String board, int threadNumber) {
            Intent        intent  = new Intent(sContext, ContentsActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(sContext, 0, intent, 0);
            Notification  notif   = buildNotification(pIntent, board, threadNumber);

            mNotifManager.notify(threadNumber, notif);
        }

        private Notification buildNotification(@NonNull PendingIntent intent,
                                               @NonNull String board,
                                               int threadNumber) {
            return new NotificationCompat.Builder(sContext)
                    .setSmallIcon(R.drawable.ic_alert)
                    .setVibrate(VIBRATE_PATTERN)
                    .setColor(Color.RED)
                    .setLights(Color.RED, 1000, 1000)
                    .setContentTitle("/" + board + "/" + threadNumber)
                    .setContentText(sContext.getString(R.string.notification_text))
                    .setContentIntent(intent)
                    .build();
        }

        private void handleApiCalling(@NonNull Response<List<ModificationList>> response,
                                      @NonNull String board) {
            if (response.isSuccessful()) {
                final List<ModificationList>  list  = response.body();
                final ArrayList<Modification> lMods = mReferences.get(board);

                for (ModificationList mlist : list) {
                    // Check all modifications
                    for (Modification mod : mlist.threads) {
                        final int idx = lMods.indexOf(mod);
                        // If following threads are into mlist, idx != -1
                        if (idx != - 1) {
                            // Get the "old" modification object
                            final Modification check = lMods.get(idx);
                            // Check updating
                            if (check.needUpdate(mod)) {
                                // Update old modification object...
                                check.lastModified = mod.lastModified;
                                // ...write it to the DB...
                                KuboTableThread.updateLastUpdated(mHelper, mod);
                                // ...and send a notification to the user
                                sendNotification(board, mod.threadNumber);
                            }
                        }
                    }
                }

            } else {
                Log.w(TAG, response.errorBody().toString());
            }
        }

        void notifyChangedFollowedThreads() {
            /** Locking */   lock.lock();
            changedFavorites = true;
            /** Unlocking */ lock.unlock();
        }
    }
}
