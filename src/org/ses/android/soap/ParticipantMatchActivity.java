package org.ses.android.soap;

import java.util.ArrayList;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.database.Participant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by anyway on 1/11/16.
 */
public class ParticipantMatchActivity extends BaseActivity {
    private Button btnPatientNotListed;
    private SharedPreferences mPreferences;
    private ListView patients_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participant_match_layout);

        btnPatientNotListed = (Button) findViewById(R.id.btnRegisterNewUser);
        patients_list = (ListView) findViewById(R.id.lstIds);

        // Receive list of matched patients
        Bundle extras = getIntent().getExtras();
        ArrayList<Participant> matches = new ArrayList<Participant>();
        if (extras != null) { // it shouldn't be null
            matches = extras.getParcelableArrayList("matches");
        }

        // Go to Patient Dashboard with only match
        if (matches.size() == 1) {
            Intent intent = new Intent(ParticipantMatchActivity.this, ParticipantDashboardActivity.class);
            intent.putExtra("patient", matches.get(0));
            startActivity(intent);
        }

        // Otherwise populate view
        ArrayAdapter<Participant> participantArrayAdapter = new ArrayAdapter<Participant>(
                this, android.R.layout.simple_list_item_1, matches);
        patients_list.setAdapter(participantArrayAdapter);

        patients_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Participant selected_patient = (Participant) parent.getAdapter().getItem(position);
                Intent intent = new Intent(ParticipantMatchActivity.this, ParticipantDashboardActivity.class);
                intent.putExtra("patient", selected_patient);
                startActivity(intent);
            }
        });

        btnPatientNotListed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParticipantMatchActivity.this, NoMatchActivity.class);
                startActivity(intent);
            }
        });
    }
}
