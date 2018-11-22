package com.itlm.agronode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itlm.agronode.Model.Campo;
import com.itlm.agronode.Model.Reporte;
import com.itlm.agronode.Model.adapter_reportes;
import com.itlm.agronode.Model.adapter_campos;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class Reportes extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    View mapView;
    TextView tvReporte;
    FloatingActionButton fab_Agregar, fab_Quema, fab_Fuga;
    FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference root,primary;

    public static String Especial= "";

    ArrayList<Reporte> listReportes = new ArrayList<Reporte>();
    RecyclerView rv;
    adapter_reportes adapter;

    public static String Nombre, Coordenada, Estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvReporte= findViewById(R.id.tvReporte);

        tvReporte.setText("- Reportes ( " +  Campos.Nombre + " ) -");
        fab_Agregar = (FloatingActionButton) findViewById(R.id.fabnnuevo);
        fab_Fuga = (FloatingActionButton) findViewById(R.id.fabagua);
        fab_Quema = (FloatingActionButton) findViewById(R.id.fabquema);

        rv= findViewById(R.id.rv_Reportes);
        //database reference pointing to root of database
        root = FirebaseDatabase.getInstance().getReference(FireBaseReference.REFERENCIAUSUARIO);
        //database reference pointing to demo node
        primary = root.child(userid.getUid());

        rv.setLayoutManager(new LinearLayoutManager(Reportes.this));
        adapter= new adapter_reportes(listReportes);
        rv.setAdapter(adapter);

        fab_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Reportes.this, Registrar_Reporte.class);
                    startActivity(intent);
                }catch (Exception e){Toast.makeText(Reportes.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}
            }
        });
        fab_Quema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Especial= "Quema";
                try {
                    Intent intent = new Intent(Reportes.this, Registrar_Reporte_Especial.class);
                    startActivity(intent);
                }catch (Exception e){Toast.makeText(Reportes.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}
            }
        });
        fab_Fuga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Especial= "Fuga";
                try {
                    Intent intent = new Intent(Reportes.this, Registrar_Reporte_Especial.class);
                    startActivity(intent);
                }catch (Exception e){Toast.makeText(Reportes.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(Reportes.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Reportes.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        moveMarker();
        try {
            primary.child(FireBaseReference.TABLACAMPOS).child(Campos.UID).child(FireBaseReference.TABLAREPORTES).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int contador=0;
                    listReportes.clear();
                    for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                        Reporte reporte = objSnaptshot.getValue(Reporte.class);
                        listReportes.add(reporte);
                        StringTokenizer st = new StringTokenizer(reporte.getCoordenada(), ":;");
                        while (st.hasMoreTokens()) {
                            String Latitud = st.nextToken();
                            String Longitud = st.nextToken();
                            LatLng p = new LatLng(Double.parseDouble(Latitud), Double.parseDouble(Longitud));
                            if (contador == 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11.5f));
                            }
                            contador++;
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(p);
                            markerOptions.title(reporte.getTitulo() + "\nEstado: " + reporte.getEstado());
                            mMap.addMarker(markerOptions);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nombre= listReportes.get(rv.getChildAdapterPosition(v)).getTitulo();
                Coordenada= listReportes.get(rv.getChildAdapterPosition(v)).getCoordenada();
                Estado= listReportes.get(rv.getChildAdapterPosition(v)).getEstado();
                try {

                }catch (Exception e){Toast.makeText(Reportes.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}
            }
        });
        }catch (Exception e){Toast.makeText(Reportes.this, "Ocurrió un error obteniendo los datos", Toast.LENGTH_SHORT).show();}
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
}
