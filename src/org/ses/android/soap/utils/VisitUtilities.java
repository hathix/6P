package org.ses.android.soap.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.ses.android.soap.BaseActivity;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.Visita;
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.models.Cacheable;
import org.ses.android.soap.models.VisitWindow;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.EstadoVisitaTask;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.tasks.VisitaAllLoadTask;
import org.ses.android.soap.tasks.VisitasListTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by neel on 1/20/16.
 */
public class VisitUtilities extends BaseActivity {

    private static final String VISIT_PENDING_STATUS = "Pendiente";
    private static final String UPDATE_VISIT_SUCCESS_RESPONSE = "OK";
    private static final String PATIENT_STATUS_CODE = "1";

    /**
     * Given an array of a patient's visits (which can be loaded from VisitasListTask),
     * finds their one pending visit, if any. (A patient can have at most 1 pending visit.)
     * Returns null if there is no pending visit or if there are no visits at all.
     */
    public static Visitas getPendingVisit(Visitas[] patientVisits) {
        if (patientVisits == null) {
            return null;
        }

        for (Visitas visit : patientVisits) {
            String status = visit.EstadoVisita;
            if (status.equals(VISIT_PENDING_STATUS)) {
                return visit;
            }
        }

        return null;
    }

    /**
     * Determines if we are currently past the end of the given visit's window. This compares
     * the current time to the visit's scheduled time plus its buffer length.
     * <p/>
     * Before start of visit window => false (it's OK if patient comes early to window)
     * In visit window => false
     * After end of visit window => true
     * <p/>
     * If there's some error, this will also return false.
     */
    public static boolean isPastVisitWindow(Visitas visit, Context context) {
        VisitWindow window = visitWindowFromVisitas(visit, context);

        if (window != null) {
            return window.isPastEnd();
        } else {
            return false;
        }
    }

    /**
     * Creates and returns a VisitWindow given a visit. If there is no Visita associated with the
     * VisitWindow, or some other error occurs, returns null.
     *
     * @param visit   a Visitas object
     * @param context from getBaseContext()
     */
    public static VisitWindow visitWindowFromVisitas(Visitas visit, Context context) {
        Visita visita = visitaFromVisitas(visit, context);

        if (visita != null) {
            VisitWindow window = new VisitWindow(visit, visita);
            return window;
        } else {
            return null;
        }
    }

    /**
     * Finds the Visita associated with a Visitas, or null if there is no Visita or some
     * error occured.
     *
     * @param visit   a Visitas object
     * @param context from getBaseContext()
     */
    public static Visita visitaFromVisitas(Visitas visit, Context context) {
        // use the visita cache
        ArrayList<Cacheable> visitaListBeforeCasting = PreferencesManager.getCacheableList(context,
                "visitaList");
        if (visitaListBeforeCasting == null) {
            // this shouldn't happen b/c caching should work; raise warning if it does
            Log.w("visitWindowFromVisitas", "visitaList not cached!");
            return null;
        }

        // we need the visita to calculate the window; find the matching one
        for (Cacheable visitaBeforeCasting : visitaListBeforeCasting) {
            Visita visita = new Visita(visitaBeforeCasting);
            if (visita.CodigoProyecto == Integer.parseInt(visit.CodigoProyecto) &&
                    visita.CodigoGrupoVisita == Integer.parseInt(visit.CodigoGrupoVisita) &&
                    visita.CodigoVisita == Integer.parseInt(visit.CodigoVisita)) {
                return visita;
            }
        }

        // no visita found, so no window can be calculated!
        return null;
    }


    /**
     * Given a participant, returns their pending visit, if any.
     */
    public static Visitas getPendingVisit(Participant participant, Context context) {
        //get VisitasListTask visits for said patient:
        VisitasListTask tarea = new VisitasListTask();
        Visitas pending_visit;
        Visitas[] visits;

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        AsyncTask<String, String, Visitas[]> asyncTask;
        String codigoUsuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        String codigoProyecto = mPreferences.getString(PreferencesActivity.KEY_PROJECT_ID, "");
        asyncTask = tarea.execute(participant.CodigoPaciente, codigoUsuario, codigoProyecto, "bogusurl");

        try {
            visits = asyncTask.get();
            pending_visit = getPendingVisit(visits);
            return pending_visit;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Calls an async task to change a participant's status for a particular visit. That is,
     * this lets you mark a patient as having attended or missed a visit.
     *
     * @param participant    A participant.
     * @param visit          A visit object.
     * @param newVisitStatus The code for a particular visit status. See the enum VisitStatus for options.
     * @param mPreferences   a sharedPreferences that the calling activity needs to provide.
     * @return true if the update worked (the backend was successfully updated),
     * false if there was some backend failure, internet connection error, or exception.
     */
    public static boolean updateVisitStatus(Participant participant, Visitas visit,
                                            int newVisitStatus, SharedPreferences mPreferences) {
        EstadoVisitaTask estadoVisitaTask = new EstadoVisitaTask();

        // generate variables to pass to task
        String localId = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
        String projectId = visit.CodigoProyecto;
        String visitId = visit.CodigoVisita;
        String visitsId = visit.CodigoVisitas;
        String patientId = participant.CodigoPaciente;
        String patientStatusCode = PATIENT_STATUS_CODE;
        String user_id = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        String url = StringConexion.conexion;


        // the visit status code must be a string, but for convenience we ask for an integer,
        // so convert the type here
        String visitStatusCode = String.valueOf(newVisitStatus);

        AsyncTask<String, String, String> loadEstadoVisita = estadoVisitaTask.execute(
                localId,
                projectId,
                visitId,
                visitsId,
                patientId,
                visitStatusCode,
                patientStatusCode,
                user_id,
                url);
        try {
            // this task returns a string with whether it succeeded or not
            String result = loadEstadoVisita.get();
            Log.v("EstadoVisita.result", result);
            return result == UPDATE_VISIT_SUCCESS_RESPONSE;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Caches the list of `Visita` objects for the selected projects. This is useful for, e.g.,
     * calculating the window of a `Visitas`, which requires knowing its `Visita`. More generally,
     * it's useful for finding the `Visita` of a `Visitas`.
     *
     * @param projectId also known as CodigoProyecto. Can be found in mPrefs.
     * @param context
     * @return true if the visita list was successfully loaded and cached, false otherwise.
     */
    public static boolean cacheVisitaList(String projectId, Context context) {
        VisitaAllLoadTask visitaAllLoadTask = new VisitaAllLoadTask();
        AsyncTask<String, String, Visita[]> visitaAllLoad =
                visitaAllLoadTask.execute(projectId, "bogusurl");
        Visita[] visitaList = null;
        try {
            visitaList = visitaAllLoad.get();
            Log.v("visitaLoadAll", "loaded");
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("visitaLoadAll", "failed to load");
        }

        if (visitaList != null) {
            // TODO Fix this when Internet returns
            PreferencesManager.saveCacheableList(context, "visitaList",
                    new ArrayList<Cacheable>(Arrays.asList(visitaList)));
            return true;
        }

        return false;
    }
}
