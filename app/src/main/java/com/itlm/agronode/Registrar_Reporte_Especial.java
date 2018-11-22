package com.itlm.agronode;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itlm.agronode.Model.Reporte;
import com.spark.submitbutton.SubmitButton;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.UUID;

public class Registrar_Reporte_Especial extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    View mapView;

    SubmitButton agregar;
    ImageButton cameraButton;
    static String ubicacion;
    DatabaseReference root,primary;
    static Uri uriFoto;
    Bitmap bmp;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_reporte_especial);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        //database reference pointing to root of database
        root = FirebaseDatabase.getInstance().getReference(FireBaseReference.REFERENCIAUSUARIO).child(userid.getUid());
        //database reference pointing to demo node
        primary = root.child(FireBaseReference.TABLACAMPOS).child(Campos.UID).child(FireBaseReference.TABLAREPORTES);

        agregar = (SubmitButton) findViewById(R.id.btnRegistrar_reporte_especial);
        cameraButton = findViewById(R.id.ib_registrar_reporte_especial);

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(uriFoto!=null || !ubicacion.isEmpty()) {
                        Reporte p = new Reporte();
                        p.setUID(UUID.randomUUID().toString());
                        if(Reportes.Especial.equals("Quema")){
                            p.setTitulo("ALERTA DE FUEGO");
                            p.setDescripcion("SE HA DETECTADO FUEGO EN EL CAMPO " + Campos.Nombre + " TOME SUS PRECAUCIONES");
                        }else{
                            p.setTitulo("ALERTA DE FUGA");
                            p.setDescripcion("SE HA DETECTADO UNA FUGA EN EL CAMPO " + Campos.Nombre + " TOME SUS PRECAUCIONES");
                        }
                        p.setCoordenada(ubicacion);
                        p.setEstado(Reportes.Especial);
                        primary.child(p.getUID()).setValue(p);
                        SubirFoto(p.getUID(), "Observacion", uriFoto);
                        finish();
                    }else{
                        Toast.makeText(Registrar_Reporte_Especial.this, "Complete todos los procedimientos", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){}
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Tomarfoto();
                }catch (Exception e){}
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        setMapLongClick(mMap);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        moveMarker();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle ext = data.getExtras();
            bmp = (Bitmap) ext.get("data");
            uriFoto= getImageUri(Registrar_Reporte_Especial.this, bmp);
            cameraButton.setImageURI(getImageUri(Registrar_Reporte_Especial.this, bmp));
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void Tomarfoto(){
        try {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, 0);
        }catch (Exception e){}
    }


    public void SubirFoto(String Nombre, String Referencia, Uri uri){
        mStorageRef = FirebaseStorage.getInstance().getReference(Referencia);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final StorageReference storageReference = mStorageRef.child( Nombre + ".jpg");
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Toast.makeText(getContext(), "Completa los cuadros", Toast.LENGTH_SHORT).show();
                Toast.makeText(Registrar_Reporte_Especial.this, "Dato agregado correctamente", Toast.LENGTH_SHORT).show();
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

    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                final String snippet = String.format(Locale.getDefault(),
                        "%1$.5f:%2$.5f;",
                        latLng.latitude,
                        latLng.longitude);

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Registrar_Reporte_Especial.this);
                dialogo1.setTitle("Agregar Reporte");
                dialogo1.setMessage("¿Esta seguro que deseas agregar un Reporte aquí?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        ubicacion = snippet;
                        // Creating a marker
                        MarkerOptions markerOptions = new MarkerOptions();

                        // Setting the position for the marker
                        markerOptions.position(latLng);

                        // Setting the title for the marker.
                        // This will be displayed on taping the marker
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        // Clears the previously touched position
                        map.clear();

                        // Animating to the touched position
                        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                        // Placing a marker on the touched position
                        map.addMarker(markerOptions);
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Toast.makeText(Registrar_Reporte_Especial.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogo1.show();
            }
        });
    }
}
