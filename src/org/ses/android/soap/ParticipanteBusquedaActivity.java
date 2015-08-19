package org.ses.android.soap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ses.android.seispapp.R;
import org.ses.android.soap.database.Participant;
import org.ses.android.soap.database.PatId;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.IdsListTask;
import org.ses.android.soap.tasks.ParticipantLoadTask;
import org.ses.android.soap.widgets.GrupoBotones;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ParticipanteBusquedaActivity extends Activity {
	private static final int ACTIVITY_VISITLIST=1;
	private static final int VISITLIST_ID = Menu.FIRST;
	private EditText edt_dni_document;
	private Button btnSearch;
	private Button btnShowVisits;
//	private ParticipantDbAdapter mDbHelper;
	private TextView txt_nombresb;
	private TextView txt_ape_patb;
	private TextView txt_ape_matb;
	private TextView txt_fechab;
	private TextView txt_sexob;
	private GrupoBotones btgNavega;
	private AsyncTask<String, String, Participant> asyncTask;
	private SharedPreferences mPreferences ;
	String nombres,doc_identidad,codigo_proyecto;
    Boolean partipantExist=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participante_busqueda_layout);
		
		edt_dni_document = (EditText)findViewById(R.id.edt_dni_document);
		txt_nombresb  = (TextView)findViewById(R.id.txt_nombresb);
		txt_ape_patb  = (TextView)findViewById(R.id.txt_ape_patb);
		txt_ape_matb  = (TextView)findViewById(R.id.txt_ape_matb);
		txt_fechab  = (TextView)findViewById(R.id.txt_fechab);
		txt_sexob  = (TextView)findViewById(R.id.txt_sexob);
//
		btnSearch = (Button)findViewById(R.id.btnSearch);
		btgNavega = (GrupoBotones)findViewById(R.id.btgNavega);
		btnShowVisits = (Button)findViewById(R.id.btnShowVisits);

		
		btnSearch.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ParticipantLoadTask tarea = new ParticipantLoadTask();
			doc_identidad = edt_dni_document.getText().toString();
			
            mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                    getString(R.string.default_server_url));
            String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID,"");
            Log.i("codigousuario",codigousuario);
            asyncTask=tarea.execute(doc_identidad,url);
            Participant objParticipante;
            try {
                objParticipante = asyncTask.get();
                Log.i("doc_identidad:",doc_identidad );

                if (objParticipante == null){
                    partipantExist=false;
                    txt_nombresb.setText("");
                    txt_ape_patb.setText("");
                    txt_ape_matb.setText("");
                    txt_fechab.setText("");
                    txt_sexob.setText("");
                    TextView lbl_noids ;
                    ListView lstIds;
                    lbl_noids = (TextView)findViewById(R.id.lbl_noIds);
                    lstIds = (ListView)findViewById(R.id.lstIds);
                    lbl_noids.setText(R.string.no_ids);
                    lstIds.setAdapter(null);
                    Toast.makeText(getBaseContext(), "No existe partipante con ese DNI!!",Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("CodigoPaciente:",objParticipante.CodigoPaciente );
                    partipantExist=true;
                    txt_nombresb.setText(objParticipante.Nombres);
                    txt_ape_patb.setText(objParticipante.ApellidoPaterno);
                    txt_ape_matb.setText(objParticipante.ApellidoMaterno);
                    txt_fechab.setText(objParticipante.FechaNacimiento);
                    nombres = objParticipante.ApellidoPaterno.trim()+" "+objParticipante.ApellidoMaterno.trim()+" "+
                            objParticipante.Nombres.trim();
                    String msexo="Masculino";
                    if (objParticipante.Sexo==2) msexo="Femenino";
                    txt_sexob.setText( msexo);
                    //
                    showIds(objParticipante.CodigoPaciente, codigousuario, url);
                    //
                    Editor editor = mPreferences.edit();
                    editor.putString("CodigoPaciente",objParticipante.CodigoPaciente);
                    editor.putString("patient_name",nombres);

                    editor.commit();
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

		});
		
        btgNavega.setOnAnteriorListener(new GrupoBotones.OnAnteriorListener() {
			
			@Override
			public void OnAnterior() {
				Intent intent=new Intent(ParticipanteBusquedaActivity.this,Menu_principal.class); 
				startActivity(intent); 			
			}
		});

        btgNavega.setOnSiguienteListener(new GrupoBotones.OnSiguienteListener() {
			
			@Override
			public void OnSiguiente() {
				Intent intent=new Intent(ParticipanteBusquedaActivity.this,ParticipanteVisitaActivity.class); 
				startActivity(intent); 		
			}
		});		
		
		btnShowVisits.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {

            visitList();
			}
//		}
		});
		        
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, VISITLIST_ID, 0, R.string.menu_visitlist);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case VISITLIST_ID:
                visitList();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    private void visitList() {
        if (partipantExist==true){
            Intent i = new Intent(this, VisitListActivity.class);
            startActivityForResult(i, ACTIVITY_VISITLIST);
        }
    }
    public void showIds(String codigopaciente,String codigousuario,String url){
//        AdaptadorIds adaptador;
        PatId[] datos ;

        AsyncTask<String, String, PatId[]> loadIdsList;
        IdsListTask tareaIds = new IdsListTask();
        TextView lbl_noids ;
        //GridView lstIds;
        ListView lstIds;
        loadIdsList = tareaIds.execute(codigopaciente,codigousuario,url);
        ArrayList<PatId> idsArray = new ArrayList<PatId>();

        try {
            datos = loadIdsList.get();

            /**
             * set item into adapter
             */
            lbl_noids = (TextView)findViewById(R.id.lbl_noIds);
            //lstIds = (GridView)findViewById(R.id.lstIds);
            lstIds = (ListView)findViewById(R.id.lstIds);

            if (datos != null){
                //Log.i("showIds:datos",datos.toString());
                //Establece Codigo de Proyecto del Participante
                mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                codigo_proyecto = datos[0].CodigoProyecto;
                Editor editor = mPreferences.edit();
                editor.putString("CodigoProyecto",codigo_proyecto);
                editor.commit();
                //
                /**
                 * add item in arraylist
                 */
                for (int i = 0; i < datos.length; i++)
                {
                    if (datos[i].IdTAM.equals("anyType{}")) datos[i].IdTAM="";
                    if (datos[i].IdENR.equals("anyType{}")) datos[i].IdENR="";
                }
                String[] idData = new String[datos.length];
                for (int j = 0; j < datos.length; j++)
                {
                    idData[j] = "("+datos[j].Proyecto + ")IdTAM:" +datos[j].IdTAM + "/IdENR:" +datos[j].IdENR;
                }

                ArrayAdapter<String> adaptador1 =
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idData);

                lstIds.setAdapter(adaptador1);

            }else{
                lbl_noids.setText(R.string.no_ids);
            }

        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

}
