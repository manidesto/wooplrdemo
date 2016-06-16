package com.manidesto.wooplrdemo.ui;

import com.manidesto.wooplrdemo.data.Room;

import java.util.List;

public interface MainView {
    void showLoading();
    void showError(String error);
    void showRooms(List<Room> rooms);
    void goToRoomAt(int position);
    void showPopup();
}
