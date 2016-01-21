package org.ses.android.soap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.tasks.ObtenerIdPacienteTask;
import org.ses.android.soap.tasks.PacienteTieneHuellaTask;
import org.ses.android.soap.tasks.ParticipantLoadTask;
import org.ses.android.soap.tasks.StringConexion;

import java.util.concurrent.ExecutionException;

/**
 * Created by anyway on 1/11/16.
 */
public class AddFingerprintExistingActivity extends BaseActivity {

    private EditText enterDni;
    private Button btnSearch;

    // Tasks for search
    private AsyncTask<String, String, Participant> asyncTask;
    private Participant participant;

    String dni;
    String url = StringConexion.conexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_fingerprint_existing_layout);

        enterDni = (EditText) findViewById(R.id.enter_dni);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dni = enterDni.getText().toString();
                // DNI has been entered, but it's not valid length
                if (dni != null && dni.length() > 0 && dni.length() != 8) {
                    Toast.makeText(AddFingerprintExistingActivity.this,
                            getString(R.string.dni_wrong_length),
                            Toast.LENGTH_SHORT).show();
                } else if (dni != null && dni.length() == 8) {
                    try {
                        ParticipantLoadTask tarea = new ParticipantLoadTask();
                        asyncTask = tarea.execute(dni, url);
                        participant = asyncTask.get();

                        if (participant == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddFingerprintExistingActivity.this);
                            builder.setMessage(getString(R.string.dni_not_found))
                                    .setTitle(getString(R.string.try_again))
                                    .setCancelable(false)
                                    .setNegativeButton(getString(R.string.try_again),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddFingerprintExistingActivity.this);
                            builder.setMessage(getString(R.string.dni_found_save,
                                        participant.Nombres + " " + participant.ApellidoMaterno
                                                + " " + participant.ApellidoPaterno))
                                    .setTitle(getString(R.string.dni_found))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.answer_yes),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    try {

                                                        if (HuellaExiste()) {
                                                            Toast.makeText(AddFingerprintExistingActivity.this,
                                                                    getString(R.string.patient_has_fingerprint),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }

                                                        else {

                                                            // Redirect to confirm fingerprint
                                                            Intent intent = new Intent(getBaseContext(), FingerprintConfirmActivity.class);
                                                            intent.putExtra("Participant", participant);
                                                            // get CodigoPaciente
                                                            ObtenerIdPacienteTask obtenerIdPacienteTask = new ObtenerIdPacienteTask();
                                                            AsyncTask<String, String, String> getIdPaciente =
                                                                    obtenerIdPacienteTask.execute(participant.Nombres,
                                                                            participant.ApellidoPaterno,
                                                                            participant.ApellidoMaterno,
                                                                            participant.FechaNacimiento, url);
                                                            String id_pac = getIdPaciente.get();
                                                            Log.e("ObtainCodigo", id_pac);
                                                            intent.putExtra("codigoPaciente", id_pac);
                                                            startActivity(intent);

                                                        }

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Log.e("ObtainCodigo", "failed");
                                                        Toast.makeText(AddFingerprintExistingActivity.this,
                                                                getString(R.string.fingerprint_save_failed),
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                    .setNegativeButton(getString(R.string.answer_no),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();

                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean HuellaExiste() {
        PacienteTieneHuellaTask tarea = new PacienteTieneHuellaTask();
        AsyncTask<String, String, Integer> asyncTaskInteger = tarea.execute(participant.CodigoPaciente);
        int exist;
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
