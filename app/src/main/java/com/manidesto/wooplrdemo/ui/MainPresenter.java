package com.manidesto.wooplrdemo.ui;

import android.os.Bundle;
import android.util.Log;

import com.manidesto.wooplrdemo.data.Room;
import com.manidesto.wooplrdemo.data.RoomsApi;

import java.util.ArrayList;

public class MainPresenter implements RoomsApi.RoomsCallback{
    private static final String STATE_LOADING = "state_loading";
    private static final String STATE_ERROR = "state_error";
    private static final String STATE_ROOMS = "state_rooms";
    private static final String STATE_CURRENT = "state_current";
    private static final String STATE_POPUP = "state_popup";

    private RoomsApi roomsApi;
    private MainView view;

    //State
    private boolean loading = false;
    private String error;
    private ArrayList<Room> rooms;
    private int current = 0;//index of current room
    private boolean popup = false;

    public MainPresenter(RoomsApi roomsApi) {
        this.roomsApi = roomsApi;
    }

    public void addView(MainView view) {
        this.view = view;
        if(rooms == null) {
            roomsApi.fetchRooms(this);
            loading = true;
        }
        setStateToView();
    }

    public void removeView() {
        this.view = null;
    }

    public void saveStateTo(Bundle state) {
        state.putBoolean(STATE_LOADING, loading);
        state.putString(STATE_ERROR, error);
        state.putParcelableArrayList(STATE_ROOMS, rooms);
        state.putInt(STATE_CURRENT, current);
        state.putBoolean(STATE_POPUP, popup);
    }

    public void restoreStateFrom(Bundle state) {
        roomsApi.cancel();

        loading = state.getBoolean(STATE_LOADING);
        error = state.getString(STATE_ERROR);
        rooms = state.getParcelableArrayList(STATE_ROOMS);
        current = state.getInt(STATE_CURRENT);
        popup = state.getBoolean(STATE_POPUP);

        setStateToView();
    }

    @Override
    public void onRoomsLoaded(ArrayList<Room> rooms) {
        this.loading = false;
        this.rooms = rooms;
        this.error = null;
        current = 0;

        setStateToView();
    }

    @Override
    public void onError(String error) {
        this.loading = false;
        this.error = error;
        this.rooms = null;
        current = 0;

        setStateToView();
    }

    public void onMarkerClicked(int position) {
        current = position;
        popup = true;
        setStateToView();
    }

    public void onRoomCardSwiped(int position) {
        current = position;
        popup = false;
        setStateToView();
    }

    private void setStateToView() {
        if(view == null) {
            return;
        }

        if(loading) {
            view.showLoading();
        } else if(error != null) {
            view.showError(error);
        } else if(rooms != null){
            view.showRooms(rooms);
            view.goToRoomAt(current);
            if(popup) {
                view.showPopup();
            }
        } else {
            Log.e("MAIN_PRESENTER", "This condition should not be reached");
        }
    }
}
