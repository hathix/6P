package org.ses.android.soap;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.BuscarHuellaTask;
import org.ses.android.soap.tasks.ParticipantLoadTask;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.utils.PreferencesManager;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class FingerprintFindActivity extends FingerprintBaseActivity {

    private ImageView imgFingerprint;
    private Button btnScan;
    private EditText edt_first_name;
    private EditText edt_maternal_name;
    private EditText edt_paternal_name;
    private EditText edt_dni_document;
    private TextView edt_dob;
    private Button btnSearch;

    private AsyncTask<String, String, Participant> asyncTask;
    private SharedPreferences mPreferences;

    private int year, month, day;
    private final int DATE_DIALOG_ID = 999;

    private Participant participant;
    String dni, firstName, paternalLast, maternalLast, dob;

    Boolean changed_dob = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_search_layout);

        imgFingerprint = (ImageView) findViewById(R.id.imgFingerprint);
        btnScan = (Button) findViewById(R.id.btnScan);
        edt_first_name = (EditText) findViewById(R.id.edt_first_name);
        edt_maternal_name = (EditText) findViewById(R.id.edt_maternal_name);
        edt_paternal_name = (EditText) findViewById(R.id.edt_paternal_name);
        edt_dob = (TextView) findViewById(R.id.tvwfecha_nacimiento);
        edt_dni_document = (EditText) findViewById(R.id.edt_dni_document);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        setupScanner();
        imgFingerprint.setImageBitmap(grayBitmap);

        addListenerOntvwfecha_nacimiento();
        setCurrentDateOnView();
        setListeners();

        // Wipe presently stored fingerprint
        PreferencesManager.removeFingerprint(getBaseContext());
    }

    @Override
    public void onPause() {
        imgFingerprint.setImageBitmap(grayBitmap);
        super.onPause();
    }

    public void ScanFingerPrint() {
        byte[] buffer = ScanFingerPrintBase();
        if (buffer != null) {
            // scan successful
            imgFingerprint.setImageBitmap(this.toGrayscale(buffer));
            PreferencesManager.setFingerprint(getBaseContext(), mTemplate);
        }
    }

    public void setCurrentDateOnView() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        edt_dob.setText(day + "/" +
                (month + 1) + "/" +
                year);
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
            edt_dob.setText(new StringBuilder().append(day)
                    .append("/").append(month + 1).append("/").append(year)
                    .append(" "));

            changed_dob = true;
        }
    };

    public void setListeners() {
        btnScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanFingerPrint();
            }
        });

        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dni = edt_dni_document.getText().toString();
                Log.i("DNI", dni);
                firstName = edt_first_name.getText().toString();
                maternalLast = edt_maternal_name.getText().toString();
                paternalLast = edt_paternal_name.getText().toString();
                dob = edt_dob.getText().toString();

                // read in other stuff as well

                mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
                Log.i("codigousuario", codigousuario);

                // Don't really need this, but whatever: tasks have not been properly updated
                String url = StringConexion.conexion;

                Boolean name_dob_inputted = false;
                Boolean fingerprint_inputted = false;
                Boolean dni_inputted = false;
                Boolean dni_valid = false;

                // DNI has been entered, but it's not valid length
                if (dni != null && dni.length() > 0) {
                    dni_inputted = true;
                    if (dni.length() == 8) {
                        dni_valid = true;
                    }
                }

                if (firstName != null && firstName.length() > 0 &&
                        maternalLast != null && maternalLast.length() > 0 &&
                        paternalLast != null && paternalLast.length() > 0 &&
                        changed_dob) {
                    name_dob_inputted = true;
                }

                if (!mPreferences.getString("Fingerprint","notFound").equals("notFound")) {
                    fingerprint_inputted = true;
                }

                if (dni_inputted && !dni_valid && !fingerprint_inputted && !name_dob_inputted) {
                    Log.v("dni", "Invalid DNI");
                    Toast.makeText(FingerprintFindActivity.this,
                            getString(R.string.dni_wrong_length),
                            Toast.LENGTH_SHORT).show();
                }

                // valid-length DNI has been entered, search just off that
                else if (dni_valid && !fingerprint_inputted && !name_dob_inputted) {
                    try {
                        ParticipantLoadTask tarea = new ParticipantLoadTask();
                        Log.v("Loaded Task", "");
                        asyncTask = tarea.execute(dni, url);
                        Log.v("Executed task", "");
                        participant = asyncTask.get();

                        if (participant == null) {
                            Intent intent = new Intent(getBaseContext(), NoMatchActivity.class);
                            intent.putExtra("dni", dni);
                            if (firstName != null && !firstName.equals(""))
                                intent.putExtra("firstName", firstName);
                            if (maternalLast != null && !maternalLast.equals(""))
                                intent.putExtra("maternalLast", maternalLast);
                            if (paternalLast != null && !paternalLast.equals(""))
                                intent.putExtra("paternalLast", paternalLast);
                            if (dob != null && !dob.equals(""))
                                intent.putExtra("dob", dob);
                            startActivity(intent);
                        } else {
                            Log.i("CodigoPaciente", participant.CodigoPaciente);
                            Intent intent = new Intent(getBaseContext(), ParticipantDashboardActivity.class);
                            intent.putExtra("Participant", participant);
                            startActivity(intent);
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                else if (fingerprint_inputted && !name_dob_inputted && !dni_inputted) {
                    BuscarHuellaTask tarea = new BuscarHuellaTask();

                }

                // We have full name + DOB information
                if (firstName != null && maternalLast != null && paternalLast != null &&
                        firstName.length() > 0 && maternalLast.length() > 0 && paternalLast.length() > 0) {
                    // search based off name
                }
            }
        });
    }
}

