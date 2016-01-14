package org.ses.android.soap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.AlertDialog;

import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;

import android.view.MotionEvent;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;
import android.util.Log;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.Proyecto;
import org.ses.android.soap.database.Visita;

import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.models.Project;
import org.ses.android.soap.tasks.ProjectLoadTask;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.tasks.VisitaLoadTask;
import org.ses.android.soap.tasks.VisitasListTask;
import org.ses.android.soap.tasks.GenerarVisitaTask;
import org.ses.android.soap.tasks.EstadoENRTask;
import org.ses.android.soap.tasks.EstadoTAMTask;
import org.ses.android.soap.preferences.PreferencesActivity;

//TODO: add scheduled days

public class NewVisitActivity extends BaseActivity {
    private Participant currentParticipant;
    private Visita currentVisit;
    private Visitas currentVisitas;
    public int proyectoLength;
    private SharedPreferences mPreferences;
    private Proyecto currentProyecto;
    String selLocal = "";
    String selProyecto  = "";
    String selGrupo  = "";
    String selVisita  = "";
    EditText timePicker;
    private int participantVisits;
    private String startDay;
    TextView visitLocaleEditor;
    private Visitas[] visitas_array;
    private int num_visitas;
    private Visita[] visita_array;
    private int num_visita;
    private String first_visit = "Does Not Exist";
    private int totalVisits;
    private AsyncTask<String, String, Visitas[]> asyncTask;
    private AsyncTask<String, String, Visitas[]> loadVisitas;
    private AsyncTask<String, String, Visita[]> loadVisita;
    private AsyncTask<String,String,ArrayList<Project>> loadProject;
    private AsyncTask<String, String, String> generarVisita;
    private AsyncTask<String, String, String> loadEstadoENR;
    private AsyncTask<String, String, String> loadEstadoTAM;
    EstadoENRTask estadoENR;
    EstadoTAMTask estadoTAM;
    DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat displayTimeFormat = new SimpleDateFormat("HH:mm");
    DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
    DateFormat dbTimeFormat = new SimpleDateFormat("HH:mm:00.0000000");
    Date visitDate = new Date();
    Date visitTime = new Date();

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    boolean mIs24HourView;

    private String codigoUsuario;
    private String codigoProyecto;
    private TextView tvwProjectName;

    private String localeId;

    private TextView names;
    private TextView local;
    private TextView project;
    private TextView start_date;
    private TextView end_date;
    private Spinner spnGrupo;
    String selGrupo = "";
    private Spinner spnVisita;
    String selVisita = "";
    String selProyecto = "";

    private TextView visit_date;
    private TextView visit_time;
    private Button btn_save_visit;

    private static final int FIRST_VISIT = 2; // 3rd visit == first real visit
    static final int DATE_DIALOG_ID = 1;
    static final int TIME_DIALOG_ID = 2;

    String url = "";

    GenerarVisitaTask tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_visit_layout);

        names = (TextView) findViewById(R.id.txt_names);
        local = (TextView) findViewById(R.id.txt_local);
        project = (TextView) findViewById(R.id.txt_project);
        start_date = (TextView) findViewById(R.id.txt_start_date);
        end_date = (TextView) findViewById(R.id.txt_end_date);
        spnGrupo = (Spinner) findViewById(R.id.spn_visit_Grupo);
        spnVisita = (Spinner) findViewById(R.id.spnVisita);
        visit_date = (TextView) findViewById(R.id.visit_date);
        visit_time = (TextView) findViewById(R.id.visit_time);
        tvwProjectName = (TextView) findViewById(R.id.tvwProjectName);

        btn_save_visit = (Button) findViewById(R.id.btn_save_visit);

        setCurrentDateOnView();
        addListenerOnVisitDate();
        setCurrentTimeOnView();
        addListenerOnVisitTime();

        btn_save_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertaGuardar();
            }
        });

        url = StringConexion.conexion;

        // get localeId, localeName and promoterId from sharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String localeName = prefs.getString((getString(R.string.login_locale_name)), null);
        local.setText(localeName);
        localeId = prefs.getString((getString(R.string.login_locale)), null);
        String promoterId = prefs.getString((getString(R.string.key_userid)), null);

        /**
         * if a patient was passed in, pre-load that patient
         */
        currentParticipant = (Participant) getIntent().getParcelableExtra("Participant");

        String fullName = currentParticipant.Nombres + " " +
                currentParticipant.ApellidoMaterno + " " +
                currentParticipant.ApellidoPaterno;
        names.setText(fullName);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        codigoUsuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        codigoProyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");
        String local_id = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
        int intLocal = Integer.valueOf(local_id);
        selLocal = Integer.toString(intLocal);

        String project_name = mPreferences.getString(PreferencesActivity.KEY_PROJECT_NAME, "");
        tvwProjectName.setText(project_name);
        String project_id = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");
        int intProject = Integer.valueOf(project_id);
        selProyecto = Integer.toString(intProject);


        // get project name
        ProjectLoadTask tareaProjects = new ProjectLoadTask();

        loadProject = tareaProjects.execute(localeId, "bogusurl");
        try {
            ArrayList<Project> project_array = loadProject.get();
            if (project_array != null) {
                for (int i = 0; i < project_array.size(); i++) {
                    Project temp = project_array.get(i);
                    if (String.valueOf(temp.id).equals(codigoProyecto)) {
                        project.setText(temp.name);
                    }
                }
            }
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();

        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }
        /*
         *counts how many visits the Patient has already done
        */

        /**
         * Terminology note:
         * - Visita is a visit group (diagnosis "TAM", enrollment "ENR", actual treatment "SIG")
         * - Visitas is an actual visit
         */


        /*
        *start day of the treatment + total time of treatment = end day
        * Use patient's current day to determine how many days left
        *
         */

        /*
         * # of patient visits so far
         */
        VisitasListTask tareaVisits = new VisitasListTask();

        loadVisitas = tareaVisits.execute(currentParticipant.CodigoPaciente, codigoUsuario, codigoProyecto, "bogusurl");
        try {
            visitas_array = loadVisitas.get();
            if (visitas_array != null) {
                num_visitas = visitas_array.length;
                Log.d("myactivity0", "number of visits already: " + num_visitas);
                // find date of first treatment
                // 77985806
                if (num_visitas > 2) {
                    for (int i = 0; i < num_visitas; i++) {
                        Visitas temp = visitas_array[i];
                        if (temp.CodigoGrupoVisita.equals("3") && temp.CodigoVisita.equals("1")) {
                            first_visit = temp.FechaVisita;
                        }

                    }
                }
            }
            Log.d("myactivity1", "number of visits already: " + num_visitas);
        } catch (InterruptedException e1) {
            e1.printStackTrace();

        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        start_date.setText(first_visit);

        VisitaLoadTask tareaVisit = new VisitaLoadTask();

        loadVisita = tareaVisit.execute(currentParticipant.CodigoPaciente, codigoUsuario, codigoProyecto, "bogusurl");
        try {
            visita_array = loadVisita.get(); //Visit
            if (visita_array != null) {
                num_visita = visita_array.length; //total number of visits in a project
            }
            Log.d("myactivit2", "number of visits total: " + num_visita);
            Log.d("myactivity3", "number of visits already: " + num_visitas);
        } catch (InterruptedException e1) {
            e1.printStackTrace();

        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        loadVisitaSpinner(currentParticipant.CodigoPaciente, localeId, codigoProyecto);
        spnGrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        selGrupo = parent.getItemAtPosition(position).toString().substring(0, 1);
//					if (!selGrupo.equals("0")) loadProyectoSpinner(selGrupo);
                        Log.i("Visita", "Grupo: pos: " + selGrupo + " valor:" + parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getBaseContext(), "Seleccione un Local!!", Toast.LENGTH_SHORT).show();
                    }
                });

        visita.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        // JT:20150817
                        //selVisita = parent.getItemAtPosition(position).toString().substring(0,1);
                        selVisita = parent.getItemAtPosition(position).toString();
                        selVisita = selVisita.substring(0, selVisita.indexOf("-", 0) - 1).trim();

//					if (!selVisita.equals("0")) loadProyectoSpinner(selVisita);
                        Log.i("Visita", "Visita: pos: " + selVisita + " valor:" + parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getBaseContext(), "Seleccione un Local!!", Toast.LENGTH_SHORT).show();
                    }
                });


        /*
        *   populate the spinners
        *   First spinner: Visit Grupo (TAM, ENR, SIG)
         */
        /* ArrayList visitgrupoSpnList = new ArrayList<String>();
        visitgrupoSpnList.add("Tamizaje"); // 1
        visitgrupoSpnList.add("Enrolamiento"); // 2
        visitgrupoSpnList.add("Siguiente"); // 3
        visitgrupoSpnList.add("Visita No Programada 1"); //not sure what this is, but I'm keeping it. 0

        ArrayAdapter<String> visitasSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, visitgrupoSpnList);

        visitasSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnGrupo.setAdapter(visitasSpinnerAdapter); */


        /*
        *
        *   Second spinner: Visits (V1, V2, V3)
        *   Set from current visit on. So if the patient has already been through 5 visits, start at 6.
        *   num_visitas is the number of visits the patient has done already.
        *   num_visita is the number of visits in a project
         */
        /* Log.d("myactivity", "number of visits already: " + num_visitas);
        Log.d("myactivity", "number of visits total: " + num_visita);
        ArrayList visitaSpnList = new ArrayList<String>();
        if (num_visitas < num_visita) {
            for (int i = num_visitas; i < num_visita; i++) {
                visitaSpnList.add("V" + i);

            }

        }
        else visitaSpnList.add("Done!");
        Log.d("myactivity", "done");

        ArrayAdapter<String> visitaSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, visitaSpnList);

        visitaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnVisita.setAdapter(visitaSpinnerAdapter); */



    }

    public void setCurrentDateOnView() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        visit_date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month + 1).append("/")
                .append(year));

    }

    public void setCurrentTimeOnView() {
        final Calendar c = Calendar.getInstance();

        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // set current time into textview
        visit_time.setText(hour + ":" + minute);
    }

    public void addListenerOnVisitDate() {

        visit_date.setOnTouchListener(new View.OnTouchListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(DATE_DIALOG_ID);
                return true;
            }


        });
        visit_date.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });

    }

    public void addListenerOnVisitTime() {
        visit_time.setOnTouchListener(new View.OnTouchListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(TIME_DIALOG_ID);
                return true;
            }


        });
        visit_time.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                showDialog(TIME_DIALOG_ID);

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
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, timePickerListener, hour, minute, mIs24HourView);
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
            visit_date.setText(day + "/" + (month + 1) + ("/") + (year));

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener
            = new TimePickerDialog.OnTimeSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onTimeSet(TimePicker view, int selectedHour,
                              int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;

            // set selected date into textview
            visit_time.setText(hour + ":" + minute);

        }
    };

    //on Siguiente
    public  void  AlertaGuardar() {
        final String fec_visita = visit_date.getText().toString();
        final String hora_visita = visit_time.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Desea Generar Visita?")
                .setTitle("Advertencia")
                .setCancelable(false)
                .setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                GenerarVisitaTask tarea = new GenerarVisitaTask();
                                //int CodigoLocal, int CodigoProyecto, int CodigoVisita, string CodigoPaciente, string FechaVisita, string HoraCita, int CodigoUsuario
                                generarVisita = tarea.execute(
                                        localeId,
                                        codigoProyecto,
                                        selGrupo,
                                        selVisita,
                                        currentParticipant.CodigoPaciente,
                                        fec_visita,
                                        hora_visita,
                                        codigoUsuario,
                                        url);

                                String guardado;
                                String estadoENR;
                                String estadoTAM;
                                try {


                                    guardado = generarVisita.get();
                                    if (!guardado.equals("OK")) {
                                        Toast.makeText(getBaseContext(), "No se creo visita!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "Datos guardados!!", Toast.LENGTH_SHORT).show();
                                        EstadoENRTask tareaEstadoENR = new EstadoENRTask();
                                        EstadoTAMTask tareaEstadoTAM = new EstadoTAMTask();
                                        loadEstadoENR = tareaEstadoENR.execute("ENR", selProyecto, url);
                                        estadoENR = loadEstadoENR.get();
                                        loadEstadoTAM = tareaEstadoTAM.execute("TAM", selProyecto, url);
                                        estadoTAM = loadEstadoTAM.get();
                                        //selGrupo=1 TAM, selGrupo=2  ENR
                                        Log.i("Visita", "estadoENR: " + estadoENR.toString() + "--- estadoTAM: " + estadoTAM.toString());
//                                                                            if (estadoENR.equals("1") || estadoTAM.equals("1")){
//                                                                                // Asignar IDs de acuerdo al tipo de spnVisita (TAM o ENR)
//                                                                                if ((selGrupo.equals("1") || selGrupo.equals("2"))  && selVisita.equals("1")) {
//                                                                                    Intent pass = new Intent(getApplicationContext(),ParticipanteAsignarIdActivity.class);
//                                                                                    Bundle extras = new Bundle();
//                                                                                    extras.putString("selLocal", selLocal);
//                                                                                    extras.putString("selProyecto", selProyecto);
//                                                                                    extras.putString("codigopaciente",codigopaciente);
//                                                                                    extras.putString("selGrupo", selGrupo);
//                                                                                    extras.putString("selVisita",selVisita);
//                                                                                    extras.putString("codigousuario",codigousuario);
//                                                                                    extras.putString("url",url);
//                                                                                    pass.putExtras(extras);
//                                                                                    startActivity(pass);
//                                                                                }
//                                                                            }
                                        Boolean asignarID = false;
                                        if (estadoTAM.equals("1") && estadoENR.equals("1")) {
                                            if ((selGrupo.equals("1") || selGrupo.equals("2")) && selVisita.equals("1")) {
                                                asignarID = true;
                                            }
                                        }
                                        //   if (estadoTAM.equals("0") && estadoENR.equals("1")){
                                        //     if ((selGrupo.equals("2"))  && selVisita.equals("1")) {
                                        //            asignarID = true;
                                        //     }
                                        // }
                                        if (estadoTAM.equals("1") && estadoENR.equals("0")) {
                                            if ((selGrupo.equals("1") || selGrupo.equals("2")) && selVisita.equals("1")) {
                                                asignarID = true;
                                            }
                                        }
                                        if (asignarID.equals(true)) {
                                            // Asignar IDs de acuerdo al tipo de spnVisita (TAM o ENR)
                                            //if ((selGrupo.equals("1") || selGrupo.equals("2"))  && selVisita.equals("1")) {
                                            /* Intent pass = new Intent(getApplicationContext(),ParticipanteAsignarIdActivity.class);
                                            Bundle extras = new Bundle();
                                            extras.putString("selLocal", localeId);
                                            extras.putString("selProyecto", selProyecto);
                                            extras.putString("codigopaciente",currentParticipant.CodigoPaciente);
                                            extras.putString("selGrupo", selGrupo);
                                            extras.putString("selVisita",selVisita);
                                            extras.putString("codigousuario",codigoUsuario);
                                            extras.putString("url",url);
                                            extras.putString("estadoTAM",estadoTAM);
                                            extras.putString("estadoENR", estadoENR);
                                            extras.putInt("validar_emr",0);

                                            pass.putExtras(extras);
                                            startActivity(pass); */
                                            //}
                                        }
                                    }
                                    finish();
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }
    // every loadVisit used to be loadVisita
    // currentParticipant.CodigoPaciente was just codigopaciente
    public void loadVisitaSpinner(String codigopaciente,String local,String proyecto){
        VisitaLoadTask tareaVisita = new VisitaLoadTask();

        loadVisita = tareaVisita.execute(codigopaciente,local,proyecto,"bogusurl");
        Visita[] objVisita;
        String[] wee,wee1,empty;
        empty = new String[0];
        ArrayAdapter<String> emptyArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, empty);
        emptyArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spnGrupo.setAdapter(emptyArrayAdapter);
        spnVisita.setAdapter(emptyArrayAdapter);

        try {

            objVisita = loadVisita.get();
            if (objVisita != null){
                wee = new String[objVisita.length];
                wee1 = new String[objVisita.length];
                for(int i = 0;i < objVisita.length; i++){
                    wee[i]= String.valueOf(objVisita[i].CodigoGrupoVisita) +" - "+objVisita[i].NombreGrupoVisita;
                    wee1[i]= String.valueOf(objVisita[i].CodigoVisita) +" - "+objVisita[i].DescripcionVisita;
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, wee);
                spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                spnGrupo.setAdapter(spinnerArrayAdapter);
                ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, wee1);
                spinnerArrayAdapter1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
                spnVisita.setAdapter(spinnerArrayAdapter1);

                Log.i("Visita","Visita Array");
            }


        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

}





