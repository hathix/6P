package org.ses.android.soap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.ses.android.soap.tasks.GenerarIdENRTask;
import org.ses.android.soap.tasks.GenerarIdTAMTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.TienePermisosTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by anyway on 1/11/16.
 */
public class ParticipantDashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_dashboard_layout);

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
        // TODO uncomment once FingerprintFindActivity works
//        Button btnLogInAnother = (Button) findViewById(R.id.log_in_other);
//        btnLogInAnother.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ParticipantDashboardActivity.this, FingerprintFindActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
