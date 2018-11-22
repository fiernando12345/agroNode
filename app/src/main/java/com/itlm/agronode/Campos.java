package com.itlm.agronode;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itlm.agronode.Model.adapter_campos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itlm.agronode.Model.Campo;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class Campos extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    View mapView;
    FloatingActionButton fab;

    FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference root,primary;

    ArrayList<Campo> listFields = new ArrayList<Campo>();
    RecyclerView rv;
    adapter_campos adapter;
    Random random= new Random();
    TextView tvCampos;

    public static String Nombre, Coordenada, UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campos);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rv= findViewById(R.id.rv_Campos);
        //database reference pointing to root of database
        root = FirebaseDatabase.getInstance().getReference(FireBaseReference.REFERENCIAUSUARIO);
        //database reference pointing to demo node
        primary = root.child(userid.getUid());

        rv.setLayoutManager(new LinearLayoutManager(Campos.this));
        adapter= new adapter_campos(listFields);
        rv.setAdapter(adapter);

        tvCampos= findViewById(R.id.tv_Campos);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Campos.this, Registrar_Campos.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(Campos.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(Campos.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Campos.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        moveMarker();

        try {
            primary.child(FireBaseReference.TABLACAMPOS).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int contador = 0;
                    listFields.clear();
                    for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                        Campo field = objSnaptshot.getValue(Campo.class);
                        listFields.add(field);
                        int nextInt = random.nextInt(0xffffff + 1);
                        String colorCode = String.format("#%06x", nextInt);
                        int nextInt2 = random.nextInt(0xffffff + 1);
                        String colorCode2 = String.format("#%06x", nextInt2);
                        PolygonOptions polygonOptions = new PolygonOptions();
                        StringTokenizer st = new StringTokenizer(field.getCoordenada(), ":;");
                        while (st.hasMoreTokens()) {
                            String Longitud = st.nextToken();
                            String Latitud = st.nextToken();
                            LatLng p = new LatLng(Double.parseDouble(Latitud), Double.parseDouble(Longitud));
                            polygonOptions.add(p);
                            if (contador == 0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11.0f));
                            }
                            contador++;
                        }
                        polygonOptions.strokeColor(Color.parseColor(colorCode)).fillColor(Color.parseColor(colorCode2));
                        Polygon polygon = mMap.addPolygon(polygonOptions);
                    }
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){Toast.makeText(Campos.this, "Ocurrió un error obteniendo los datos", Toast.LENGTH_SHORT).show();}

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nombre= listFields.get(rv.getChildAdapterPosition(v)).getNombre();
                Coordenada= listFields.get(rv.getChildAdapterPosition(v)).getCoordenada();
                UID= listFields.get(rv.getChildAdapterPosition(v)).getUid();
                try {
                    startActivity(new Intent(Campos.this, Reportes.class));
                }catch (Exception e){Toast.makeText(Campos.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_iniciar__sesiones, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.acerca_de) {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Campos.this);
            dialogo1.setTitle("Bienvenido a AgroNow");
            dialogo1.setMessage("Bienvenido a la aplicación AgroNow");
            dialogo1.setCancelable(true);
            dialogo1.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
