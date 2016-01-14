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
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Idreg;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.tasks.GenerarIdENRTask;
import org.ses.android.soap.tasks.GenerarIdTAMTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.TienePermisosTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by svijayakumar2 on 1/12/16.
 */
public class NoMatchActivity extends BaseActivity {

    private String dni;
    private String firstName;
    private String maternalLast;
    private String paternalLast;
    private String dob;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_match_layout);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // load in all the relevant fields the intent passed
        try{
            dni = getIntent().getStringExtra("dni");
        }
        catch(Exception e)
        {
            dni = null;
        }

        try{
            firstName = getIntent().getStringExtra("firstName");
        }
        catch(Exception e)
        {
            firstName = null;
        }

        try{
            maternalLast = getIntent().getStringExtra("maternalLast");
        }
        catch(Exception e)
        {
            maternalLast = null;
        }

        try{
            paternalLast = getIntent().getStringExtra("paternalLast");
        }
        catch(Exception e)
        {
            paternalLast = null;
        }

        try{
            dob = getIntent().getStringExtra("dob");
        }
        catch (Exception e)
        {
            dob = null;
        }



        // "Register Patient" button should open RegisterParticipantActivity
<<<<<<< HEAD
        // Should carry fingerprint data on
        Button buttonRegisterNewParticipant = (Button) findViewById(R.id.btnRegisterNewUser);
=======
        Button buttonRegisterNewParticipant = (Button) findViewById(R.id.btnRegisterNewParticipant);
>>>>>>> 837a859fe9fc95e4514ec0ef38e98331d57ac4f5
        buttonRegisterNewParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoMatchActivity.this, RegisterParticipantActivity.class);
                if (dni != null)
                    intent.putExtra("dni", dni);
                if (firstName != null)
                    intent.putExtra("firstName", firstName);
                if (paternalLast != null)
                    intent.putExtra("paternalLast", paternalLast);
                if (maternalLast !=null)
                    intent.putExtra("maternalLast", maternalLast);
                Log.i("dob", dob);
                if (dob != null)
                    intent.putExtra("dob", dob);

                startActivity(intent);
            }
        });

        // "Add Fingerprint to Existing Patient" button should open AddFingerprintExistingActivity
<<<<<<< HEAD
        // Should carry fingerprint data on
=======
        // if there's a fingerprint, otherwise, get rid of the button
>>>>>>> 33b73cb26e4b6438527af2c431739f72c7153346
        Button buttonAddFingerprintExisting = (Button) findViewById(R.id.btnAddFingerprintExisting);

        if (mPreferences.getString("Fingerprint", "notFound").equals("notFound"))
        {
            buttonAddFingerprintExisting.setVisibility(View.GONE);
        }
        else {
            buttonAddFingerprintExisting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NoMatchActivity.this, AddFingerprintExistingActivity.class);
                    startActivity(intent);
                }
            });
            // "Try Again" button should return to FingerprintFindActivity (Fingerprint search layout)
            Button buttonTryAgain = (Button) findViewById(R.id.btnTryAgain);
            buttonTryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NoMatchActivity.this, FingerprintFindActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
