package com.productversion.googlemaptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    FrameLayout fram_map;
    Button btn_draw_state;
    Boolean Is_MAP_Moveable = false;
    GoogleMap mMap;
    PolylineOptions rectOptions;
    ArrayList<LatLng> val = new ArrayList<>();
    Projection projection;
    Polyline polyline;

    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fram_map = (FrameLayout)findViewById(R.id.fram_map);
        btn_draw_state = (Button)findViewById(R.id.btn_draw_State);
        mapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);


        btn_draw_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Is_MAP_Moveable != true) {
                    fram_map.setOnTouchListener(onTouchListener);
                    Is_MAP_Moveable = true;
                } else {
                    Is_MAP_Moveable = false;
                    fram_map.setOnTouchListener(null);
                }
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(65.001898, 25.478067)));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(12));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Sovellus voi soittaa puheluita", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Sovellus ei voi soittaa puheluita", Toast.LENGTH_LONG).show();
                }
        }
    }

    public void drawMap() {
        rectOptions = new PolylineOptions();
        rectOptions.addAll(val);
        rectOptions.color(Color.RED);
        rectOptions.width(7);
        polyline = mMap.addPolyline(rectOptions);
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            int x_co = Math.round(x);
            int y_co = Math.round(y);

            projection = mMap.getProjection();
            Point x_y_points = new Point(x_co, y_co);

            LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
            latitude = latLng.latitude;

            longitude = latLng.longitude;

            int eventaction = event.getAction();
            switch (eventaction) {
                case MotionEvent.ACTION_DOWN:
                    // finger touches the screen
                    if (polyline != null){
                        polyline.setZIndex(0);
                        polyline.remove();
                        val.removeAll(polyline.getPoints());
                    }
                    val.add(new LatLng(latitude, longitude));
                    Log.e("Coordinates", String.valueOf(latitude) + "," +String.valueOf(longitude));
                    break;

                case MotionEvent.ACTION_MOVE:
                    // finger moves on the screen
                    val.add(new LatLng(latitude, longitude));
                    Log.e("Coordinates", String.valueOf(latitude) + "," + String.valueOf(longitude));
                    drawMap();
                    break;

                case MotionEvent.ACTION_UP:
                    // finger leaves the screen
                    Log.e("Coordinates", String.valueOf(latitude) + "," +String.valueOf(longitude));
                    break;
            }

            if (Is_MAP_Moveable == true) {
                return true;

            } else {
                return false;
            }
    }
    };



}
