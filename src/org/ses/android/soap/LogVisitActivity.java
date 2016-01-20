package org.ses.android.soap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.EstadoVisitaTask;
import org.ses.android.soap.tasks.StringConexion;

import java.util.concurrent.ExecutionException;

/**
 * Created by saranya on 1/19/16.
 */
public class LogVisitActivity extends BaseActivity {

    private Participant currentParticipant;
    private Visitas pendingVisitas;
    private SharedPreferences mPreferences;

    // constants needed for EstadoVisitaTask
    private static final String ESTADO_VISITA_ID = "3";
    private static final String CODIGO_ESTATUS_PACIENTE = "1";
    private static final String ESTADO_VISITA_SUCCESS_RESPONSE = "OK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_visit_layout);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(
                getBaseContext().getApplicationContext());

        currentParticipant = getIntent().getParcelableExtra("Participant");
        pendingVisitas = getIntent().getParcelableExtra("Visitas");


        /*
         * when log button hit, call EstadoVisitaTask, TODO change FechaUpdVisita
         */
        Button logButton = (Button) findViewById(R.id.log_visit);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EstadoVisitaTask estadoVisitaTask = new EstadoVisitaTask();

                // generate variables to pass to task
                String localId = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
                String projectId = pendingVisitas.CodigoProyecto;
                String visitId = pendingVisitas.CodigoVisita;
                String visitsId = pendingVisitas.CodigoVisitas;
                String patientId = currentParticipant.CodigoPaciente;
                String user_id = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
                String url = StringConexion.conexion;

                AsyncTask<String, String, String> loadEstadoVisita = estadoVisitaTask.execute(
                        localId,
                        projectId,
                        visitId,
                        visitsId,
                        patientId,
                        ESTADO_VISITA_ID,
                        CODIGO_ESTATUS_PACIENTE,
                        user_id,
                        url);
                try {
                    String result = loadEstadoVisita.get();
                    Log.v("EstadoVisita.result", result);

                    if (result == ESTADO_VISITA_SUCCESS_RESPONSE) {
                        // visit successfully confirmed
                        // show toast with success
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.visit_confirmed_success),
                                Toast.LENGTH_SHORT).show();

                        // go back to dashboard
                        Intent i = new Intent(getBaseContext(), ParticipantDashboardActivity.class);
                        i.putExtra("Participant", currentParticipant);
                        startActivity(i);
                    } else {
                        // visit was not confirmed
                        // show toast with error
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.visit_confirmed_error),
                                Toast.LENGTH_SHORT).show();
                        // stay here
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }
}
