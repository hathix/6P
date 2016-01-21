package org.ses.android.soap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.models.VisitWindow;
import org.ses.android.soap.utils.VisitStatus;
import org.ses.android.soap.utils.VisitUtilities;

/**
 * Created by saranya on 1/19/16.
 */
public class LogVisitActivity extends BaseActivity {

    private Participant currentParticipant;
    private Visitas pendingVisitas;
    private SharedPreferences mPreferences;
    private TextView end_window;
    private TextView start_window;
    private TextView schedule_date;
    private TextView middle_window;
    private VisitWindow visitWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_visit_layout);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(
                getBaseContext().getApplicationContext());

        currentParticipant = getIntent().getParcelableExtra("Participant");
        pendingVisitas = getIntent().getParcelableExtra("Visitas");
        Log.v("PendingVisitas2", pendingVisitas.CodigoVisitas);
         /*
        end_window = (TextView) findViewById(R.id.end_window);
        start_window = (TextView) findViewById(R.id.start_window);
        middle_window = (TextView) findViewById(R.id.middle_window);
        */
        schedule_date = (TextView) findViewById(R.id.schedule_date);


        //visitWindow = VisitUtilities.visitWindowFromVisitas(pendingVisitas, getBaseContext());
        /*
        end_window.setText("End of Window: " + visitWindow.getEnd()); //neels new function in VisitUtilities );
        middle_window.setText("Middle of Window: " + visitWindow.getCenter());
        start_window.setText("Start of Window: " + visitWindow.getStart());//neels new function in VisitUtilities );
        */
        schedule_date.setText("Date of Scheduled Visit: " + pendingVisitas.FechaVisita);



        /*
         * when log button hit, call EstadoVisitaTask, TODO change FechaUpdVisita
         */
        Button logButton = (Button) findViewById(R.id.confirm_log_visit);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mark the participant as having attended this pending visit
                boolean success = VisitUtilities.updateVisitStatus(
                        currentParticipant, pendingVisitas, VisitStatus.ATTENDED.value(),
                        mPreferences);
                if (success) {
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
            }
        });

    }
}
