package org.ses.android.soap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;

import org.ses.android.soap.database.Participant;
import org.ses.android.soap.tasks.AgregarHuellaTask;
import org.ses.android.soap.utils.PreferencesManager;

import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;

/**
 * Created by anyway on 1/11/16.
 */
public class FingerprintConfirmActivity extends FingerprintBaseActivity {

    private Participant currParticipant;

    private TextView headerText;
    private ImageView imgFingerprint;
    private Button btnScan;
    private Button btnConfirm;

    private byte[] storedTemplate;

    AgregarHuellaTask agregarHuellaTask;
    private AsyncTask<String, String, String> agregarHuella;

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

        // Retrieve presently stored fingerprint
        storedTemplate = PreferencesManager.getFingerprint(getBaseContext());
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
                // Compare just-scanned fingerprint to stored fingerprint
                if (mTemplate == null) {
                    askForRescan();
                }

                long sl = SGFDxSecurityLevel.SL_NORMAL; // Set security level as NORMAL
                boolean[] matched = new boolean[1];
                if (jsgfpLib.MatchTemplate(mTemplate, storedTemplate, sl, matched)
                        == SGFDxErrorCode.SGFDX_ERROR_NONE && matched[0]) {

                    // Unpack patient info
                    Bundle patientInfo = getIntent().getExtras();
                    if (patientInfo != null) { // it shouldn't be null
                        currParticipant = patientInfo.getParcelable("Participant");
                    }

                    // Save everything
                    try {
                        Log.v("register", "Try to register fingerprint.");

                        String huella = Base64.encodeToString(storedTemplate, Base64.DEFAULT);
                        agregarHuellaTask = new AgregarHuellaTask();
                        agregarHuella = agregarHuellaTask.execute(currParticipant.CodigoPaciente,
                                huella, "bogusurl");

                        try {
                            String msg = agregarHuella.get();
                            Log.e("agregarHuellaTask", msg);
                            Toast.makeText(getApplicationContext(), getString(R.string.fingerprint_saved), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("agregarHuellaTask", "failed");
                            Toast.makeText(getApplicationContext(), getString(R.string.fingerprint_save_failed), Toast.LENGTH_SHORT).show();
                        }


                        // Continue to patient dashboard activity
                        Intent intent = new Intent(FingerprintConfirmActivity.this, ParticipantDashboardActivity.class);
                        intent.putExtra("Participant", currParticipant);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        askForRescan();
                    }
                } else {
                    askForRescan();
                }
            }
        });
    }

    public void askForRescan() {
        headerText.setText(this.getString(R.string.scan_again));
        imgFingerprint.setImageBitmap(grayBitmap);
    }

}
