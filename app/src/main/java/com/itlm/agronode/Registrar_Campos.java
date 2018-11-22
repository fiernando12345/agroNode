package com.itlm.agronode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itlm.agronode.Model.Campo;
import com.spark.submitbutton.SubmitButton;

import java.util.UUID;

public class Registrar_Campos extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    View mapView;
    Double Latitud_Presionada, Longitud_Presionada;

    DatabaseReference root, primary;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    SubmitButton btnRegistrar;
    EditText txtNombre;
    String coordenadas = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar__campos);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        inicializarFirebase();
        FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        //database reference pointing to root of database
        root = FirebaseDatabase.getInstance().getReference(FireBaseReference.REFERENCIAUSUARIO);
        //database reference pointing to demo node
        primary = root.child(userid.getUid()).child(FireBaseReference.TABLACAMPOS);
        txtNombre = findViewById(R.id.txtregistro_campos_nombre);
        btnRegistrar = findViewById(R.id.btnRegistrarCampo);
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();
    }

    public void moveMarker(){
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }

    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Latitud_Presionada= latLng.latitude;
                Longitud_Presionada= latLng.longitude;
                coordenadas+= Longitud_Presionada + ":" + Latitud_Presionada + ";";
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                map.addMarker(markerOptions);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(Registrar_Campos.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Registrar_Campos.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        moveMarker();
        setMapLongClick(mMap);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Campo field = new Campo();
                field.setUid(UUID.randomUUID().toString());
                field.setNombre(txtNombre.getText().toString().trim());
                field.setCoordenada(coordenadas);
                primary.child(field.getUid()).setValue(field);
                Toast.makeText(Registrar_Campos.this, "Campo agregado", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Registrar_Campos.this, Campos.class);
                    startActivity(intent);
                    finish();
                }catch (Exception e){Toast.makeText(Registrar_Campos.this, "Ah ocurrido un error", Toast.LENGTH_SHORT).show();}
            }
        });
    }
}
