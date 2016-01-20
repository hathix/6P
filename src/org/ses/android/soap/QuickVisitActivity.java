package org.ses.android.soap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.BuscarHuellaTask;
import org.ses.android.soap.tasks.ParticipantLoadFromCodigoTask;
import org.ses.android.soap.tasks.VisitasListTask;
import org.ses.android.soap.utils.PreferencesManager;

import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;

import org.ses.android.soap.database.Participant;
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
    private Participant participant;
    private Visitas pending_visit;
    private Visitas[] visits;

    String result;
    // TODO FingerprintSearchTask searchPatientFingerprint;
    private AsyncTask<String, String, String> search_fingerprint_task;
    private AsyncTask<String, String, String> asyncTaskString;
    private SharedPreferences mPreferences;
    private AsyncTask<String, String, Visitas[]> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_confirm_layout);

        imgFingerprint = (ImageView) findViewById(R.id.imageViewScan1);
        headerText = (TextView) findViewById(R.id.textScan1);
        btnScan = (Button) findViewById(R.id.btnScan1);
        btnConfirm = (Button) findViewById(R.id.btnConfirm1);

        setupScanner();
        imgFingerprint.setImageBitmap(grayBitmap);

        setListeners();
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
        }
    }


    public void searchFingerPrint() {
        BuscarHuellaTask tarea = new BuscarHuellaTask();
        asyncTaskString = tarea.execute(mPreferences.getString("Fingerprint", ""));
        try {
            result = asyncTaskString.get();
            if (result.equals("fingerprintNotFound") || (result.equals("someMatchDidntWork"))) {
                Log.v("myActivity", "no fingerprint match found");
            } else {
                getParticipantVisits();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void getParticipantVisits() {
        ParticipantLoadFromCodigoTask participantTask = new ParticipantLoadFromCodigoTask();
        participantTask.execute(result, "bogusurl");
        try {
            participant = participantTask.get();
            if (participant == null) {
                Log.v("myActivity", "error getting participant visits");
            } else {

                if (VisitUtilities.hasPendingVisit(participant)) {// this is a participant

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
                // TODO Use mTemplate to do the search with FingerprintSearchTask
                // If found, automatically log visit using auto-scheduling
                // If not found, redirect to register page or something like that
            }
        });
    }

    public void askForRescan() {
        headerText.setText(this.getString(R.string.scan_first));
        imgFingerprint.setImageBitmap(grayBitmap);
    }

}
