package org.ses.android.soap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Idreg;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.tasks.GenerarIdENRTask;
import org.ses.android.soap.tasks.GenerarIdTAMTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.TienePermisosTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by anyway on 1/11/16.
 */
public class ParticipantDashboardActivity extends BaseActivity {

    private TextView tvw_nombres;
    private Participant participant;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        setContentView(R.layout.participant_dashboard_layout);

        // load up the participant
        participant = (Participant) getIntent().getParcelableExtra("Participant");

        tvw_nombres = (TextView) findViewById(R.id.tvw_nombres);
        String fullName = participant.Nombres + " " +
                            participant.ApellidoMaterno + " " +
                            participant.ApellidoPaterno;
        tvw_nombres.setText(getString(R.string.nombres) + " " + fullName);
        // still need some more

        // "Log visit" button should open NewVisitActivity
        Button btnLogVisit = (Button) findViewById(R.id.log_visit);
        btnLogVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParticipantDashboardActivity.this, NewVisitActivity.class);
                startActivity(intent);
            }
        });

        // "History" button should open ParticipantHistoryActivity
        Button btnHistory = (Button) findViewById(R.id.participant_history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParticipantDashboardActivity.this, ParticipantHistoryActivity.class);
                startActivity(intent);
            }
        });
        // "Log in another patient" button should return to FingerprintFindActivity
        Button btnLogInAnother = (Button) findViewById(R.id.log_in_other);
        btnLogInAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParticipantDashboardActivity.this, FingerprintFindActivity.class);
                startActivity(intent);
            }
        });
    }
}
