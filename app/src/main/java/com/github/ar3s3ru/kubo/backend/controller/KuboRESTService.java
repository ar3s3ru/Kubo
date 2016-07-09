package com.github.ar3s3ru.kubo.backend.controller;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.github.ar3s3ru.kubo.KuboApp;
import com.github.ar3s3ru.kubo.backend.models.BoardsList;
import com.github.ar3s3ru.kubo.backend.models.parcelable.ParcelableBoardsList;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KuboRESTService extends IntentService implements Callback {

    @Inject Bus mBus;                       // Otto Bus
    @Inject KuboAPInterface mAPInterface;   // Retrofit API interface

    public KuboRESTService() {
        super("KuboRESTService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ((KuboApp) getApplication()).getNetComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            switch (action) {
                case KuboRESTIntents.GET_BOARDS:
                    mAPInterface.getBoards().enqueue(this);
                    break;
            }
        }
    }

    @Override
    public void onResponse(Call call, Response response) {
        if (response.isSuccessful()) {
            if (response.body() instanceof BoardsList) {
                // Handle BoardsList
                handleGetBoards((BoardsList) response.body());
            } else {
                // Handle no events recognized
            }
        } else {
            handleErrors(response);
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        t.printStackTrace();
    }

    private static Intent newIntent(Context context, String action) {
        return new Intent(context, KuboRESTService.class).setAction(action);
    }

    public static void getBoards(Context context) {
        context.startService(newIntent(context, KuboRESTIntents.GET_BOARDS));
    }

    private void handleErrors(Response response) {
        Intent intent = null;

        if (response.body() instanceof BoardsList) {
            intent = new Intent(KuboEvents.BOARDS)
                        .putExtra(KuboEvents.BOARDS_STATUS, false)
                        .putExtra(KuboEvents.BOARDS_ERR0, response.errorBody().toString())
                        .putExtra(KuboEvents.BOARDS_ERRCOD, response.code());
        }

        if (intent != null) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void handleGetBoards(BoardsList list) {
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(
                        new Intent(KuboEvents.BOARDS)
                                .putExtra(KuboEvents.BOARDS_ARG0, new ParcelableBoardsList(list))
                                .putExtra(KuboEvents.BOARDS_STATUS, true)
                );
    }
}
