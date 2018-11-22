package com.itlm.agronode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itlm.agronode.Model.Datos;
import com.spark.submitbutton.SubmitButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Registrarse extends AppCompatActivity {

    EditText txtCorreo, txtContraseña, txtNombre;
    SubmitButton btnRegistrar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener listener;
    Task<AuthResult> task = FirebaseAuth.getInstance().signInAnonymously();
    ArrayList<Datos> datosUsuarios;

    ImageButton imageButton;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference(FireBaseReference.REFERENCIAUSUARIO);

    static final int REQUEST_IMAGE_CAPTURE = 100;

    static Uri uriFoto;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        FirebaseUser userid = FirebaseAuth.getInstance().getCurrentUser();
        txtNombre = (EditText) findViewById(R.id.registroNombre);
        txtCorreo = (EditText) findViewById(R.id.registroemail);
        txtContraseña = (EditText) findViewById(R.id.registropassword);
        btnRegistrar = (SubmitButton) findViewById(R.id.btnRegistrarUsuario);
        imageButton = findViewById(R.id.ib_registrar_perfil);

        mAuth = FirebaseAuth.getInstance();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    try {
                        Intent i = new Intent(Registrarse.this, Iniciar_Sesion.class);
                        startActivity(i);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(Registrarse.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        datosUsuarios= new ArrayList<>();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtNombre.getText().toString().trim().isEmpty() || txtCorreo.getText().toString().trim().isEmpty() || txtContraseña.getText().toString().trim().isEmpty() || uriFoto==null){
                    Toast.makeText(Registrarse.this, "Complete los campos porfavor", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Registrarse.this);
                        dialogo1.setTitle("Registrarse");
                        dialogo1.setMessage("¿Estos datos son correctoso?\nNombre: " + txtNombre.getText().toString().trim() + "\nCorreo: " + txtCorreo.getText().toString().trim() + "\nContraseña: " + txtContraseña.getText().toString().trim());
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                try {
                                    RegistrarUsuario();
                                } catch (Exception e) {
                                    Toast.makeText(Registrarse.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                Toast.makeText(Registrarse.this, "Cancelado", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialogo1.show();
                    } catch (Exception e) {
                    }
                }
            }

        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    public void SubirFoto(String Nombre, String Referencia, Uri uri){
        mStorageRef = FirebaseStorage.getInstance().getReference(Referencia);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final StorageReference storageReference = mStorageRef.child( Nombre + ".jpg");
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Toast.makeText(getContext(), "Completa los cuadros", Toast.LENGTH_SHORT).show();
                Toast.makeText(Registrarse.this, "Dato agregado correctamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RegistrarUsuario(){
        final String Email= txtCorreo.getText().toString().trim();
        final String Password= txtContraseña.getText().toString().trim();
        final String Nombre= txtNombre.getText().toString().trim();
        if(!Email.isEmpty() && !Password.isEmpty() && !Nombre.isEmpty()){
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Datos datos= new Datos(Nombre, Email, Password);
                        myRef.child(mAuth.getUid()).child(FireBaseReference.TABLADATOS).push().setValue(datos);
                        SubirFoto(mAuth.getUid(), "Perfil", uriFoto);
                    } else {

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "Usuario ya en uso", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    finish();
                    try {
                        startActivity(new Intent(Registrarse.this, Iniciar_Sesion.class));
                    }catch (Exception e){Toast.makeText(Registrarse.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}
                }
            });
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            uriFoto= getImageUri(Registrarse.this, imageBitmap);
            imageButton.setImageBitmap(imageBitmap);
        }
    }
}
