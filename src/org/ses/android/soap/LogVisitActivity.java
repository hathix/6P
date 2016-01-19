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
import org.ses.android.soap.tasks.EstadoVisitaTask;
import org.ses.android.soap.tasks.GenerarIdENRTask;
import org.ses.android.soap.tasks.GenerarIdTAMTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.TienePermisosTask;
import org.ses.android.soap.tasks.VisitasListTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import java.util.Calendar;
import java.util.Date;
/**
 * Created by saranya on 1/19/16.
 */
public class LogVisitActivity extends BaseActivity {

    private Participant currentParticipant;
    private Visitas pending_visitas;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_visit_layout);

    currentParticipant = getIntent().getParcelableExtra("Participant");
    pending_visitas =  getIntent().getParcelableExtra("Visitas");


    /*
     * when log button hit, call EstadoVisitaTask
     */
        Button logButton = (Button) findViewById(R.id.log_visit);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogVisitActivity.this, EstadoVisitaTask.class);
                intent.putExtra("Visitas", pending_visitas);
                startActivity(intent);
            }
        });

    }
}
