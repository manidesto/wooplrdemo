package com.manidesto.wooplrdemo.data;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class RoomsApi {
    private static final String ROOMS_JSON = "wooplr.json";
    private RoomsTask task;
    private Context context;

    public RoomsApi(Context context) {
        this.context = context.getApplicationContext();
    }

    public void fetchRooms(@NonNull RoomsCallback callback) {
        if(task != null) {
            task.cancel(true);
        }

        task = new RoomsTask(context, callback);
        task.execute("");
    }

    public void cancel() {
        if(task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    private static class RoomsTask extends AsyncTask<String, Long, ArrayList<Room>> {
        Context context;
        RoomsCallback callback;
        String error;

        RoomsTask(Context context, RoomsCallback callback) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        protected ArrayList<Room> doInBackground(String... params) {
            try {
                InputStream is = context.getAssets().open(ROOMS_JSON);
                InputStreamReader reader = new InputStreamReader(is);
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Room>>(){}.getType();
                return gson.fromJson(reader, listType);
            } catch (IOException e) {
                error = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Room> rooms) {
            super.onPostExecute(rooms);
            if(error == null) {
                callback.onRoomsLoaded(rooms);
            } else {
                callback.onError(error);
            }
        }
    }

    public interface RoomsCallback {
        void onRoomsLoaded(ArrayList<Room> rooms);
        void onError(String error);
    }
}
