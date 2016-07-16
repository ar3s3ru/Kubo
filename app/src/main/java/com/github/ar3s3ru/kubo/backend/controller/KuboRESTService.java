package com.github.ar3s3ru.kubo.backend.controller;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.backend.database.KuboSQLHelper;
import com.github.ar3s3ru.kubo.backend.database.tables.KuboTableBoard;
import com.github.ar3s3ru.kubo.backend.models.BoardsList;
import com.github.ar3s3ru.kubo.backend.models.RepliesList;
import com.github.ar3s3ru.kubo.backend.models.ThreadsList;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * IntentService for Retrofit REST API consuming.
 * Implements a general Callback object for more asynchronization
 * (just receives all the intents and adds the relative Call to the queue).
 *
 * Successful handling is done with per-case routines;
 * error handling is done with a general routine (error format is the same).
 */
public class KuboRESTService extends IntentService implements Callback {

    private static final String TAG = "KuboRESTService";

    @Inject KuboAPInterface mAPInterface;   // Retrofit API interface
    @Inject KuboSQLHelper   mDBHelper;      // SQLite application helper

    public KuboRESTService() {
        super("KuboRESTService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform dependency injection into this object
        ((KuboApp) getApplication()).getNetComponent().inject(this);
    }

    /**
     * Enqueue calls recevied through the intent
     * @param intent Request intent
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            switch (intent.getAction()) {
                case KuboRESTIntents.GET_BOARDS:
                    mAPInterface.getBoards().enqueue(this);
                    break;
                case KuboRESTIntents.GET_CATALOG:
                    mAPInterface.getCatalog(
                            intent.getStringExtra(KuboRESTIntents.GET_CATALOG_PATH)
                    ).enqueue(this);
                    break;
                case KuboRESTIntents.GET_REPLIES:
                    mAPInterface.getReplies(
                            intent.getStringExtra(KuboRESTIntents.GET_REPLIES_PATH),
                            intent.getIntExtra(KuboRESTIntents.GET_REPLIES_NUMBER, 0)
                    ).enqueue(this);
                    break;
            }
        }
    }

    /**
     * Retrofit callback for HTTP response handling
     * @param call Retrofit HTTP call object
     * @param response Retrofit HTTP response object
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onResponse(Call call, Response response) {
        if (response.isSuccessful()) {
            // General programming manteined through 'instanceof' operator
            if (response.body() instanceof BoardsList) {
                // Handle BoardsList
                handleGetBoards((BoardsList) response.body());
            } else if (response.body() instanceof List &&
                       ((List) response.body()).get(0) instanceof ThreadsList) {
                // Handle List<ThreadsList>
                handleGetCatalog((List<ThreadsList>) response.body());
            } else if (response.body() instanceof RepliesList) {
                // Handle RepliesList

            } else {
                // Handle no events recognized
                Log.e(TAG, "No event recognized, from call: " + call.toString());
            }
        } else {
            handleErrors(response);
        }
    }

    /**
     * Retrofit callback for HTTP failed responses handling
     * (not server's causes, but client's)
     * @param call Retrofit HTTP call object
     * @param t Error exception
     */
    @Override
    public void onFailure(Call call, Throwable t) {
        t.printStackTrace();
    }

    /**
     * Generate a new intent for a certain action (service requests from UI thread)
     * @param context UI context (Activity, Fragment, ...)
     * @param action Action to perform
     * @return New request intent for the desired action
     */
    private static Intent newIntent(@NonNull Context context, @NonNull String action) {
        return new Intent(context, KuboRESTService.class).setAction(action);
    }

    /**
     * Returns a new Intent for an error response callback
     * @param action Event action
     * @param statusTag Event status tag
     * @param errorTag Event error tag
     * @param errcodTag Event error code tag
     * @param response Error response string
     * @return Intent for error response callback
     */
    private static Intent newErrorIntent(@NonNull String action,
                                         @NonNull String statusTag,
                                         @NonNull String errorTag,
                                         @NonNull String errcodTag,
                                         @NonNull Response response) {
        return new Intent(action)
                .putExtra(statusTag, false)
                .putExtra(errorTag, response.errorBody().toString())
                .putExtra(errcodTag, response.code());
    }

    /**
     * Handle failed HTTP responses (300/400/500 codes).
     * @param response Failed HTTP response
     */
    private void handleErrors(Response response) {
        // Intent to send
        Intent intent = null;

        // General programming manteined through 'instanceof' operator
        if (response.body() instanceof BoardsList) {
            // getBoards() error
            intent = newErrorIntent(KuboEvents.BOARDS, KuboEvents.BOARDS_STATUS,
                    KuboEvents.BOARDS_ERR, KuboEvents.BOARDS_ERRCOD, response);
            // ---------------------------------------------------------------- //
        } else if (response.body() instanceof List &&
                   ((List) response.body()).get(0) instanceof ThreadsList) {
            // getCatalog() error
            intent = newErrorIntent(KuboEvents.CATALOG, KuboEvents.CATALOG_STATUS,
                    KuboEvents.CATALOG_ERROR, KuboEvents.CATALOG_ERRCOD, response);
        } else if (response.body() instanceof RepliesList) {
            intent = newErrorIntent(KuboEvents.REPLIES, KuboEvents.REPLIES_STATUS,
                    KuboEvents.REPLIES_ERROR, KuboEvents.REPLIES_ERRCOD, response);
        }

        if (intent != null) {
            // If the intent has been successfully set, send it via LocalBroadcastManager
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    /**
     * Handle successful HTTP response to getBoards() callback
     * @param list Downloaded boards list
     */
    private void handleGetBoards(BoardsList list) {
        final Intent intent = new Intent(KuboEvents.BOARDS);
        // There could be a SQLException if the unique constraints are not satisfied
        try {
            // Adding boards into the database
            KuboTableBoard.insertBoards(mDBHelper, list);
            // Everything went good :-)
            LocalBroadcastManager
                    .getInstance(this)
                    .sendBroadcast(intent.putExtra(KuboEvents.BOARDS_STATUS, true));
        } catch (SQLException ex) {
            // Oh... Database refused to oblige
            LocalBroadcastManager
                    .getInstance(this)
                    .sendBroadcast(intent
                            .putExtra(KuboEvents.BOARDS_STATUS, false)
                            .putExtra(KuboEvents.BOARDS_ERR, ex.getMessage())
                            .putExtra(KuboEvents.BOARDS_ERRCOD, 1));
        }
    }

    /**
     * Handle successful HTTP response to getCatalog(path) callback
     * @param list Downloaded catalog list
     */
    private void handleGetCatalog(List<ThreadsList> list) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(KuboEvents.CATALOG)
                        .putExtra(KuboEvents.CATALOG_STATUS, true)
                        .putExtra(KuboEvents.CATALOG_RESULT, Parcels.wrap(list))
        );
    }

    /**
     * Handle successful HTTP response to getReplies(path, number) callback
     * @param list Downloaded thread replies list
     */
    private void handleGetReplies(RepliesList list) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(KuboEvents.BOARDS)
                        .putExtra(KuboEvents.BOARDS_STATUS, true)
                        .putExtra(KuboEvents.REPLIES_RESULT, Parcels.wrap(list))
        );
    }

    /**
     * Makes a getBoards() request to the IntentService
     * @param context UI context (Activity, Fragment, ...)
     */
    public static void getBoards(@NonNull Context context) {
        context.startService(newIntent(context, KuboRESTIntents.GET_BOARDS));
    }

    /**
     * Makes a getCatalog(path) request to the IntentService
     * @param context UI context (Activity, Fragment, ...)
     * @param path Board request catalog path
     */
    public static void getCatalog(@NonNull Context context, @NonNull String path) {
        context.startService(
                newIntent(context, KuboRESTIntents.GET_CATALOG)
                        .putExtra(KuboRESTIntents.GET_CATALOG_PATH, path) // Path argument
        );
    }

    /**
     * Makes a getReplies(path, number) request to the IntentService
     * @param context UI context (Activity, Fragment, ...)
     * @param path Board in which requested thread resides on
     * @param threadNumber Requested thread number
     */
    public static void getReplies(@NonNull Context context,
                                  @NonNull String path,
                                  int threadNumber) {
        context.startService(
                newIntent(context, KuboRESTIntents.GET_REPLIES)
                    .putExtra(KuboRESTIntents.GET_REPLIES_PATH, path)
                    .putExtra(KuboRESTIntents.GET_REPLIES_NUMBER, threadNumber)
        );
    }
}
