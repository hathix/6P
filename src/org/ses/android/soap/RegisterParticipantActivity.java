package org.ses.android.soap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.utils.UrlUtils;
import org.ses.android.soap.tasks.RegistrarParticipanteTask;
import org.ses.android.soap.tasks.ObtenerIdPacienteTask;
import org.ses.android.soap.database.Participant;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.AlertDialog;

import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.MotionEvent;

import org.ses.android.soap.utils.GPSTracker;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.models.Depart;
import org.ses.android.soap.models.Distrit;
import org.ses.android.soap.models.Prov;
import org.ses.android.soap.tasks.LoadDepartTask;
import org.ses.android.soap.tasks.LoadDsitritoTask;
import org.ses.android.soap.tasks.LoadProvincias;
import org.ses.android.soap.utils.InternetConnection;
import org.ses.android.soap.tasks.RegistrarPacienteContacto;
import org.ses.android.soap.preferences.PreferencesActivity;

import java.util.concurrent.ExecutionException;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;

import android.location.Address;
import android.location.Geocoder;

import android.preference.PreferenceManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by fanneyzhu on 1/12/16.
 */
public class RegisterParticipantActivity extends BaseActivity {

    private EditText edt_dni_document;
    private EditText edt_first_name;
    private EditText edt_maternal_name;
    private EditText edt_paternal_name;
    private TextView edt_dob;
    private int year;
    private int month;
    private int day;
    private RadioGroup rbg_gender;

    String dni,nombres,ape_pat,ape_mat,fec_nacimiento,sexo,url;
    String domicilio,referencia,telefono,celular,longitude,latitude;

    private Spinner sp_department;
    private Spinner sp_province;
    private Spinner sp_district;
    String  cod_Provincia ;
    String  cod_Distrito;

    private EditText edt_domicilio;
    private EditText edt_referencia;
    private EditText edt_telefono;
    private EditText edt_celular;
    private EditText edt_localizac;
    private EditText edt_long;
    private EditText edt_lat;
    private ImageButton btn_gps;
    private GPSTracker gpsTracker;
    private  AsyncTask<String,String,String> regPaciente;
    Bundle bundle;
    String var_Ubigeo = "";
    String pac_id = "";
    Context context = this;

    private Button btn_guardar;

    private AsyncTask<String, String, String> asyncTask;
    private SharedPreferences mPreferences;

    RegistrarParticipanteTask tarea_registrar;
    private AsyncTask<String, String, String> registrarParticipante;
    private  AsyncTask<String,String,String> getIdPaciente;
    String tip_doc= "2";

    private Participant participant;

    static final int DATE_DIALOG_ID = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_participant_layout);

        edt_dni_document = (EditText)findViewById(R.id.edt_dni_document);
        // if we can get a dni field from intent, set that as the default dni
        try {
            edt_dni_document.setText(getIntent().getStringExtra("dni"));
        }
        catch (Exception e)
        {
            // do nothing since no no dni was passed
        }


        edt_first_name = (EditText)findViewById(R.id.edt_first_name);
        edt_maternal_name = (EditText)findViewById(R.id.edt_maternal_name);
        edt_paternal_name = (EditText)findViewById(R.id.edt_paternal_name);

        edt_dob = (TextView)findViewById(R.id.edt_dob);
        setCurrentDateOnView();
        addListenerOntvwfecha_nacimiento();

        rbg_gender = (RadioGroup)findViewById(R.id.rbg_gender);
        rbg_gender.clearCheck();
        rbg_gender.check(R.id.rbg_gender);

        rbg_gender.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        			lblMensaje.setText("ID opcion seleccionada: " + checkedid);
                        Log.i("rgb_gender", "ID opcion seleccionada: " + Integer.toString(checkedId));
                        Log.i("rgb_gender", "getCheckedRadioButtonId: " + group.getCheckedRadioButtonId());

//        			Toast.makeText(getBaseContext(), "ID opcion seleccionada!!"+ Integer.toString(checkedId),Toast.LENGTH_SHORT).show();
                    }
                }
        );

        sp_department = (Spinner)findViewById(R.id.sp_department);
        sp_province = (Spinner)findViewById(R.id.sp_province);
        sp_district = (Spinner)findViewById(R.id.sp_district);

        edt_domicilio = (EditText)findViewById(R.id.edt_domicilio);
        edt_referencia = (EditText)findViewById(R.id.edt_referencia);
        edt_telefono = (EditText)findViewById(R.id.edt_telefono);
        edt_celular = (EditText)findViewById(R.id.edt_celular);
        edt_localizac = (EditText)findViewById(R.id.edt_localizac);
        btn_gps = (ImageButton)findViewById(R.id.btn_gps);

        edt_long = (EditText)findViewById(R.id.edt_long);
        edt_lat = (EditText)findViewById(R.id.edt_lat);
        edt_long.setEnabled(false);
        edt_lat.setEnabled(false);
        final Intent intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null)
            pac_id = bundle.getString("id_pact");
        final String myurl = StringConexion.conexion;

        btn_guardar = (Button)findViewById(R.id.btn_guardar);

        // load districts
        LoadaDepar(myurl);
        sp_district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ObtenerObigeoGen(cod_Distrito, String.valueOf(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        // load provinces
        sp_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        // load departments
        sp_department.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GPSTracker(getApplicationContext());
                String lat = String.valueOf(gpsTracker.getLatitude());
                String Long = String.valueOf(gpsTracker.getLongitude());
                edt_localizac.setText(NombreLocalizacion(gpsTracker.getLongitude(), gpsTracker.getLatitude()));
                edt_lat.setText(lat);
                edt_long.setText(Long);
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyDNI() == true)
                    if (verifyInfo() == true)
                        AlertaGuardar();
            }
        });
    }

    public void setCurrentDateOnView() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        edt_dob.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month + 1).append("/")
                .append(year));

    }

    public void addListenerOntvwfecha_nacimiento() {

        edt_dob.setOnTouchListener(new OnTouchListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(DATE_DIALOG_ID);
                return true;
            }


        });
        edt_dob.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            edt_dob.setText(day + "/" + (month + 1) + ("/") + (year));

        }
    };

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
            sp_department = (Spinner)findViewById(R.id.sp_department);
            sp_department.setAdapter(spinnerArrayAdapter);
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

    public  void LoadDistrito(String cod,String url){
        Distrit distrit =  new Distrit();
        LoadDsitritoTask loadDsitritoTask = new LoadDsitritoTask() ;
        AsyncTask loadDistr;
        ArrayList<Distrit> arrDistri = null;
        String[] distritos;
        boolean connected = InternetConnection.checkConnection(this);
        if (connected)
            try {

                loadDistr = loadDsitritoTask.execute(cod, url);
                arrDistri=(ArrayList<Distrit>)loadDistr.get();
                distritos = Distrit.ConvertLocalObjsToStrings(arrDistri);
                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                        this,android.R.layout.simple_spinner_item,distritos
                );
                sp_district=(Spinner)findViewById(R.id.sp_district);
                sp_district.setAdapter(stringArrayAdapter);

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
                sp_province=(Spinner)findViewById(R.id.sp_province);
                sp_province.setAdapter(stringArrayAdapter);

            }
            catch (InterruptedException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", "Interrupted Exception");
            } catch (ExecutionException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", "Execution Exception");
            } catch (NullPointerException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", " NullPointerException");
            }
    }

    //Tarea Asincrona para llamar al WS de consulta en segundo plano
    private class ExisteParticipante extends AsyncTask<String,String,String>  {

        @Override
        protected String doInBackground(String... params) {

            String existe = "no";
            String resp;
            String urlserver = params[1];
            final String NAMESPACE = StringConexion.conexion;
            final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
            final String METHOD_NAME = "ExisteParticipante";
            final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
            // para usar en la busqueda del DNI del Participante

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("DocIdentidad", params[0]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            transporte.debug = true;
            try
            {
                transporte.call(SOAP_ACTION, envelope);
                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                resp = resultado_xml.toString();
                Log.i("DNI", "doInBackground_resp:" + resp);

                if (resp.equals("1")){
                    existe = "si";
                }

            }
            catch (Exception e)
            {
                existe = "no";
            }

            return existe;
        }

    }

    // verify that the participant entered a valid DNI
    public boolean verifyDNI() {

        ExisteParticipante tarea_existe = new ExisteParticipante();

        try {

            mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String doc_identidad = edt_dni_document.getText().toString();

            if (doc_identidad.length() > 0){
                if (!UrlUtils.validData(doc_identidad, "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]")){
                    Toast.makeText(getBaseContext(), getString(R.string.invalid_dni),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                            getString(R.string.default_server_url));
                    asyncTask=tarea_existe.execute(doc_identidad,url);
                    String existe = asyncTask.get();
                    Log.i("doc_identidad",doc_identidad );
                    if (existe.equals("si")){
                        Toast.makeText(getBaseContext(), getString(R.string.dni_already_exists),Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    else{
                        //Guardamos el mensaje personalizado en las preferencias

                        Editor editor = mPreferences.edit();
                        editor.putString("doc_identidad", doc_identidad);

                        editor.commit();
                        return true;
                    }

                }

            }
            // no dni/document
            else
            {
                Editor editor = mPreferences.edit();
                editor.putString("doc_identidad", doc_identidad);

                editor.commit();
                return true;
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    // verify that the participant filled in names, dob, and gender
    public boolean verifyInfo() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        url = StringConexion.conexion;
        Log.i("URL",url);
        dni = mPreferences.getString("doc_identidad", "");

        if (dni.equals("")) tip_doc = "1";

        Log.i("Datos_DNI",dni);

        nombres = edt_first_name.getText().toString();
        ape_pat = edt_paternal_name.getText().toString();
        ape_mat = edt_maternal_name.getText().toString();
        sexo ="";

        int idSexo= rbg_gender.getCheckedRadioButtonId();

        Log.i("onSiguiente_rgbSexo","ID opcion seleccionada: " + Integer.toString(idSexo));
        Log.i("onSiguiente_rboMasculino","R.id.rboMasculino:" + String.valueOf(R.id.rbo_male));
        Log.i("onSiguiente_rboFemenino","R.id.rboFemenino:" + String.valueOf(R.id.rbo_female));
        if (idSexo== R.id.rbo_male)	sexo="1";
        if (idSexo==R.id.rbo_female) sexo="2";
        if (idSexo==-1) sexo="";
        Log.i("onSiguiente sexo:",sexo);

        fec_nacimiento = edt_dob.getText().toString();
        Log.i("fec_nacimiento:",fec_nacimiento);

        //ok?: "[a-zA-Z]+\\.?"  , Unicode: "^\pL+[\pL\pZ\pP]{0,}$",OK:"^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}"
        String regExp="^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}";

        if (!UrlUtils.validData(nombres, regExp)){
            Toast.makeText(getBaseContext(), getString(R.string.invalid_name),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!UrlUtils.validData(ape_pat, regExp)){
            Toast.makeText(getBaseContext(), getString(R.string.invalid_paternal_last),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!UrlUtils.validData(ape_mat, regExp)){
            Toast.makeText(getBaseContext(), getString(R.string.invalid_maternal_last),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sexo.equals("")){
            Toast.makeText(getBaseContext(), getString(R.string.choose_gender),Toast.LENGTH_SHORT).show();
            return false;
        }
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        StringBuilder sb = new StringBuilder()
                .append(day).append("/").append(month + 1).append("/")
                .append(year);
        String fec_hoy = sb.toString();
        long elapsedDays = UrlUtils.daysBetween(fec_nacimiento, fec_hoy);
        String[] hoy = fec_hoy.split("/");
        String[] birthdate = fec_nacimiento.split("/");
        int[] hoy_ints = new int[3];
        int[] birthdate_ints = new int[3];
        for (int i = 0; i < hoy.length; i++) {
            hoy_ints[i] = Integer.parseInt(hoy[i]);
            birthdate_ints[i] = Integer.parseInt(birthdate[i]);
        };

        if (elapsedDays == 0  || elapsedDays > 10){
            Toast.makeText(getBaseContext(), getString(R.string.invalid_dob),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (hoy_ints[2] == birthdate_ints[2] && hoy_ints[1] <= birthdate_ints[1] &&
                        hoy_ints[0] <= birthdate_ints[0]) {
            Toast.makeText(getBaseContext(), getString(R.string.invalid_dob),Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    // alert dialog to confirm
    public  void  AlertaGuardar(){

        AlertDialog.Builder alertdialobuilder = new AlertDialog.Builder(context);
        alertdialobuilder.setTitle("Aviso");

        alertdialobuilder.setMessage("Desea guardar la informacion?");
        alertdialobuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                tarea_registrar = new RegistrarParticipanteTask();
                registrarParticipante = tarea_registrar.execute(dni, tip_doc, nombres, ape_pat,
                        ape_mat, fec_nacimiento, sexo, url);

                RegistrarPacienteContacto registrarPacienteContactoTask = new RegistrarPacienteContacto();

                domicilio = edt_domicilio.getText().toString();
                referencia = edt_referencia.getText().toString();
                telefono = edt_telefono.getText().toString();
                celular = edt_celular.getText().toString();
                longitude = edt_long.getText().toString();
                latitude = edt_lat.getText().toString();

                regPaciente = registrarPacienteContactoTask.execute(pac_id, var_Ubigeo,
                        domicilio, referencia, telefono, celular, longitude, latitude,
                        StringConexion.conexion);

                try {
                    String msj = regPaciente.get();
                    ObtenerIdPacienteTask  obtenerIdPacienteTask = new ObtenerIdPacienteTask();
                    getIdPaciente= obtenerIdPacienteTask.execute(nombres,ape_pat,ape_mat,fec_nacimiento,url);
                    String id_pac = getIdPaciente.get();
                    Toast.makeText(getApplicationContext(), getString(R.string.registration_success), Toast.LENGTH_LONG).show();

                    Participant participant = new Participant(id_pac,nombres,ape_pat,ape_mat,
                            Integer.parseInt(tip_doc),dni,fec_nacimiento,Integer.parseInt(sexo));
                    // participant fingerprint was never taken
                    if (mPreferences.getString("Fingerprint", "notFound").equals("notFound")){
                        Intent i = new Intent(getBaseContext(), ParticipantDashboardActivity.class);
                        i.putExtra("Participant", participant);
                        startActivity(i);
                    }
                    // participant logged fingerprint before registering
                    else {
                        Intent i = new Intent(context, FingerprintConfirmActivity.class);
                        i.putExtra("Participant", participant);
                        startActivity(i);
                    }

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
            Toast.makeText(getApplicationContext(),getString(R.string.check_gps),Toast.LENGTH_LONG).show();

        }

        return  addressStr;
    }

}
