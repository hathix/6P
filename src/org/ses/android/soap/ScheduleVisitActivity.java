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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;

import android.util.Log;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.Proyecto;
import org.ses.android.soap.database.Visita;

import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.models.Project;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.tasks.VisitaLoadTask;
import org.ses.android.soap.tasks.VisitasListTask;
import org.ses.android.soap.tasks.GenerarVisitaTask;
import org.ses.android.soap.tasks.EstadoENRTask;
import org.ses.android.soap.tasks.EstadoTAMTask;
import org.ses.android.soap.preferences.PreferencesActivity;


public class ScheduleVisitActivity extends BaseActivity {

    private Participant currentParticipant;
    private SharedPreferences mPreferences;

    private Proyecto currentProyecto;

    private Visitas[] visitas_array;
    private int num_visitas;
    private Visita[] visita_array;
    private int num_visita;
    private String first_visit = "Does Not Exist";
    private int totalVisits;
    private AsyncTask<String, String, Visitas[]> asyncTask;
    private AsyncTask<String, String, Visitas[]> loadVisitas;
    private AsyncTask<String, String, Visita[]> loadVisita;
    private AsyncTask<String, String, ArrayList<Project>> loadProject;
    private AsyncTask<String, String, String> generarVisita;
    private AsyncTask<String, String, String> loadEstadoENR;
    private AsyncTask<String, String, String> loadEstadoTAM;
    EstadoENRTask estadoENR;
    EstadoTAMTask estadoTAM;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    boolean mIs24HourView;


    private TextView names;
    private TextView local;
    private TextView project;
    private TextView start_date;
    private Spinner spnGrupo;
    private Spinner spnVisita;
    private TextView visit_date;
    private TextView visit_time;
    private Button btn_save_visit;

    static final int DATE_DIALOG_ID = 1;
    static final int TIME_DIALOG_ID = 2;


    // variables for GenerarVisitaTask
    private String selectedLocal;
    private String selectedProject;
    private String selectedGroup;
    private String selectedVisitas;
    private String participantId;
    private String visitDate;
    private String visitTime;
    private String userId;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_visit_layout);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        names = (TextView) findViewById(R.id.txt_names);
        local = (TextView) findViewById(R.id.txt_local);
        project = (TextView) findViewById(R.id.txt_project);
        start_date = (TextView) findViewById(R.id.txt_start_date);
        spnGrupo = (Spinner) findViewById(R.id.spn_visit_Grupo);
        spnVisita = (Spinner) findViewById(R.id.spnVisita);
        visit_date = (TextView) findViewById(R.id.visit_date);
        visit_time = (TextView) findViewById(R.id.visit_time);
        btn_save_visit = (Button) findViewById(R.id.btn_save_visit);

        setCurrentDateOnView();
        addListenerOntvwfecha_visita();
        setCurrentTimeOnView();
        addListenerOntvwhora_visita();

        btn_save_visit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertaGuardar();
            }
        });


        //Locale
        String local_name = mPreferences.getString(PreferencesActivity.KEY_LOCAL_NAME, "");
        local.setText(local_name);


        //Project
        String project_name = mPreferences.getString(PreferencesActivity.KEY_PROJECT_NAME, "");
        project.setText(project_name);


        // if a patient was passed in, pre-load that patient
        currentParticipant = getIntent().getParcelableExtra("Participant");
        names.setText(currentParticipant.getFullNameTitleCase());

        // calculate all values for generarVisita

        //        private String selectedLocal;
        //        private String selectedProject;
        //        private String selectedGroup;
        //        private String selectedVisitas;
        //        private String participantId;
        //        private String visitDate;
        //        private String visitTime;
        //        private String userId;
        //        private String url;

        selectedLocal = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
        selectedProject = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");
        // selectedGroup set by spinners
        // selectedVisitas set by spinners
        participantId = currentParticipant.CodigoPaciente;
        // visitDate set when save button hit
        // visitTime set when save button hit
        userId = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        url = StringConexion.conexion;


        /*
         *counts how many visits the Patient has already done
        */

        /**
         * Terminology note:
         * - Visita is a visit group (diagnosis "TAM", enrollment "ENR", actual treatment "SIG")
         * - Visitas is an actual visit
         */


        /*
         * # of patient visits so far
         */
        VisitasListTask tareaVisits = new VisitasListTask();

        loadVisitas = tareaVisits.execute(participantId, userId, selectedProject, "bogusurl");
        try {
            visitas_array = loadVisitas.get();
            if (visitas_array != null) {
                num_visitas = visitas_array.length;
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
        } catch (InterruptedException e1) {
            e1.printStackTrace();

        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        start_date.setText(first_visit);

        loadGrupoAndVisitaSpinners(participantId, selectedLocal, selectedProject);

        // TODO consider moving these to the load...() method
        spnGrupo.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        selectedGroup = parent.getItemAtPosition(position).toString().substring(0, 1);
//					if (!selectedGroup.equals("0")) loadProyectoSpinner(selectedGroup);
                        Log.i("Visita", "Grupo: pos: " + selectedGroup + " valor:" + parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getBaseContext(), "Seleccione un Local!!", Toast.LENGTH_SHORT).show();
                    }
                });

        spnVisita.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {

                        // the text in the list item will be something like
                        // "3 - V3"
                        // extract the visit number (here, the "3") from the raw item text.
                        String rawItemText = parent.getItemAtPosition(position).toString();
                        int dashIndex = rawItemText.indexOf("-", 0) - 1;
                        int rawVisitas = Integer.valueOf(rawItemText.substring(0, dashIndex).trim());
                        // note that the list item contains the *last* visit number, we need
                        // to increment it to get the *next* visit number
                        int nextVisitas = rawVisitas + 1;
                        selectedVisitas = String.valueOf(nextVisitas);

//					if (!selectedVisitas.equals("0")) loadProyectoSpinner(selectedVisitas);
                        Log.i("Visita", "Visita: pos: " + selectedVisitas + " valor:" + parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getBaseContext(), "Seleccione un Local!!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // display current date
    public void setCurrentDateOnView() {

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        visit_date.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month + 1).append("/")
                .append(year));

    }

    public void addListenerOntvwfecha_visita() {
        visit_date.setOnTouchListener(new OnTouchListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(DATE_DIALOG_ID);
                return true;
            }


        });


        visit_date.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });

    }

    // display current date
    public void setCurrentTimeOnView() {


        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        visit_time.setText(
                new StringBuilder().append(pad(hour))
                        .append(":").append(pad(minute)));

    }

    public void addListenerOntvwhora_visita() {
        visit_time.setOnTouchListener(new OnTouchListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(TIME_DIALOG_ID);
                return true;
            }


        });


        visit_time.setOnClickListener(new OnClickListener() {

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
                        year, month, day);
            case TIME_DIALOG_ID:
                // set date picker as current date
                return new TimePickerDialog(this, timePickerListener,
                        hour, minute, true);
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
//			tvDisplayDate.setText(new StringBuilder().append(day)
//			   .append("/").append(month + 1).append("/").append(year)
//			   .append(" "));

            visit_date.setText(new StringBuilder().append(day)
                    .append("/").append(month + 1).append("/").append(year));

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener
            = new TimePickerDialog.OnTimeSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//			String sHour = "0"+String.valueOf(selectedHour);
//			String sMinute = "0"+String.valueOf(selectedMinute);
//			if (sHour.length() > 2) sHour = sHour.substring(1, 2);
////			sHour = sHour.substring(1, 2);
//			if (sMinute.length() > 2) sMinute = sMinute.substring(1, 2);
////			sMinute = sMinute.substring(1, 2);
//
//			tvwhora_visita.setText( sHour + ":" + sMinute);

            hour = selectedHour;
            minute = selectedMinute;

            // set current time into textview
            visit_time.setText(new StringBuilder().append(pad(hour))
                    .append(":").append(pad(minute)));

            // set current time into timepicker
//			timePicker1.setCurrentHour(hour);
//			timePicker1.setCurrentMinute(minute);

        }

    };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    /**
     * Called when the save button is hit.
     */
    public void AlertaGuardar() {

        visitDate = visit_date.getText().toString();
        visitTime = visit_time.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleVisitActivity.this);
        builder.setMessage("Desea Generar Visita?")
                .setTitle("Advertencia")
                .setCancelable(false)
                .setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                generateVisit();
                                Intent intent = new Intent(ScheduleVisitActivity.this, ParticipantDashboardActivity.class);
                                intent.putExtra("Participant", currentParticipant);
                                startActivity(intent);
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

    /**
     * Commits a new visit to the database.
     */
    private void generateVisit() {
        GenerarVisitaTask tarea = new GenerarVisitaTask();

        generarVisita = tarea.execute(
                selectedLocal,
                selectedProject,
                selectedGroup,
                selectedVisitas,
                participantId,
                visitDate,
                visitTime,
                userId,
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
                loadEstadoENR = tareaEstadoENR.execute("ENR", selectedProject, url);
                estadoENR = loadEstadoENR.get();
                loadEstadoTAM = tareaEstadoTAM.execute("TAM", selectedProject, url);
                estadoTAM = loadEstadoTAM.get();
                //selectedGroup=1 TAM, selectedGroup=2  ENR
                Log.i("Visita", "estadoENR: " + estadoENR.toString() + "--- estadoTAM: " + estadoTAM.toString());
//                if (estadoENR.equals("1") || estadoTAM.equals("1")){
//                    // Asignar IDs de acuerdo al tipo de visita (TAM o ENR)
//                    if ((selectedGroup.equals("1") || selectedGroup.equals("2"))  && selectedVisitas.equals("1")) {
//                        Intent pass = new Intent(getApplicationContext(),ParticipanteAsignarIdActivity.class);
//                        Bundle extras = new Bundle();
//                        extras.putString("selectedLocal", selectedLocal);
//                        extras.putString("selectedProject", selectedProject);
//                        extras.putString("participantId",participantId);
//                        extras.putString("selectedGroup", selectedGroup);
//                        extras.putString("selectedVisitas",selectedVisitas);
//                        extras.putString("userId",userId);
//                        extras.putString("url",url);
//                        pass.putExtras(extras);
//                        startActivity(pass);
//                    }
//                }
                Boolean asignarID = false;
                if (estadoTAM.equals("1") && estadoENR.equals("1")) {
                    if ((selectedGroup.equals("1") || selectedGroup.equals("2")) && selectedVisitas.equals("1")) {
                        asignarID = true;
                    }
                }
                //   if (estadoTAM.equals("0") && estadoENR.equals("1")){
                //     if ((selectedGroup.equals("2"))  && selectedVisitas.equals("1")) {
                //            asignarID = true;
                //     }
                // }
                if (estadoTAM.equals("1") && estadoENR.equals("0")) {
                    if ((selectedGroup.equals("1") || selectedGroup.equals("2")) && selectedVisitas.equals("1")) {
                        asignarID = true;
                    }
                }
                if (asignarID.equals(true)) {
                    // Asignar IDs de acuerdo al tipo de visita (TAM o ENR)
                    //if ((selectedGroup.equals("1") || selectedGroup.equals("2"))  && selectedVisitas.equals("1")) {
                    /* Intent pass = new Intent(getApplicationContext(),ParticipanteAsignarIdActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("selectedLocal", selectedLocal);
                    extras.putString("selectedProject", selectedProject);
                    extras.putString("participantId",participantId);
                    extras.putString("selectedGroup", selectedGroup);
                    extras.putString("selectedVisitas",selectedVisitas);
                    extras.putString("userId",userId);
                    extras.putString("url",url);
                    extras.putString("estadoTAM",estadoTAM);
                    extras.putString("estadoENR",estadoENR);
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

    // every loadVisit used to be loadVisita
    // currentParticipant.CodigoPaciente was just participantId

    /**
     * Loads the spinners for the visit groups and visits, populating them with data grabbed
     * from the `VisitaLoadTask`.
     *
     * @param codigopaciente
     * @param local
     * @param proyecto
     */
    public void loadGrupoAndVisitaSpinners(String codigopaciente, String local, String proyecto) {
        VisitaLoadTask tareaVisita = new VisitaLoadTask();

        loadVisita = tareaVisita.execute(codigopaciente, local, proyecto, "bogusurl");
        Visita[] objVisita;
        String[] grupoList, visitaList;

        // start off our spinners empty, then populate later
        // TODO is there a problem with changing the adapter like this??
        String[] empty = new String[0];
        ArrayAdapter<String> emptyArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, empty);
        emptyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGrupo.setAdapter(emptyArrayAdapter);
        spnVisita.setAdapter(emptyArrayAdapter);

        try {

            objVisita = loadVisita.get();
            if (objVisita != null) {
                grupoList = new String[objVisita.length];
                visitaList = new String[objVisita.length];

                for (int i = 0; i < objVisita.length; i++) {
                    grupoList[i] = String.valueOf(objVisita[i].CodigoGrupoVisita) + " - " + objVisita[i].NombreGrupoVisita;
                    visitaList[i] = String.valueOf(objVisita[i].CodigoVisita) + " - " + objVisita[i].DescripcionVisita;
                }

                ArrayAdapter<String> grupoAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, grupoList);
                grupoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnGrupo.setAdapter(grupoAdapter);
                ArrayAdapter<String> visitaAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, visitaList);
                visitaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnVisita.setAdapter(visitaAdapter);

                Log.i("Visita", "Visita Array");
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





