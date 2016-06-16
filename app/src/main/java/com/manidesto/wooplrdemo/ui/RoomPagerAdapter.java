package com.manidesto.wooplrdemo.ui;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.manidesto.wooplrdemo.data.Room;
import com.manidesto.wooplrdemo.ui.view.RoomView;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class RoomPagerAdapter extends PagerAdapter{
    private List<Room> rooms;
    private Queue<RoomView> recycleBin = new ArrayDeque<>(3);

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return rooms == null ? 0 : rooms.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Room room = getRoomAt(position);
        RoomView view;
        if(recycleBin.isEmpty()) {
            view = new RoomView(container.getContext());
            Log.d("VIEW_PAGER", "ROOM_VIEW inflated");
        } else {
            view = recycleBin.remove();
        }

        view.setRoom(room);
        container.addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        recycleBin.add((RoomView) object);
    }

    protected Room getRoomAt(int position) {
        return rooms.get(position);
    }
}
