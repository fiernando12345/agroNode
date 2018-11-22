package com.itlm.agronode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spark.submitbutton.SubmitButton;

public class Iniciar_Sesion extends AppCompatActivity {

    private FirebaseAuth Auth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    EditText password,emails;
    SubmitButton logIn, registrate;

    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    private static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 3;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar__sesion);
        setTitle("Bienvenido a AgroNode");
        PedirPermisos();
        password = (EditText) findViewById(R.id.txt_IS_Contraseña);
        emails = (EditText) findViewById(R.id.txt_IS_Correo);
        logIn = (SubmitButton) findViewById(R.id.btn_IS_Iniciar);
        registrate = (SubmitButton) findViewById(R.id.btn_IS_Registrarse);
        saveLoginCheckBox = (CheckBox)findViewById(R.id.chk_IS_Recordar);
        //Verifica si los permisos establecidos se encuentran concedidos
        try {
            loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            loginPrefsEditor = loginPreferences.edit();

            saveLogin = loginPreferences.getBoolean("saveLogin", false);
            if (saveLogin == true) {
                emails.setText(loginPreferences.getString("username", ""));
                password.setText(loginPreferences.getString("password", ""));
                saveLoginCheckBox.setChecked(true);
            }
        }catch (Exception e){Toast.makeText(Iniciar_Sesion.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}

        Auth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    try {
                        Intent intent = new Intent(Iniciar_Sesion.this, Campos.class);
                        startActivity(intent);
                        finish();
                        return;
                    }catch (Exception e){Toast.makeText(Iniciar_Sesion.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}
                }
            }
        };

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emails.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
                    Toast.makeText(Iniciar_Sesion.this, "Complete los campos porfavor", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(emails.getWindowToken(), 0);


                    String mail = emails.getText().toString().trim();
                    String pass = password.getText().toString().trim();

                    if (saveLoginCheckBox.isChecked()) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("username", mail);
                        loginPrefsEditor.putString("password", pass);
                        loginPrefsEditor.commit();
                    } else {
                        loginPrefsEditor.clear();
                        loginPrefsEditor.commit();
                    }
                    try{
                    Auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(Iniciar_Sesion.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(Iniciar_Sesion.this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Iniciar_Sesion.this, "Hubo un error, intente de nuevo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    } catch (Exception e) {
                        Toast.makeText(Iniciar_Sesion.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Iniciar_Sesion.this, Registrarse.class);
                    startActivity(i);
                }catch (Exception e){Toast.makeText(Iniciar_Sesion.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();}
            }
        });
    }

    public void PedirPermisos(){
        if (ActivityCompat.checkSelfPermission(Iniciar_Sesion.this, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Iniciar_Sesion.this, permissions[1]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Iniciar_Sesion.this, permissions[2]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Iniciar_Sesion.this, permissions[3]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Iniciar_Sesion.this, permissions[4]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Iniciar_Sesion.this, permissions[5]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Iniciar_Sesion.this, permissions[6]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Iniciar_Sesion.this, permissions[7]) != PackageManager.PERMISSION_GRANTED) {
            //Si alguno de los permisos no esta concedido lo solicita
            ActivityCompat.requestPermissions(Iniciar_Sesion.this, permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        try{
        super.onStart();
        Auth.addAuthStateListener(firebaseAuthListener);
        } catch (Exception e) {
            Toast.makeText(Iniciar_Sesion.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        try{
        super.onStop();
        Auth.removeAuthStateListener(firebaseAuthListener);
        } catch (Exception e) {
            Toast.makeText(Iniciar_Sesion.this, "Ocurrió un error intentelo de nuevo", Toast.LENGTH_SHORT).show();
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
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Iniciar_Sesion.this);
            dialogo1.setTitle("Bienvenido a AgroNow");
            dialogo1.setMessage("Bienvenido a la aplicación AgroNow");
            dialogo1.setCancelable(true);
            dialogo1.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
