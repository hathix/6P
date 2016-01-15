package org.ses.android.soap.utils;

/**
 * Created by Jtomaylla on 19/08/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.ses.android.seispapp120.R;

import java.util.concurrent.ExecutionException;

import org.ses.android.soap.models.Login;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.EstadoENRTask;
import org.ses.android.soap.tasks.EstadoTAMTask;
import org.ses.android.soap.tasks.LoginTask;
import org.ses.android.soap.tasks.TienePermisosTask;


@SuppressWarnings("ALL")
public class AccountLogin {

    // TODO: refactor dialog back into promoter login activity

    /**
     * Sent from server if the login is successful.
     */
    private static final String SUCCESSFUL_LOGIN_MESSAGE = "Gracias por Iniciar Sesion";

    /**
     * Sent from server if the user's password is expired.
     */
    private static final String PASSWORD_EXPIRED_MESSAGE = "Contraseña caducada";

    public static String login( Context c, String username, String password,
                                String locale_id, String locale_name, String project_id, String project_name) {
        // TODO: check internet connection

        LoginTask runner = new LoginTask();
        AsyncTask<String, String, Login> loginAsyncTask;
        String response = "";

        // Get the server from the settings
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        SharedPreferences.Editor editor = mPreferences.edit();
        // TODO: do not hardcode
        //String url = "http://demo.sociosensalud.org.pe";
        String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                c.getString(R.string.default_server_url));
        Log.i("login", "OnClick_url:" + url);

        loginAsyncTask = runner.execute(username, password, locale_id, url);

        try {
            Login login = loginAsyncTask.get();
            response = login.Message;
            editor.putString(c.getString(R.string.username), username);
            editor.putString(c.getString(R.string.key_userid), String.valueOf(login.UserID));
            editor.putString(c.getString(R.string.login_locale), locale_id);
            editor.putString(c.getApplicationContext().getString(R.string.login_locale_name), locale_name);
            editor.putString(c.getString(R.string.login_project), project_id);
            editor.putString(c.getApplicationContext().getString(R.string.login_project_name), project_name);
            Log.i("AccountLogin", "OnClick_response:" + response);
            if (response.equals(SUCCESSFUL_LOGIN_MESSAGE)
                    || response.equals(PASSWORD_EXPIRED_MESSAGE)) {
                editor.commit();
            }
        } catch (InterruptedException e1) {
            response = e1.getMessage();
        } catch (ExecutionException e2) {
            response = e2.getMessage();
        } catch (Exception e3) {
            response = e3.getMessage();
        }
        return response;
    }

    /**
     * Checks Shared Preferences if already logged in by checking if saved username is the same as the current one
     *
     * @return username of the promoter that is logged in
     * @author JN
     */
    public static String CheckAlreadyLoggedIn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String username = prefs.getString((context.getString(R.string.username)), null);
        if (username != null) {
            return username;
        }
        return null;
    }


    /**
     * Calls login web service and returns true if login is successful
     *
     * @param username input promoter username
     * @param password input promoter password
     * @param locale   input promoter locale from Spinner
     * @param Project  input promoter locale from Spinner
     * @return true if login successful from Service, false if not successful
     * @author JN
     */
    public static boolean CheckLogin(Context context, String username, String password, String locale_id,
                                     String locale_name, String project_id, String project_name) {
        //if (password != null && !password.isEmpty()) {
        if (password != null && !password.equals("")) {
            String message = login(context, username, password, locale_id, locale_name, project_id,project_name);
            if (message == null) {
                return false;
            }
            if (message.equals(SUCCESSFUL_LOGIN_MESSAGE) ||
                    message.equals(PASSWORD_EXPIRED_MESSAGE)) {
                return true;
            } else {
                Log.w("login", message);
                Log.i("login", "Datos incorrectos");
            }
            return false;
        } else {
            return false;
        }

    }
    public static Boolean CheckPermission(String user_id,String locale_id,String project_id,String url) throws ExecutionException, InterruptedException {
        TienePermisosTask tareaTienePermisos = new TienePermisosTask();
        AsyncTask loadTienePermisos;
        //loadTienePermisos
        String tienePermisos;
        loadTienePermisos = tareaTienePermisos.execute(locale_id,user_id,project_id,url);
        tienePermisos = (String) loadTienePermisos.get();
        if (tienePermisos == null) {
            return false;
        }else {
            if (tienePermisos.equals("OK")) {
                return true;
            } else {
                Log.i("login", "Datos incorrectos");
            }
            return false;
        }
    }
}

