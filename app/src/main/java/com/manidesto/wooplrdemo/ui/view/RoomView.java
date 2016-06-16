package com.manidesto.wooplrdemo.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.manidesto.wooplrdemo.R;
import com.manidesto.wooplrdemo.data.Room;
import com.squareup.picasso.Picasso;

public class RoomView extends FrameLayout{
    private ImageView image;
    private TextView name;
    private TextView price;

    public RoomView(Context context) {
        super(context);
        init();
    }

    public RoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_room, this, true);

        image = (ImageView) findViewById(R.id.img_room);
        name = (TextView) findViewById(R.id.tv_name);
        price = (TextView) findViewById(R.id.tv_price);
    }

    public void setRoom(Room room) {
        name.setText(room.getName());
        price.setText(getContext().getString(R.string.price_template, room.getPrice()));

        Picasso.with(getContext())
                .load(room.getImage())
                .placeholder(null)
                .centerCrop()
                .fit()
                .into(image);
    }
}
