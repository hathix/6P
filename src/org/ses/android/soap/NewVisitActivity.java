package org.ses.android.soap;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;


import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.Proyecto;
import org.ses.android.soap.database.Visita;

import java.util.Calendar;
//import org.ses.android.soap.utils.DatePickerFragment;
//import org.ses.android.soap.utils.TimePickerFragment;
// TimePicker.class and TimePickerDialog.class

// import org.ses.android.soap.database.Schedule; // ??? Can't find Project length
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.models.Project;
import org.ses.android.soap.tasks.ProjectLoadTask;
import org.ses.android.soap.tasks.VisitaLoadTask;
//import org.ses.android.soap.utils.DatePickerFragment;
//import org.ses.android.soap.utils.TimePickerFragment;
//import org.ses.android.soap.tasks.NewVisitUploadTask;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.VisitasListTask;
import org.ses.android.soap.tasks.ProjectLoadTask;


//TODO: add scheduled days

public class NewVisitActivity extends Activity {
    private Participant currentParticipant;
    private Visita currentVisit;
    private Visitas currentVisitas;
    public int proyectoLength;
    private SharedPreferences mPreferences;
    private Proyecto currentProyecto;

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
    private AsyncTask<String, String, Visita[]> loadVisit;
    private AsyncTask<String,String,ArrayList<Project>> loadProject;
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


    private TextView names;
    private TextView local;
    private TextView project;
    private TextView start_date;
    private TextView end_date;
    private Spinner visit_grupo;
    private Spinner visita;

    private TextView visit_date;
    private Button btn_save_visit;

    private static final int FIRST_VISIT = 2; // 3rd visit == first real visit
    static final int DATE_DIALOG_ID = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_visit_layout);

        names = (TextView) findViewById(R.id.txt_names);
        local = (TextView) findViewById(R.id.txt_local);
        project = (TextView) findViewById(R.id.txt_project);
        start_date = (TextView) findViewById(R.id.txt_start_date);
        end_date = (TextView) findViewById(R.id.txt_end_date);
        visit_grupo = (Spinner) findViewById(R.id.spn_visit_Grupo);
        visita = (Spinner) findViewById(R.id.spnVisita);
        visit_date = (TextView) findViewById(R.id.visit_date);

        btn_save_visit = (Button) findViewById(R.id.btn_save_visit);

        setCurrentDateOnView();
        addListenerOnVisitDate();

        // get localeId, localeName and promoterId from sharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String localeName = prefs.getString((getString(R.string.login_locale_name)), null);
        local.setText(localeName);
        String localeId = prefs.getString((getString(R.string.login_locale)), null);
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
        String codigoUsuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        String codigoProyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");

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

        VisitaLoadTask tareaVisit = new VisitaLoadTask();

        loadVisit = tareaVisit.execute(currentParticipant.CodigoPaciente, codigoUsuario, codigoProyecto, "bogusurl");
        try {
            visita_array = loadVisit.get(); //Visit
            if (visita_array != null) {
                num_visita = visita_array.length; //total number of visits in a project
            }

        } catch (InterruptedException e1) {
            e1.printStackTrace();

        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        /*
        *   populate the spinners
        *   First spinner: Visit Grupo (TAM, ENR, SIG)
         */
        ArrayList visitgrupoSpnList = new ArrayList<String>();
        visitgrupoSpnList.add("Tamizaje"); // 1
        visitgrupoSpnList.add("Enrolamiento"); // 2
        visitgrupoSpnList.add("Siguiente"); // 3
        visitgrupoSpnList.add("Visita No Programada 1"); //not sure what this is, but I'm keeping it. 0

        ArrayAdapter<String> visitasSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, visitgrupoSpnList);

        visitasSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        visit_grupo.setAdapter(visitasSpinnerAdapter);


        /*
        *
        *   Second spinner: Visits (V1, V2, V3)
        *   Set from current visit on. So if the patient has already been through 5 visits, start at 6.
         */
        ArrayList visitaSpnList = new ArrayList<String>();
        if (num_visitas < num_visita) {
            for (int i = num_visitas; i < num_visita; i++) {
                visitaSpnList.add("V" + i);
                Log.d("Debug", "number of visits already: " + num_visitas);
            }

        }
        else visitaSpnList.add("Done!");

        ArrayAdapter<String> visitaSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, visitaSpnList);

        visitaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        visita.setAdapter(visitaSpinnerAdapter);



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

        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);


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
            visit_date.setText(day + "/" + (month + 1) + ("/") + (year));

        }
    };

}





