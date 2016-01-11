package org.ses.android.soap;

/**
 * Created by Jtomaylla 19/08/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.ses.android.soap.models.Locale;
import org.ses.android.soap.models.Project;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.LocaleLoadTask;
import org.ses.android.soap.tasks.ProjectLoadTask;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.utils.AppStatus;
import org.ses.android.soap.utils.AccountLogin;
import org.ses.android.soap.utils.InternetConnection;
import org.ses.android.soap.utils.OfflineStorageManager;
import org.ses.android.seispapp120.R;
import org.ses.android.soap.widgets.CambioServer;

/*
 * Written by Brendan
 *
 * This is the start screen for the app when it is logged out. Allows for Promoter to login.
 *
 * onSubmit Behavior: Switches to Menu principal Intent
 *
 */

public class PromoterLoginActivity extends Activity {
    private SharedPreferences mPreferences;
    private Spinner spnLocale;
    private Spinner spnProject;
    public ProgressDialog mProgressDialog;
    private static final int PROGRESS_DIALOG = 1;
    protected void onResume() {
        super.onResume();
        String username = AccountLogin.CheckAlreadyLoggedIn(this);

        if (username != null) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
        } else {
            //String myurl = getString(R.string.server_url);
            // Get the server from the settings
         //   mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String myurl =  StringConexion.conexion;

            loadLocaleSpinner(myurl);
        }
        spnLocale.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoter_login);
        spnLocale = (Spinner) findViewById(R.id.locale_spinner);
        spnProject = (Spinner) findViewById(R.id.project_spinner);
        String username = AccountLogin.CheckAlreadyLoggedIn(this);
        StringConexion.ifExistCreateFile();
        TextView mainMessageLabel = (TextView) findViewById(R.id.textView2);
        AppStatus appStatus = new AppStatus();

        mainMessageLabel.setText(appStatus.getVersionedAppName(this));
        if (username != null) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
        } else {
           // mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String myurl = StringConexion.conexion;
            loadLocaleSpinner(myurl);
        }
        // Progress bar set to gone on page load
        ProgressBar p_d = (ProgressBar) findViewById(R.id.marker_progress);
        p_d.setVisibility(View.GONE);

        spnLocale.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });

        spnLocale.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        String selLocal = Integer.toString(position);
                        Log.i("PromoterLogin Activity","spnLocale.setOnItemSelectedListener: pos: "+ selLocal + " valor:" + parent.getItemAtPosition(position));
                        if ( selLocal != null) {
                           // mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            String myurl = StringConexion.conexion;
                            if (!selLocal.equals("0")) loadProjectSpinner(selLocal,StringConexion.conexion);
                        }

                        Log.i("PromoterLogin Activity","Seleccionado(2): pos: "+ selLocal + " valor:" + parent.getItemAtPosition(position));
                        //Log.i("PromoterLigin Activity"," valor:" + selLocal);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getBaseContext(), "Seleccione un Local!!", Toast.LENGTH_SHORT).show();
                    }
                });
        spnProject.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        spnProject.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        String selProject = Integer.toString(position);
                        //codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
                        Log.i("PromoterLogin Activity","spnProject.setOnItemSelectedListener: pos: "+ selProject + " valor:" + parent.getItemAtPosition(position));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getBaseContext(), "Seleccione un Proyecto!!", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_worker_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent ig = new Intent(this, CambioServer.class);
            startActivity(ig);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // switch to PatientType activity

    /**
     * Check if login is successful and save promoter and patient files locally if successful
     * show alert if login not successful
     *
     * @param view
     * @throws Exception
     * @author JN
     */
    public void switchPatientType(View view) throws ExecutionException, InterruptedException {

        // Progress Dialog starts
        final ProgressBar p_d = (ProgressBar) findViewById(R.id.marker_progress);
        p_d.getHandler().post(new Runnable() {
            @Override
            public void run() {
                p_d.setVisibility(View.VISIBLE);
            }
        });

        EditText u = (EditText) findViewById(R.id.username);
        EditText p = (EditText) findViewById(R.id.password);
        String username = u.getText().toString();
        String password = p.getText().toString();

        String locale_name = spnLocale.getItemAtPosition(spnLocale.getSelectedItemPosition()).toString();
        String locale_num = null;

        if (locale_name != null) {
            locale_num = Locale.GetLocaleNumber(this, locale_name);
            Log.e("PromoterLoginActivity: switchPatientType", locale_num);
        }

        if (username.equals("") || password.equals("") || locale_num.equals("0")) {
            String alertMsg = "";
            if (username.equals("")) {
                alertMsg =getString(R.string.login_enter_user);
            }else if (password.equals("")){
                alertMsg =getString(R.string.login_enter_password);
            }else{
                alertMsg =getString(R.string.select_local);
            }

            Toast.makeText(this,alertMsg , Toast.LENGTH_SHORT).show();

            p_d.getHandler().post(new Runnable() {
                public void run() {
                    p_d.setVisibility(View.GONE);
                }
            });
        }else{
            String project_name = spnProject.getItemAtPosition(spnProject.getSelectedItemPosition()).toString();
            String project_num = null;

            if (project_name != null) {
                project_num = Project.GetProjectNumber(this, project_name);
                Log.e("PromoterLoginActivity: switchPatientType", project_num);
            }

            boolean validLogin = AccountLogin.CheckLogin(this, username, password, locale_num,
                    locale_name, project_num, project_name);

            if (validLogin) {
                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String user_id = mPreferences.getString(PreferencesActivity.KEY_USERID,"");
                String url =  StringConexion.conexion;
                try {
                    boolean validPermission = AccountLogin.CheckPermission(user_id, locale_num, project_num,url);
                    if (validPermission) {

                        Intent intent=new Intent(PromoterLoginActivity.this,MainMenuActivity.class);
                        startActivity(intent);
                        finish();
                        p_d.getHandler().post(new Runnable() {
                            public void run() {
                                p_d.setVisibility(View.GONE);
                            }
                        });

                    }else{
                        AlertError("Login Error", getString(R.string.login_no_permission));
                        p_d.getHandler().post(new Runnable() {
                            public void run() {
                                p_d.setVisibility(View.GONE);
                            }
                        });

                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                AlertError("Login Error", getString(R.string.login_data_incorrect_invalid));
                p_d.getHandler().post(new Runnable() {
                    public void run() {
                        p_d.setVisibility(View.GONE);
                    }
                });
            }
        }


    }

    /**
     * Shows dialog with parameters if there is a login error
     *
     * @param title   title of dialogue
     * @param message message of dialogue
     * @author JN
     */
    public void AlertError(String title, String message) {
        // Alert if username and password are not entered
        AlertDialog.Builder loginError = new AlertDialog.Builder(this);
        loginError.setTitle(title);
        loginError.setMessage(message);
        loginError.setPositiveButton(R.string.login_try_again, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        loginError.show();
    }


    /**
     * @param url the url of the server
     *            Loads the spinner for all the locales first by pulling down from server
     *            And if that does not work, then by checking file locally
     * @author JT
     */
    private void loadLocaleSpinner(String url) {
        LocaleLoadTask localeTask = new LocaleLoadTask();
        AsyncTask loadLocale;
        ArrayList<Locale> arrLocale = null;
        String[] locales;
        boolean connected = InternetConnection.checkConnection(this);
        if (connected){
            try {
                // try server side first
                loadLocale = localeTask.execute(url);
                arrLocale = (ArrayList<Locale>) loadLocale.get();
                locales = Locale.ConvertLocalObjsToStrings(arrLocale);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, locales);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLocale.setAdapter(spinnerArrayAdapter);
                OfflineStorageManager sm = new OfflineStorageManager(this);
                sm.SaveArrayListToLocal(arrLocale, this.getString(R.string.locale_filename));

            } catch (InterruptedException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", "Interrupted Exception");
            } catch (ExecutionException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", "Execution Exception");
            } catch (NullPointerException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", " NullPointerException");
            }
        }

        // Load data locally
        try {
            if (arrLocale == null) {
                // locale_data load
                String locale_file = getString(R.string.locale_filename);
                OfflineStorageManager sm = new OfflineStorageManager(this);
                JSONArray array = new JSONArray(sm.getStringFromLocal(locale_file));
                locales = new String[array.length()];

                // look at all locales
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Locale l = new Locale(obj.toString());
                    locales[i] = l.name;
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, locales);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLocale.setAdapter(spinnerArrayAdapter);
            }
        } catch (JSONException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity", " JSON Exception On Load");
        }
        catch(NullPointerException e1){
            Log.e("PromoterLoginActivity: loadLocaleActivity", "Null Pointer Exception ");
        }
    }

    public void loadProjectSpinner(String local,String url){
        ProjectLoadTask tareaProject = new ProjectLoadTask();
        AsyncTask loadProject;
        ArrayList<Project> arrProject = null;
        String[] projects;


        boolean connected = InternetConnection.checkConnection(this);
        if (connected){
            try {
                // try server side first
                loadProject = tareaProject.execute(local,url);
                arrProject = (ArrayList<Project>) loadProject.get();
                projects = Project.ConvertLocalObjsToStrings(arrProject);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, projects);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnProject.setAdapter(spinnerArrayAdapter);
                OfflineStorageManager sm = new OfflineStorageManager(this);
                sm.SaveArrayListToLocal(arrProject, this.getString(R.string.project_filename));

            } catch (InterruptedException e1) {
                Log.e("PromoterLoginActivity: loadProjectActivity1", "Interrupted Exception");
            } catch (ExecutionException e1) {
                Log.e("PromoterLoginActivity: loadProjectActivity1", "Execution Exception");
            } catch (NullPointerException e1) {
                Log.e("PromoterLoginActivity: loadProjectActivity1", " NullPointerException");
            }
        }

        // Load data locally
        try {
            if (arrProject == null) {
                // project_data load
                String project_file = getString(R.string.project_filename);
                OfflineStorageManager sm = new OfflineStorageManager(this);
                JSONArray array = new JSONArray(sm.getStringFromLocal(project_file));
                projects = new String[array.length()];

                // look at all projects
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Project p = new Project(obj.toString());
                    projects[i] = p.name;
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, projects);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnProject.setAdapter(spinnerArrayAdapter);
            }
        } catch (JSONException e1) {
            Log.e("PromoterLoginActivity: loadProjectActivity", " JSON Exception On Load");
        }
        catch(NullPointerException e1){
            Log.e("PromoterLoginActivity: loadProjectActivity", "Null Pointer Exception ");
        }
    }
}
