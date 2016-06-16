package com.manidesto.wooplrdemo.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.manidesto.wooplrdemo.R;
import com.manidesto.wooplrdemo.data.Room;
import com.manidesto.wooplrdemo.data.RoomsApi;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        ViewPager.OnPageChangeListener, GoogleMap.InfoWindowAdapter, Target{
    MainPresenter mainPresenter;
    ViewPager viewPager;
    TextView errorTextView;
    View loadingIndicator;
    View infoContents;
    ImageView infoImage;
    TextView infoText;

    RoomPagerAdapter pagerAdapter;
    GoogleMap googleMap;
    List<Room> rooms;
    List<Marker> markers;

    int current = 0; //current room position
    int infoPos = -1;

    int imgWidth, imgHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RoomsApi roomsApi = new RoomsApi(this);
        mainPresenter = new MainPresenter(roomsApi);
        pagerAdapter = new RoomPagerAdapter();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        errorTextView = (TextView) findViewById(R.id.tv_error);
        loadingIndicator = findViewById(R.id.loading_indicator);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        infoContents = LayoutInflater.from(this).inflate(R.layout.layout_info_contents, null);
        infoImage = (ImageView) infoContents.findViewById(R.id.info_image);
        infoText = (TextView) infoContents.findViewById(R.id.info_text);

        imgWidth = getResources().getDimensionPixelSize(R.dimen.info_image_width);
        imgHeight = getResources().getDimensionPixelSize(R.dimen.info_image_height);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMarkerClickListener(this);
        googleMap.setInfoWindowAdapter(this);

        mainPresenter.addView(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mainPresenter.addView(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mainPresenter.removeView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainPresenter.saveStateTo(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mainPresenter.restoreStateFrom(savedInstanceState);
    }

    @Override
    public void showLoading() {
        viewPager.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String error) {
        errorTextView.setText(error);

        viewPager.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void showRooms(List<Room> rooms) {
        if(rooms != this.rooms) {
            this.rooms = rooms;
            addMarkersToMap();
            pagerAdapter.setRooms(rooms);
        }

        if(markers == null) {
            addMarkersToMap();
        }

        viewPager.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void goToRoomAt(int position) {
        if(googleMap != null) {
            markers.get(current).setAlpha(0.25f);
            markers.get(current).hideInfoWindow();
            markers.get(position).setAlpha(1.0f);

            CameraUpdate update = CameraUpdateFactory.newLatLng(
                    markers.get(position).getPosition()
            );
            googleMap.animateCamera(update);
        }
        current = position;
        viewPager.setCurrentItem(position, true);
    }

    @Override
    public void showPopup() {
       if(googleMap != null && markers != null) {
           Marker marker = markers.get(current);
           marker.showInfoWindow();
       }
    }

    //----------Info Window Adapter---------------------
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        final int position = markers.indexOf(marker);
        if(infoPos != position) {
            infoPos = position;
            Room room = rooms.get(position);
            Picasso.with(this)
                    .load(room.getImage())
                    .placeholder(null)
                    .centerCrop()
                    .resize(imgWidth, imgHeight)
                    .into(this);
            infoText.setText(room.getName());
        }
        return infoContents;
    }
    //---------End info window adapter-----------------

    //----------------Picasso target for info window image -------------
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        infoImage.setImageBitmap(bitmap);
        Marker marker = markers.get(infoPos);
        marker.showInfoWindow();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        infoImage.setImageDrawable(placeHolderDrawable);
        Marker marker = markers.get(infoPos);
        marker.showInfoWindow();
    }
    //--------------End picasso target-------------------------------

    @Override
    public boolean onMarkerClick(Marker marker) {
        int position = markers.indexOf(marker);
        mainPresenter.onMarkerClicked(position);
        return true;
    }

    //---------View pager on page changed listener-------------------
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(current != position) {
            mainPresenter.onRoomCardSwiped(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //---------End view pager page changed listener-------------------

    private void addMarkersToMap() {
        if(googleMap == null) {
            return;
        }

        googleMap.clear();
        if(markers != null) {
            markers.clear();
        } else {
            markers = new ArrayList<>(rooms.size());
        }

        for(Room room : rooms) {
            MarkerOptions options = new MarkerOptions()
                    .alpha(0.25f)
                    .title(room.getName())
                    .position(getLatLng(room));
            Marker marker = googleMap.addMarker(options);
            markers.add(marker);
        }

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                markers.get(current).getPosition(), 17.0f
        );
        googleMap.moveCamera(update);
    }

    private LatLng getLatLng(Room room) {
        return new LatLng(room.getLatitude(), room.getLongitude());
    }
}
