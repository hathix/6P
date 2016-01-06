package org.ses.android.soap.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.opengl.GLException;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.Menu_principal;
import org.ses.android.soap.tasks.StringConexion;

public class CambioServer extends Activity {

     Button btnGuardar  ;
     Button  btnSalir ;
    EditText txtServer;
    //Context context = this;
    Context  context= this;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_server);
        btnGuardar = (Button)findViewById(R.id.btnGuardarServer);
        btnSalir =(Button)findViewById(R.id.btnSalirC);
        txtServer = (EditText)findViewById(R.id.txtUrlServ);
        txtServer.setEnabled(true);
        btnGuardar.setEnabled(false);
        Drawable  d =getResources().getDrawable(R.drawable.boton_inactivo);
        btnGuardar.setBackground(d);


        txtServer.setText(StringConexion.conexion);
        StringConexion.ifExistCreateFile();


         btnSalir.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent ig = new Intent(context, Menu_principal.class);
                 startActivity(ig);
             }
         });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertCambios();


            }


        });





        txtServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //final StringConexion global = (StringConexion)getApplicationContext();

                 if (txtServer.getText().toString()==StringConexion.conexion){

                     Drawable  d =getResources().getDrawable(R.drawable.boton_inactivo);
                     btnGuardar.setBackground(d);
                     btnGuardar.setEnabled(false);

                 }
                else {
                     btnGuardar.setEnabled(true);
                     btnGuardar.setBackground(getResources().getDrawable(R.drawable.botonrojo));
                 }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public  void  AlertCambios(){

        AlertDialog.Builder alertdialobuilder = new AlertDialog.Builder(context);
        alertdialobuilder.setTitle("Aviso");

        alertdialobuilder.setMessage("Desea realizar los cambios?");
        alertdialobuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUrl = txtServer.getText().toString();

                  StringConexion.SetConexion(newUrl);




                btnGuardar.setEnabled(false);
                btnGuardar.setBackground(getResources().getDrawable(R.drawable.boton_inactivo));
                Toast.makeText(context, "Cambios relaizados", Toast.LENGTH_LONG).show();

            }
        });

        alertdialobuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alertdialobuilder.setIcon(R.drawable.alert_64);
        AlertDialog alertDialog = alertdialobuilder.create();

        alertDialog.show();


    }








}
