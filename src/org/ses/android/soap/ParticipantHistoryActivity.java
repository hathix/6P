package org.ses.android.soap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import java.util.concurrent.ExecutionException;

/**
 * Created by anyway on 1/11/16.
 */
public class ParticipantHistoryActivity extends BaseActivity {

    private Participant participant;
    private TableLayout historyTable;
    private TextView title;
    private SharedPreferences mPreferences;
    private Visitas[] visits;
    private AsyncTask<String, String, Visitas[]> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_history_layout);

        // load up who's patient history this is
        participant = (Participant) getIntent().getParcelableExtra("Participant");

        // get items from xml file
        historyTable = (TableLayout) findViewById(R.id.history_table);
        title = (TextView) findViewById(R.id.lbl_history);

        // properly set title
        Resources res = getResources();
        title.setText(String.format(res.getString(R.string.visit_history), participant.getNombresTitleCase()));

        // get patient's history
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String codigoUsuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        String codigoProyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");

        VisitaListTask tarea = new VisitaListTask();

        // tasks all have extra url parameter at the end that's unused
        asyncTask = tarea.execute(participant.CodigoPaciente, codigoUsuario, codigoProyecto, "bogusurl");
        try {
            visits = asyncTask.get();
            TableRow tr;
            Visitas visit;
            TextView date, time, group, project, status;

            // add all the patient's visits as rows if they exist
            if (visits != null && visits.length >= 1)
                for (int i=0; i < visits.length; i++)
                {
                    visit = visits[i];
                    tr = new TableRow(this);
                    date = new TextView(this);
                    date.setText(visit.FechaVisita);
                    time = new TextView(this);
                    time.setText(visit.HoraCita);
                    group = new TextView(this);
                    group.setText(visit.CodigoGrupoVisita);
                    project = new TextView(this);
                    project.setText(visit.Proyecto);
                    status = new TextView(this);
                    status.setText(visit.EstadoVisita);

                    tr.addView(date);
                    tr.addView(time);
                    tr.addView(group);
                    tr.addView(project);
                    tr.addView(status);

                    historyTable.addView(tr);
                }
            // otherwise, notify that there are no visits logged
            else{
                tr = new TableRow(this);
                TextView alert = new TextView(this);
                alert.setText(String.format(getString(R.string.no_visit_history),
                        participant.getNombresTitleCase()));
                tr.addView(alert);
                historyTable.addView(tr);
            }
        } catch (InterruptedException e) {
            // TODO do something?
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            // TODO do something else?
            e.printStackTrace();
        }

    }
}
