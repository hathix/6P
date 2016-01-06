package org.ses.android.soap.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.Menu_principal;
import org.ses.android.soap.database.Departamento;
import org.ses.android.soap.models.Depart;
import org.ses.android.soap.models.Distrit;
import org.ses.android.soap.models.Locale;
import org.ses.android.soap.models.Prov;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.LoadDepartTask;
import org.ses.android.soap.tasks.LoadDsitritoTask;
import org.ses.android.soap.tasks.LoadProvincias;
import org.ses.android.soap.tasks.LocaleLoadTask;
import org.ses.android.soap.tasks.RegistrarPacienteContacto;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.utils.GPSTracker;
import org.ses.android.soap.utils.InternetConnection;
import org.ses.android.soap.utils.OfflineStorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ParticipanteDatosSecund extends Activity {

    private SharedPreferences mPreferences;


    ImageButton btnGps;
    Spinner   spDepart;
    Spinner  spProv ;
    Spinner spDistrit ;
    Context  context = this;
    EditText EditDomic ;
    EditText EditRefer ;
    EditText EditTelf ;
    EditText EditLong;
    EditText EditLat;
    EditText  EditCel ;
    Button  btnvolv ;
    Button   botGuardar ;
    String  cod_Provincia ;
    String  cod_Distrito;
    GPSTracker gpsTracker ;
    EditText EditGeoLocName;
    private  AsyncTask<String,String,String> regPaciente;
    Bundle   bundle  ;
    String var_Ubigeo = "";
    String  pac_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Datos Secundarios");
        setContentView(R.layout.participante_datos_secund);
        btnGps = (ImageButton)findViewById(R.id.ImgBtnGps);
        Spinner   spDepart= (Spinner)findViewById(R.id.spDepartamento);
        Spinner  spProv = (Spinner)findViewById(R.id.spProvinc);
         spDistrit = (Spinner)findViewById(R.id.spDistri);
        EditDomic = (EditText)findViewById(R.id.txtDomic);
        EditCel = (EditText)findViewById(R.id.txtCelular);
         EditRefer  = (EditText)findViewById(R.id.txtRefer);
         EditTelf =  (EditText)findViewById(R.id.textTelf);
        btnvolv = (Button)findViewById(R.id.btnVolver);

        EditGeoLocName = (EditText)findViewById(R.id.txtGeoLocalN);
           botGuardar = (Button)findViewById(R.id.btnGuardar);
        EditLat =  (EditText)findViewById(R.id.txtLat);
        EditLong = (EditText)findViewById(R.id.txtLong);
        bundle = getIntent().getExtras();
        pac_id = bundle.getString("id_pact");
     //   mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String myurl = StringConexion.conexion;
        EditLat.setEnabled(false);
        EditLong.setEnabled(false);

      //  Toast.makeText(getApplicationContext(),pac_id,Toast.LENGTH_LONG).show();
          LoadaDepar(myurl);
        btnvolv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent  i = new Intent(getApplicationContext(),Menu_principal.class);
                startActivity(i);


            }
        });

         botGuardar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

           AlertaGuardar();


             }
         });

        spDistrit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ObtenerObigeoGen(cod_Distrito,String.valueOf(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {




            }
        });

       // spDepart.setEnabled(true);

        spProv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String cod = getUbigeDistrito(cod_Provincia, String.valueOf(position));
                LoadDistrito(cod, StringConexion.conexion);
                cod_Distrito = cod;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GPSTracker(getApplicationContext());
                 String lat = String.valueOf(gpsTracker.getLatitude());
                String Long = String.valueOf(gpsTracker.getLongitude());
                EditGeoLocName.setText(NombreLocalizacion(gpsTracker.getLongitude(),gpsTracker.getLatitude()));
                EditLat.setText(lat);
                EditLong.setText(Long);
            }
        });

        spDepart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                 int val = position+100;
                String cod = "00" + String.valueOf(val);
                LoadProvincia(cod, StringConexion.conexion);
                cod_Provincia = cod;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*spDepart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadaDepar(myurl);
            }
        });*/
    }



        public  void LoadDistrito(String cod,String url){
            Distrit distrit =  new Distrit();
            LoadDsitritoTask loadDsitritoTask = new LoadDsitritoTask() ;
            AsyncTask loadDistr;
            ArrayList<Distrit> arrDistri = null;
            String[] distritos;
            boolean connected = InternetConnection.checkConnection(this);
            if (connected)
                try {

                    loadDistr = loadDsitritoTask.execute(cod,url);
                    arrDistri=(ArrayList<Distrit>)loadDistr.get();
                    distritos = Distrit.ConvertLocalObjsToStrings(arrDistri);
                    ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                            this,android.R.layout.simple_spinner_item,distritos
                    );
                    spDistrit=(Spinner)findViewById(R.id.spDistri);
                    spDistrit.setAdapter(stringArrayAdapter);

                }
                catch (InterruptedException e1) {
                    Log.e("PromoterLoginActivity: loadLocaleActivity1", "Interrupted Exception");
                } catch (ExecutionException e1) {
                    Log.e("PromoterLoginActivity: loadLocaleActivity1", "Execution Exception");
                } catch (NullPointerException e1) {
                    Log.e("PromoterLoginActivity: loadLocaleActivity1", " NullPointerException");
                }

        }

     public void  LoadProvincia(String cod,String url ){

         Prov prov =  new Prov();
         LoadProvincias loadProvincia = new LoadProvincias() ;
         AsyncTask loadProv;
         ArrayList<Prov> arrProv = null;
         String[] provincias;
         boolean connected = InternetConnection.checkConnection(this);
         if (connected)
             try {

                 loadProv = loadProvincia.execute(cod,url);
                 arrProv=(ArrayList<Prov>)loadProv.get();
                 provincias = Prov.ConvertLocalObjsToStrings(arrProv);
                 ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                         this,android.R.layout.simple_spinner_item,provincias
                 );
                 spProv=(Spinner)findViewById(R.id.spProvinc);
                 spProv.setAdapter(stringArrayAdapter);

             }
             catch (InterruptedException e1) {
        Log.e("PromoterLoginActivity: loadLocaleActivity1", "Interrupted Exception");
          } catch (ExecutionException e1) {
          Log.e("PromoterLoginActivity: loadLocaleActivity1", "Execution Exception");
          } catch (NullPointerException e1) {
              Log.e("PromoterLoginActivity: loadLocaleActivity1", " NullPointerException");
    }

     }

    public  void  LoadaDepar(String url){

        Depart dlp = new Depart();
        LoadDepartTask localeTask = new LoadDepartTask();
        AsyncTask loadDep;
        ArrayList<Depart> arrLocale = null;
        String[] departamentos;
        boolean connected = InternetConnection.checkConnection(this);
        if (connected) try {
            // try server side first
            loadDep = localeTask.execute(url);
            arrLocale = (ArrayList<Depart>) loadDep.get();
            departamentos =Depart.ConvertLocalObjsToStrings(arrLocale);


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, departamentos);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spDepart = (Spinner)findViewById(R.id.spDepartamento);
            spDepart.setAdapter(spinnerArrayAdapter);
           // OfflineStorageManager sm = new OfflineStorageManager(this);

           // sm.SaveArrayListToLocal(arrLocale, this.getString(R.string.locale_filename));

        } catch (InterruptedException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", "Interrupted Exception");
        } catch (ExecutionException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", "Execution Exception");
        } catch (NullPointerException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", " NullPointerException");
        }




    }

    public  String NombreLocalizacion (double longi , double lat)
    {
        String addressStr = "";
     //   Locale currten =  getResources().getConfiguration().L;
        try {


            Geocoder myLocation = new Geocoder(getApplicationContext(), java.util.Locale.getDefault());
            List<Address> myList = myLocation.getFromLocation(lat, longi, 1);
            Address address = (Address) myList.get(0);
             addressStr = "";
            addressStr += address.getAddressLine(0) + ", ";
            addressStr += address.getAddressLine(1) + ", ";
            addressStr += address.getAddressLine(2);

        }

        catch (Exception e){

            addressStr = "Error de localizacion ";
            Toast.makeText(getApplicationContext(),"verificar que su GPS este activo",Toast.LENGTH_LONG).show();

        }


        return  addressStr;
    }

     public  String getUbigeDistrito (String codProv, String pos){

           String resul  =  "";

         if (pos.length()==1){
             resul=codProv+"0"+pos;

         }
         else if (pos.length()>1){
             resul=codProv+pos;

         }

         return  resul;


     }

    public  void  AlertaGuardar(){

        AlertDialog.Builder alertdialobuilder = new AlertDialog.Builder(context);
        alertdialobuilder.setTitle("Aviso");

        alertdialobuilder.setMessage("Desea guardar la informacion?");
        alertdialobuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RegistrarPacienteContacto registrarPacienteContactoTask = new RegistrarPacienteContacto();

                regPaciente = registrarPacienteContactoTask.execute(pac_id, var_Ubigeo, EditDomic.getText().toString(), EditRefer.getText().toString(), EditTelf.getText().toString(), EditCel.getText().toString(), EditLong.getText().toString(), EditLat.getText().toString(), StringConexion.conexion);
                // String msj = "";

                try {
                    String msj = regPaciente.get();
                    Toast.makeText(getApplicationContext(), "Datos Guardados Correctamente", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(context, Menu_principal.class);
                     startActivity(i);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


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


     public void ObtenerObigeoGen(String cod, String pos){

         String resul  =  "";

         if (pos.length()==1){
             var_Ubigeo=cod+"0"+pos;

         }
         else if (pos.length()>1){
             var_Ubigeo=cod+pos;

         }



    }

}
