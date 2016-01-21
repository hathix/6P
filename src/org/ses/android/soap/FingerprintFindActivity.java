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
import org.ses.android.soap.tasks.ObtenerIdPacienteTask;
import org.ses.android.soap.tasks.PacienteTieneHuellaTask;
import org.ses.android.soap.tasks.ParticipantLoadTask;
import org.ses.android.soap.tasks.ParticipantLoadFromCodigoTask;
import org.ses.android.soap.tasks.BuscarHuellaFiltradoTask;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.utils.PreferencesManager;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class FingerprintFindActivity extends FingerprintBaseActivity {

    private ImageView imgFingerprint;
    private Button btnScan;
    private EditText edtFirstName;
    private EditText edtMaternalName;
    private EditText edtPaternalName;
    private EditText edtDniDocument;
    private TextView edtDOB;
    private Button btnSearch;

    private AsyncTask<String, String, Participant> asyncTaskParticipant;
    private AsyncTask<String, String, String> asyncTaskString;
    private AsyncTask<String, String, Integer> asyncTaskInteger;
    private SharedPreferences mPreferences;

    private int year, month, day;
    private final int DATE_DIALOG_ID = 999;

    private Participant participant;
    String dni, firstName, paternalLast, maternalLast, dob, result;
    int exist;

    Boolean changedDOB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_search_layout);

        imgFingerprint = (ImageView) findViewById(R.id.imgFingerprint);
        btnScan = (Button) findViewById(R.id.btnScan);
        edtFirstName = (EditText) findViewById(R.id.edt_first_name);
        edtMaternalName = (EditText) findViewById(R.id.edt_maternal_name);
        edtPaternalName = (EditText) findViewById(R.id.edt_paternal_name);
        edtDOB = (TextView) findViewById(R.id.tvwfecha_nacimiento);
        edtDniDocument = (EditText) findViewById(R.id.edt_dni_document);
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
        edtDOB.setText(day + "/" +
                (month + 1) + "/" +
                year);
    }

    public void addListenerOntvwfecha_nacimiento() {
        edtDOB.setOnTouchListener(new OnTouchListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDialog(DATE_DIALOG_ID);
                return true;
            }


        });
        edtDOB.setOnClickListener(new OnClickListener() {

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
            edtDOB.setText(new StringBuilder().append(day)
                    .append("/").append(month + 1).append("/").append(year)
                    .append(" "));

            changedDOB = true;
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
                dni = edtDniDocument.getText().toString();
                Log.i("DNI", dni);
                firstName = edtFirstName.getText().toString();
                maternalLast = edtMaternalName.getText().toString();
                paternalLast = edtPaternalName.getText().toString();
                dob = edtDOB.getText().toString();

                // read in other stuff as well

                mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
                Log.i("codigousuario", codigousuario);

                // Don't really need this, but whatever: tasks have not been properly updated
                String url = StringConexion.conexion;

                Boolean nameDOBInputted = false;
                Boolean fingerprintInputted = false;
                Boolean dniInputted = false;
                Boolean dniValid = false;
                Boolean namePartiallyInputted = false;

                // check if fields are entered
                if (dni != null && dni.length() > 0) {
                    dniInputted = true;
                    if (dni.length() == 8) {
                        dniValid = true;
                    }
                }
                if (firstName != null && firstName.length() > 0 &&
                        maternalLast != null && maternalLast.length() > 0 &&
                        paternalLast != null && paternalLast.length() > 0 &&
                        changedDOB) {
                    nameDOBInputted = true;
                }
                if (!mPreferences.getString("Fingerprint","notFound").equals("notFound")) {
                    fingerprintInputted = true;
                }
                if (dniInputted && !dniValid && !fingerprintInputted && !nameDOBInputted) {
                    Log.v("dni", "Invalid DNI");
                    Toast.makeText(FingerprintFindActivity.this,
                            getString(R.string.dni_wrong_length),
                            Toast.LENGTH_SHORT).show();
                }
                if ((firstName != null && firstName.length() > 0) ||
                        (maternalLast != null && maternalLast.length() > 0) ||
                        (paternalLast != null && paternalLast.length() > 0)) {
                    namePartiallyInputted = true;
                }

                // only valid-length DNI inputted, search just off that
                if (dniValid && !fingerprintInputted && !nameDOBInputted) {
                    Log.i("Search", "Only DNI");
                    try {
                        ParticipantLoadTask tarea = new ParticipantLoadTask();
                        Log.v("Loaded Task", "");
                        asyncTaskParticipant = tarea.execute(dni, url);
                        Log.v("Executed task", "");
                        participant = asyncTaskParticipant.get();

                        if (participant == null) {
                            triggerNoMatch();
                        } else {
                            triggerPatientFound();
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                // name, fingerprint inputted; not dni
                else if (fingerprintInputted && namePartiallyInputted && !dniValid) {
                    Log.i("Search", "Fingerprint and Name Partially Inputted");
                    BuscarHuellaFiltradoTask tarea = new BuscarHuellaFiltradoTask();
                    asyncTaskString = tarea.execute(mPreferences.getString("Fingerprint",""),firstName,
                            paternalLast,maternalLast);

                    try {
                        result = asyncTaskString.get();

                        if (result.equals("fingerprintNotFound") || result.equals("someMatchDidntWork")) {
                            buscarHuella();
                        } else {
                            successfulCodigoFound();
                        }
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                // only name and dob inputted
                else if (nameDOBInputted && !fingerprintInputted && !dniValid) {
                    Log.i("Search","Name and DOB");
                    ObtenerIdPacienteTask tarea = new ObtenerIdPacienteTask();
                    asyncTaskString = tarea.execute(firstName,paternalLast,maternalLast,dob,"bogusurl");

                    try {
                        result = asyncTaskString.get();

                        if (result != null && result.length() == 36) {
                            successfulCodigoFound();
                        }
                        else {
                            triggerNoMatch();
                        }
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // dni, name, dob inputted; no fingerprint
                else if (dniValid && nameDOBInputted && !fingerprintInputted) {
                    Log.i("Search", "DNI, Name, and DOB");
                    try {
                        ParticipantLoadTask tarea = new ParticipantLoadTask();
                        Log.v("Loaded Task", "");
                        asyncTaskParticipant = tarea.execute(dni, url);
                        Log.v("Executed task", "");
                        participant = asyncTaskParticipant.get();

                        if (participant == null) {
                            triggerNoMatch();
                        } else {
                            if (participant.Nombres.equals(firstName.toUpperCase()) &&
                                    participant.ApellidoPaterno.equals(paternalLast.toUpperCase()) &&
                                    participant.ApellidoMaterno.equals(maternalLast.toUpperCase())) {
                                triggerPatientFound();
                            }
                            else {
                                Toast.makeText(FingerprintFindActivity.this,
                                        getString(R.string.info_not_matched),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // fingerprint and dni inputted
                else if (fingerprintInputted && dniValid) {
                    Log.i("Search","Fingerprint and DNI");
                    try {
                        ParticipantLoadTask tarea = new ParticipantLoadTask();
                        Log.v("Loaded Task", "");
                        asyncTaskParticipant = tarea.execute(dni, url);
                        Log.v("Executed task", "");
                        participant = asyncTaskParticipant.get();

                        if (participant == null) {
                            buscarHuella();
                        } else {
                            if (HuellaExiste()) {
                                buscarHuella();
                            }
                            // if codigopaciente is not associated with a fingerprint in database
                            // check if the fingerprint is already in the database
                            // if fingerprint is already in database, notify user
                            // if fingerprint is not in database, go to fingerprint confirm activity
                            else {

                                BuscarHuellaTask tarea1 = new BuscarHuellaTask();
                                asyncTaskString = tarea1.execute(mPreferences.getString("Fingerprint",""));

                                try {
                                    result = asyncTaskString.get();

                                    if (result.equals("fingerprintNotFound") || result.equals("someMatchDidntWork")) {
                                        Intent intent = new Intent(v.getContext(),FingerprintConfirmActivity.class);
                                        intent.putExtra("Participant",participant);
                                        intent.putExtra("codigoPaciente",participant.CodigoPaciente);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(FingerprintFindActivity.this,
                                                getString(R.string.fingerprint_dni_not_matched),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                                catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // fingerprint inputted
                else if (fingerprintInputted) {
                    Log.i("Search","Fingerprint");
                    buscarHuella();
                }

            }
        });
    }

    private void triggerNoMatch() {
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
    }

    private void triggerPatientFound() {
        Log.i("CodigoPaciente", participant.CodigoPaciente);
        Intent intent = new Intent(getBaseContext(), ParticipantDashboardActivity.class);
        intent.putExtra("Participant", participant);
        startActivity(intent);
    }

    private void successfulCodigoFound() {
        ParticipantLoadFromCodigoTask asyncTask = new ParticipantLoadFromCodigoTask();
        asyncTask.execute(result, "bogusurl");

        try {
            participant = asyncTask.get();

            if (participant == null) {
                triggerNoMatch();
            } else {
                triggerPatientFound();
            }
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void buscarHuella()
    {
        BuscarHuellaTask tarea = new BuscarHuellaTask();
        asyncTaskString = tarea.execute(mPreferences.getString("Fingerprint",""));

        try {
            result = asyncTaskString.get();

            if (result.equals("fingerprintNotFound") || result.equals("someMatchDidntWork")) {
                triggerNoMatch();
            } else {
                successfulCodigoFound();
            }
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean HuellaExiste() {
        PacienteTieneHuellaTask tarea = new PacienteTieneHuellaTask();
        asyncTaskInteger = tarea.execute(participant.CodigoPaciente);

        try {
            exist = asyncTaskInteger.get();
            Log.i("HuellaExiste", String.valueOf(exist));

            if (exist == 1) {
                return true;
            } else {
                return false;
            }
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }


}

