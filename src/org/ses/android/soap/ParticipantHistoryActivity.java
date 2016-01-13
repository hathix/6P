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
        String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        String codigoproyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");

        VisitaListTask tarea = new VisitaListTask();
        asyncTask = tarea.execute(participant.CodigoPaciente, codigousuario, codigoproyecto, "bogusurl");
        try{
        visits = asyncTask.get();


    }
}
