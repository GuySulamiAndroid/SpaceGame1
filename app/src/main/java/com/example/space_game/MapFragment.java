package com.example.space_game;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private final LatLng DEFAULT_LOCATION = new LatLng(32.0853, 34.7818);
    private GoogleMap gMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment =(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    public void updateMarker(String name, double lat, double lng){
        if(gMap != null){
            LatLng marker = new LatLng(lat,lng);
            gMap.clear();
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15));
            gMap.addMarker(new MarkerOptions().title(name).position(marker));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.clear();
        CameraPosition googlePlex = CameraPosition.builder().target(DEFAULT_LOCATION).
                zoom(16).bearing(0).build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
        gMap.addMarker(new MarkerOptions().position(DEFAULT_LOCATION));
    }
}
