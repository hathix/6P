package org.ses.android.soap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.BuscarHuellaTask;
import org.ses.android.soap.tasks.ParticipantLoadFromCodigoTask;
import org.ses.android.soap.tasks.VisitasListTask;
import org.ses.android.soap.utils.PreferencesManager;

import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;

import org.ses.android.soap.database.Participant;
import org.ses.android.soap.utils.VisitStatus;
import org.ses.android.soap.utils.VisitUtilities;
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.database.Visita;

import java.util.concurrent.ExecutionException;

// TODO import org.ses.android.soap.tasks.FingerprintSearchTask;

/**
 * Created by anyway on 1/15/16.
 */
public class QuickVisitActivity extends FingerprintBaseActivity {

    private TextView headerText;
    private ImageView imgFingerprint;
    private Button btnScan;
    private Button btnConfirm;

    // TODO FingerprintSearchTask searchPatientFingerprint;
    private AsyncTask<String, String, String> search_fingerprint_task;
    private AsyncTask<String, String, String> asyncTaskString;
    private SharedPreferences mPreferences;
    private AsyncTask<String, String, Visitas[]> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(
                getBaseContext().getApplicationContext());
        setContentView(R.layout.fingerprint_confirm_layout);

        imgFingerprint = (ImageView) findViewById(R.id.imageViewScan1);
        headerText = (TextView) findViewById(R.id.textScan1);
        btnScan = (Button) findViewById(R.id.btnScan1);
        btnConfirm = (Button) findViewById(R.id.btnConfirm1);

        setupScanner();
        imgFingerprint.setImageBitmap(grayBitmap);

        setListeners();

        // Wipe presently stored fingerprint
        PreferencesManager.removeFingerprint(getBaseContext());
    }

    @Override
    public void onPause() {
        imgFingerprint.setImageBitmap(grayBitmap);
        super.onPause();
    }

    public void ScanFingerPrint() {
        byte[] buffer = ScanFingerPrintBase();
        if (buffer != null) {
            imgFingerprint.setImageBitmap(this.toGrayscale(buffer));
            PreferencesManager.setFingerprint(getBaseContext(), mTemplate);
        }

    }


    public void searchFingerPrint() {

        BuscarHuellaTask tarea = new BuscarHuellaTask();
        asyncTaskString = tarea.execute(mPreferences.getString("Fingerprint", ""));
        try {
            String result = asyncTaskString.get();
            if (result.equals("fingerprintNotFound") || (result.equals("someMatchDidntWork"))) {
                Log.v("myActivity", "no fingerprint match found");
                Toast.makeText(getApplicationContext(),
                        getString(R.string.no_fingerprint_match),
                        Toast.LENGTH_SHORT).show();
            } else {
                getParticipantVisits(result);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads all of a participant's visits, finds their pending visit (if any), and continues
     * to the dashboard or to automatic logging as appropriate.
     */
    public void getParticipantVisits(String patientCodigo) {
        ParticipantLoadFromCodigoTask participantTask = new ParticipantLoadFromCodigoTask();
        participantTask.execute(patientCodigo, "bogusurl");
        try {
            Participant participant = participantTask.get();
            if (participant == null) {
                Log.v("myActivity", "error getting participant visits");
            } else {
                Visitas pending_visit = VisitUtilities.getPendingVisit(participant, getBaseContext());
                if (pending_visit == null) {
                    //take to dash
                    takeToDash(participant);
                } else {
                    markMissedOrAttended(participant, pending_visit);
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Given a participant and a visit, marks the participant as having missed the visit if
     * they're late to it or as having attended the visit if they're early/on time. Redirects
     * to the appropriate pages.
     *
     * @param participant
     * @param visitas
     */
    public void markMissedOrAttended(Participant participant, Visitas visitas) {
        if (VisitUtilities.isPastVisitWindow(visitas, getBaseContext())) {
            //mark missed
            boolean missed_success = VisitUtilities.updateVisitStatus(
                    participant, visitas, VisitStatus.MISSED.value(),
                    mPreferences);
            // show toast with error
            if (missed_success) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.visit_missed_success),
                        Toast.LENGTH_SHORT).show();
            } else {
                // show toast with error
                Toast.makeText(getApplicationContext(),
                        getString(R.string.visit_missed_error),
                        Toast.LENGTH_SHORT).show();
            }
            takeToDash(participant);

        } else {
            //mark attended
            boolean attended_success = VisitUtilities.updateVisitStatus(
                    participant, visitas, VisitStatus.ATTENDED.value(),
                    mPreferences);
            // visit successfully confirmed
            // show toast with success
            if (attended_success) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.visit_confirmed_success),
                        Toast.LENGTH_SHORT).show();
            } else {
                // show toast with error
                Toast.makeText(getApplicationContext(),
                        getString(R.string.visit_confirmed_error),
                        Toast.LENGTH_SHORT).show();
            }
            takeToDash(participant);
        }
    }

    public void setListeners() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanFingerPrint();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTemplate == null) {
                    askForRescan();
                }
                searchFingerPrint();
            }
        });
    }

    /**
     * Opens up the dashboard for a particular participant.
     */
    public void takeToDash(Participant participant) {
        Intent i = new Intent(getBaseContext(), ParticipantDashboardActivity.class);
        i.putExtra("Participant", participant);
        startActivity(i);
    }

    /**
     * Asks the patient to re-scan their fingerprint.
     */
    public void askForRescan() {
        headerText.setText(this.getString(R.string.scan_first));
        imgFingerprint.setImageBitmap(grayBitmap);
    }

}
