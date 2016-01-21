package org.ses.android.soap.utils;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.models.Cacheable;
import org.ses.android.soap.database.Visita;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.EstadoVisitaTask;
import org.ses.android.soap.tasks.StringConexion;

import org.ses.android.soap.utils.PreferencesManager;

import android.content.Context;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by neel on 1/20/16.
 */
public class VisitUtilities {

    private static final String VISIT_PENDING_STATUS = "Pendiente";
    private static final String UPDATE_VISIT_SUCCESS_RESPONSE = "OK";
    private static final String PATIENT_STATUS_CODE = "1";

    /**
     * Given an array of a patient's visits (which can be loaded from VisitasListTask),
     * finds their one pending visit, if any. (A patient can have at most 1 pending visit.)
     */
    public static Visitas getPendingVisit(Visitas[] patientVisits) {
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
     */
    public static boolean isPastVisitWindow(Visitas visit, Context context) {
        ArrayList<Cacheable> visitaListBeforeCasting = PreferencesManager.getCacheableList(context,
                "visitaList");
        for (Cacheable visitaBeforeCasting : visitaListBeforeCasting) {
            Visita visita = new Visita(visitaBeforeCasting);
            if (visita.CodigoProyecto == Integer.parseInt(visit.CodigoProyecto) &&
                    visita.CodigoGrupoVisita == Integer.parseInt(visit.CodigoGrupoVisita) &&
                    visita.CodigoVisita == Integer.parseInt(visit.CodigoVisita)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date windowCenter = dateFormat.parse(visit.FechaVisita);
                    Calendar c = Calendar.getInstance();

                    c.setTime(windowCenter);
                    c.add(Calendar.DATE, -1 * visita.DiasAntes);
                    Date windowLeft = c.getTime();

                    c.setTime(windowCenter);
                    c.add(Calendar.DATE, visita.DiasDespues);
                    Date windowRight = c.getTime();

                    Date currentDate = new Date();
                    if (currentDate.after(windowLeft) && currentDate.before(windowRight)) {
                        return false;
                    } else {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
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
}
