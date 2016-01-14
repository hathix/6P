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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Idreg;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.GenerarIdENRTask;
import org.ses.android.soap.tasks.GenerarIdTAMTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.TienePermisosTask;
import org.ses.android.soap.tasks.VisitaListTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by anyway on 1/11/16.
 *
 * The "command center" for a patient, providing information at a glance about them and providing
 * links to log a new visit, see their visit history, etc.
 *
 * Any Intent that opens this Activity **MUST** pass a Patient in the Bundle.
 */
public class ParticipantDashboardActivity extends BaseActivity {

    private TextView tvw_nombres;
    private TextView tvwLocal;
    private TextView tvwProyecto;
    private TextView tvwStartDate;
    private Participant participant;
    private TextView weekMissedView;
    private TextView monthMissedView;
    private TextView totalMissedView;
    private TextView weekReceivedView;
    private TextView monthReceivedView;
    private TextView totalReceivedView;

    private String first_visit = "Does Not Exist";

    private SharedPreferences mPreferences;

    DateFormat visitDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private Visitas[] visits;
    private AsyncTask<String, String, Visitas[]> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String codigoUsuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        String codigoProyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");
        String locale = mPreferences.getString(PreferencesActivity.KEY_LOCAL_NAME, "");
        String proyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_NAME, "");

        setContentView(R.layout.participant_dashboard_layout);

        // load up the participant
        participant = (Participant) getIntent().getParcelableExtra("Participant");

        tvw_nombres = (TextView) findViewById(R.id.tvw_nombres);
        tvw_nombres.setText(getString(R.string.nombres) + " " + participant.getFullNameTitleCase());

        tvwLocal = (TextView) findViewById(R.id.tvwLocal);
        tvwLocal.setText(getString(R.string.txtLocal) + " " + locale);

        tvwProyecto = (TextView) findViewById(R.id.tvwProyecto);
        tvwProyecto.setText(getString(R.string.txtProject) + " " + proyecto);

        tvwStartDate = (TextView) findViewById(R.id.tvwStart);

        // TODO Calculate next visit date.

        // Fill in table
        weekMissedView = (TextView) findViewById(R.id.past_week_missed);
        weekReceivedView = (TextView) findViewById(R.id.past_week_received);
        monthMissedView = (TextView) findViewById(R.id.past_month_missed);
        monthReceivedView = (TextView) findViewById(R.id.past_month_received);
        totalMissedView = (TextView) findViewById(R.id.total_missed);
        totalReceivedView = (TextView) findViewById(R.id.total_received);

        VisitaListTask tarea = new VisitaListTask();
        asyncTask = tarea.execute(participant.CodigoPaciente, codigoUsuario, codigoProyecto, "bogusurl");
        int weekMissed = 0, monthMissed = 0, totalMissed = 0, weekReceived = 0,
                monthReceived = 0, totalReceived = 0;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date weekAgo = cal.getTime();

        cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date monthAgo = cal.getTime();
        try {
            visits = asyncTask.get();
            Date date = new Date();
            String status;
            Visitas visit;

            // start counting
            if (visits != null && visits.length >= 1)
                for (int i=0; i < visits.length; i++)
                {
                    visit = visits[i];
                    try {
                        date = visitDateFormat.parse(visit.FechaVisita.split("\\s+")[1]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    status = visit.EstadoVisita;

                    // Need to compare on the string level because Visitas has this field as
                    // a string, although in the db it is 1, 2, 3.
                    if (status.equals("Atendido")) {
                        totalReceived++;
                        if (date.after(weekAgo)) {
                            monthReceived++;
                            weekReceived++;
                        } else if (date.after(monthAgo)) {
                            monthReceived++;
                        }
                    } else if (!status.equals("Pendiente")) { // Must be missed
                        totalMissed++;
                        if (date.after(weekAgo)) {
                            monthMissed++;
                            weekMissed++;
                        } else if (date.after(monthAgo)) {
                            monthMissed++;
                        }
                    }

                    if (visit.CodigoGrupoVisita.equals("3") && visit.CodigoVisita.equals("1")) {
                        first_visit = visit.FechaVisita;
                    }
                }
            weekMissedView.setText(Integer.toString(weekMissed));
            monthMissedView.setText(Integer.toString(monthMissed));
            totalMissedView.setText(Integer.toString(totalMissed));

            weekReceivedView.setText(Integer.toString(weekReceived));
            monthReceivedView.setText(Integer.toString(monthReceived));
            totalReceivedView.setText(Integer.toString(totalReceived));

            tvwStartDate.setText(getString(R.string.txtstartdate) + " " + first_visit);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }

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
                intent.putExtra("Participant", participant);
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
