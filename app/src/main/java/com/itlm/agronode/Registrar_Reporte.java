package com.itlm.agronode;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
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
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

public class Registrar_Reporte extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    View mapView;

    SubmitButton agregar;
    EditText reporte, descripcion;
    ImageButton cameraButton;
    static String ubicacion;
    DatabaseReference root,primary;
    static Uri uriFoto;
    Bitmap bmp;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar__reporte);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        //database reference pointing to root of database
        root = FirebaseDatabase.getInstance().getReference(FireBaseReference.REFERENCIAUSUARIO).child(userid.getUid());
        //database reference pointing to demo node
        primary = root.child(FireBaseReference.TABLACAMPOS).child(Campos.UID).child(FireBaseReference.TABLAREPORTES);

        agregar = (SubmitButton) findViewById(R.id.btnRegistrarReporte);
        reporte = (EditText) findViewById(R.id.txtregistro_reporte_nombre);
        descripcion = (EditText) findViewById(R.id.txtregistro_reporte_descripcion);
        cameraButton = findViewById(R.id.ib_registrar_reporte);

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(uriFoto!=null || !ubicacion.isEmpty() || !reporte.getText().toString().isEmpty() || !descripcion.getText().toString().isEmpty()) {
                        Reporte p = new Reporte();
                        String subirReporte = reporte.getText().toString().trim();
                        String subirDescripcion = descripcion.getText().toString().trim();
                        p.setUID(UUID.randomUUID().toString());
                        p.setTitulo(subirReporte);
                        p.setDescripcion(subirDescripcion);
                        p.setCoordenada(ubicacion);
                        p.setEstado("Normal");
                        primary.child(p.getUID()).setValue(p);
                        SubirFoto(p.getUID(), "Observacion", uriFoto);
                        finish();
                    }else{
                        Toast.makeText(Registrar_Reporte.this, "Complete todos los procedimientos", Toast.LENGTH_SHORT).show();
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
            uriFoto= getImageUri(Registrar_Reporte.this, bmp);
            cameraButton.setImageURI(getImageUri(Registrar_Reporte.this, bmp));
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
                Toast.makeText(Registrar_Reporte.this, "Dato agregado correctamente", Toast.LENGTH_SHORT).show();
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

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Registrar_Reporte.this);
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
                        map.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
                        // Placing a marker on the touched position
                        map.addMarker(markerOptions);
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Toast.makeText(Registrar_Reporte.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogo1.show();
            }
        });
    }
}
