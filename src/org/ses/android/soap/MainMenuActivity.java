package org.ses.android.soap;


import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.preferences.AdminPreferencesActivity;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.FormListTask;
import org.ses.android.soap.utils.AppStatus;

import java.util.concurrent.ExecutionException;

public class MainMenuActivity extends BaseActivity {

    private Button btnCheckInRegisterUpdatePatient;
    private Button btnViewMissingAppts;
    private Button btnCerrarSesion;
    private Button btnRunODK;
    private AsyncTask<String, String, String> formListTask;
    SharedPreferences mPreferences;

    // menu options
    private static final int MENU_PREFERENCES = Menu.FIRST;

    @SuppressWarnings("unused")
    private SharedPreferences mAdminPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);
        // dynamically construct the "ODK Collect vA.B" string
        TextView mainMenuMessageLabel = (TextView) findViewById(R.id.main_menu_header);
        AppStatus appStatus = new AppStatus();

        mainMenuMessageLabel.setText(appStatus.getVersionedAppName(this));

        if (AppStatus.getInstance(this).isOnline(this)) {
            Toast.makeText(this,R.string.online,Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this,R.string.no_connection,Toast.LENGTH_SHORT).show();
        }
        btnCheckInRegisterUpdatePatient = (Button)findViewById(R.id.btnParticipanteOpciones);
        btnViewMissingAppts = (Button)findViewById(R.id.btnLista);
        btnCerrarSesion = (Button)findViewById(R.id.btnCerrarSesion_new);
        btnRunODK = (Button)findViewById(R.id.btnRunODK_new);
        mAdminPreferences = this.getSharedPreferences(AdminPreferencesActivity.ADMIN_PREFERENCES, 0);

        btnCheckInRegisterUpdatePatient.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, FingerprintFindActivity.class);
                startActivity(intent);
            }
        });

        btnViewMissingAppts.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainMenuActivity.this,ViewMissedVisitsActivity.class);
                startActivity(intent);
            }
        });

        btnRunODK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Remote Server
                // JT:04/06/2015
                mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String filterForms = "";
                String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                        getString(R.string.default_server_url));
                String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
                String codigolocal = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
                String codigoproyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");
                Log.i("menu", ".codigousuario:"+codigousuario );
                Log.i("menu", ".codigolocal:"+codigolocal );
                Log.i("menu", ".codigoproyecto:"+codigoproyecto );

                FormListTask formList = new FormListTask();
                formListTask = formList.execute(codigousuario,codigolocal,codigoproyecto,url);

                try {
                    filterForms = formList.get();
                    Log.i("menu", ".filterForms:"+filterForms );

                    String Codigo = "002009-1234-2";
                    Intent intents = new Intent(Intent.ACTION_MAIN);
                    intents.setComponent(new ComponentName("org.odk.collect.android", "org.odk.collect.android.activities.MainMenuActivity"));
                    intents.putExtra("idParticipante", Codigo);
                    intents.putExtra("idProyecto", filterForms);
                    startActivity(intents);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        btnCerrarSesion.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

}
