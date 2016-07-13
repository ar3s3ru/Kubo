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
import com.github.ar3s3ru.kubo.backend.models.Board;
import com.github.ar3s3ru.kubo.backend.models.BoardsList;
import com.github.ar3s3ru.kubo.backend.models.autoparcel.ThreadsList;

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
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            switch (action) {
                case KuboRESTIntents.GET_BOARDS:
                    mAPInterface.getBoards().enqueue(this);
                    break;
                case KuboRESTIntents.GET_CATALOG:
                    String path = intent.getStringExtra(KuboRESTIntents.GET_CATALOG_PATH);
                    mAPInterface.getCatalog(path).enqueue(this);
                    break;
            }
        }
    }

    /**
     * Retrofit callback for HTTP response handling
     * @param call Retrofit HTTP call object
     * @param response Retrofit HTTP response object
     */
    @Override
    public void onResponse(Call call, Response response) {
        if (response.isSuccessful()) {
            // General programming manteined through 'instanceof' operator
            if (response.body() instanceof BoardsList) {
                // Handle BoardsList
                handleGetBoards((BoardsList) response.body());
            } else if (response.body() instanceof List &&
                       ((List) response.body()).get(0) instanceof ThreadsList) {
                // TODO: unchecked casts revising
                // Handle List<ThreadsList>
                handleGetCatalog((List<ThreadsList>) response.body());
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
     *
     * @param action
     * @param statusTag
     * @param errorTag
     * @param errcodTag
     * @param response
     * @return
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
        } else if (response.body() instanceof List && ((List) response.body()).get(0) instanceof ThreadsList) {
            // getCatalog() error
            intent = newErrorIntent(KuboEvents.CATALOG, KuboEvents.CATALOG_STATUS,
                    KuboEvents.CATALOG_ERROR, KuboEvents.CATALOG_ERRCOD, response);
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
        Intent intent = new Intent(KuboEvents.BOARDS);

        try {
            // Adding boards into the database
            for (Board board : list.getBoards()) {
                KuboTableBoard.insertBoard(mDBHelper, board);
            }

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
        Log.e(TAG, list.toString());
    }
}
