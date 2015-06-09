/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ses.android.soap;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.ses.android.seispapp.R;
import org.ses.android.soap.database.Idreg;
import org.ses.android.soap.database.Visitas;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.EstadoVisitaTask;
import org.ses.android.soap.tasks.FormList1Task;
import org.ses.android.soap.tasks.FormListTask;
import org.ses.android.soap.tasks.MostrarTipoIDTask;
import org.ses.android.soap.tasks.RegistrarParticipanteTask;
import org.ses.android.soap.tasks.VisitaListTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VisitListActivity extends Activity {
	static final int DATE_DIALOG_ID = 999;
	private static final int ACTIVITY_VISITLIST=1;
	private static final int PROGRESS_DIALOG = 1;
	private String url= "";
	String codigopaciente = "";
	String patientname = "";
    String codigoproyecto = "";
	private AsyncTask<String, String, Visitas[]> loadVisitas;
	SharedPreferences mPreferences ;	
	private String mAlertMsg;
	private TextView lbl_novisits;
	private ListView lstVisit;
	private TextView lbl_nombres;
	private Visitas[] datos;
	private Visitas vis;

    public ProgressDialog mProgressDialog;
	private VisitaListTask mVisitaListTask;
	private AsyncTask<String, String, String> formListTask;
	private AsyncTask<String, String, String> estadoVisita;
    private AsyncTask<String,String,Idreg[]> mostrarTipoID;
    private Idreg[] tipoID;
    String participantID = "";
    String participantName = "";
    private AsyncTask<String, String, String> formList1Task;
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visits_list);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));		
		lbl_nombres = (TextView) findViewById(R.id.lbl_nombres);
    	codigopaciente = mPreferences.getString("CodigoPaciente", "");
    	patientname = getString(R.string.txtvisit)+"("+mPreferences.getString("patient_name", "")+")";
    	lbl_nombres.setText(patientname);
    	mAlertMsg = getString(R.string.please_wait);
        codigoproyecto = mPreferences.getString("CodigoProyecto", "");
        if (getLastNonConfigurationInstance() instanceof VisitaListTask) {
            mVisitaListTask = (VisitaListTask) getLastNonConfigurationInstance();
            if (mVisitaListTask.getStatus() == AsyncTask.Status.FINISHED) {
                try {
                    dismissDialog(PROGRESS_DIALOG);
                } catch (IllegalArgumentException e) {
                    Log.i("Login", "Attempting to close a dialog that was not previously opened");
                }
                mVisitaListTask = null;
            }
        }

		showDialog(PROGRESS_DIALOG);
        loadVisitsListView();
        dismissDialog(PROGRESS_DIALOG);

    	
        lstVisit.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Log.i("List View Clicked", "**********");
                Toast.makeText(VisitListActivity.this,
                  "List View Clicked:" + position, Toast.LENGTH_LONG)
                .show();
            }
           
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	public void loadVisitsListView(){
		VisitaListTask tareaVisits = new VisitaListTask();
		AdaptadorVisitas adaptador;

        String codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID,"");
		loadVisitas = tareaVisits.execute(codigopaciente,codigousuario,codigoproyecto,url);

		try {
			ArrayList<Visitas> visitasArray = new ArrayList<Visitas>();
			datos = loadVisitas.get();

			  /**
			   * set item into adapter
			   */	            
	        lbl_novisits = (TextView)findViewById(R.id.lbl_novisits);
	        lstVisit = (ListView)findViewById(R.id.lstVisit);
			if (datos != null){
				  /**
				   * add item in arraylist
				   */
				for (int i = 0; i < datos.length; i++) 
				{
					 visitasArray.add(datos[i]);
				}	        
		        adaptador = new AdaptadorVisitas(VisitListActivity.this, R.layout.visits_row,
					    visitasArray);
		        lstVisit.setItemsCanFocus(false);
		        lstVisit.setAdapter(adaptador);
		    }else{
		        lbl_novisits.setText(R.string.no_visits);
			}

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	protected Dialog onCreateDialog(int id) {
	        switch (id) {
	        case PROGRESS_DIALOG:
	            mProgressDialog = new ProgressDialog(this);
	            mProgressDialog = new ProgressDialog(this);
	            mProgressDialog.setTitle(getString(R.string.downloading_data));
	            mProgressDialog.setMessage(mAlertMsg);
	            mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
	            mProgressDialog.setIndeterminate(true);
	            mProgressDialog.setCancelable(false);
	            return mProgressDialog;	        
	        }
	        return null;
    }
	
    class AdaptadorVisitas extends ArrayAdapter<Visitas> {
    	
    	 Context context;
    	int layoutResourceId;
    	ArrayList<Visitas> data = new ArrayList<Visitas>();
    	
    	AdaptadorVisitas(Context context, int layoutResourceId,
    			   ArrayList<Visitas> data) {
			  super(context, layoutResourceId, data);
			  this.layoutResourceId = layoutResourceId;
			  this.context = context;
			  this.data = data;
    	}
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		  View row = convertView;
    		  VisitHolder holder = null;
    		  vis = data.get(position);
    		  if (row == null) {
    			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
    			row = inflater.inflate(layoutResourceId, parent, false);
    			holder = new VisitHolder(); 
	   			holder.lblTitulo= (TextView) row.findViewById(R.id.LblTitulo);
	   			holder.lblSubtitulo= (TextView) row.findViewById(R.id.LblSubTitulo);
	   			holder.btnDone = (Button) row.findViewById(R.id.btnDone);
	   			holder.btnSeisD = (Button) row.findViewById(R.id.btnSeisD);
	   			holder.btnDone.setEnabled(false);
	   			holder.btnSeisD.setEnabled(false);
	    		if (vis.EstadoVisita.equals("Pendiente")){
					holder.btnDone.setEnabled(true);
					holder.btnSeisD.setEnabled(true);
	    		}   			
    			row.setTag(holder);
    		  } else {
    			  holder = (VisitHolder) row.getTag();
    		  }

    		  holder.lblTitulo.setText(vis.Proyecto+"/"+vis.Visita);
    		  holder.lblSubtitulo.setText(vis.FechaVisita+"-"+vis.HoraCita+"-"+vis.EstadoVisita);

    		  holder.btnDone.setOnClickListener(new OnClickListener() {

    			   @Override
    			   public void onClick(View v) {
    			    // TODO Auto-generated method stub
    			    Log.i("btnDone Clicked", "**********");
//    			    Toast.makeText(context, "btnDone Clicked",
//    			      Toast.LENGTH_LONG).show();
//    			    actualizarVisita(position,data);
    			    
    			    //
    	        	AlertDialog.Builder builder = new AlertDialog.Builder(VisitListActivity.this);
    	        	builder.setMessage(getString(R.string.question_visit_done))
    	        	        .setTitle(getString(R.string.opc_visit))
    	        	        .setCancelable(false)
    	        	        .setPositiveButton(getString(R.string.answer_yes),
    	        	                new DialogInterface.OnClickListener() {
    	        	                    @Override
    									public void onClick(DialogInterface dialog, int id) {
											actualizarVisita(position,data);
//	    	        	                        finish();
    	        	                    }
    	        	                })
    	        	        .setNegativeButton(getString(R.string.answer_no),
    	 	                new DialogInterface.OnClickListener() {
    	 	                    @Override
    							public void onClick(DialogInterface dialog, int id) {
    	 	                        dialog.cancel();
    	 	                    }
    	 	                });
    	        	AlertDialog alert = builder.create();
    	        	alert.show(); 
    			    //
    			   }
    		  });
    		  holder.btnSeisD.setOnClickListener(new OnClickListener() {

	   			   @Override
	   			   public void onClick(View v) {
	   			    // TODO Auto-generated method stub
	   			    Log.i("btnSeisD Clicked", "**********");
//	   			    Toast.makeText(context, "btnSeisD Clicked",
//	   			      Toast.LENGTH_LONG).show();
		      
    			    call1SeisD(position,data);
	   			   }
   			  });    		  
    		  
			return(row);
		}
    	
    }
    static class VisitHolder {
    	  TextView lblTitulo;
    	  TextView lblSubtitulo;
    	  Button btnDone;
    	  Button btnSeisD;
    }  
    public void callSeisD(int pos,ArrayList<Visitas> data){
      mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    //testSP
      SharedPreferences prefs = getSharedPreferences("demopref",Context.MODE_WORLD_READABLE);
    //testSP
      String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
              getString(R.string.default_server_url));		        
      String userid = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
      String local_id = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
      Visitas visrow = data.get(pos);
      String project_id = visrow.CodigoProyecto;
      String visit_group_id = visrow.CodigoGrupoVisita;
      String visit_id = visrow.CodigoVisita;     

      Editor editor = mPreferences.edit();
      
		FormListTask formList=new FormListTask();
		formListTask=formList.execute(userid,local_id,project_id,visit_group_id,visit_id,url);
//		String filterForms;
        // JT:2015-05-15, Obtener ID de Tamizaje del Participante
        MostrarTipoIDTask mostrarID =  new MostrarTipoIDTask();
        mostrarTipoID = mostrarID.execute(local_id,project_id,codigopaciente,url);
        // JT:2015-05-15, Obtener ID de Tamizaje del Participante
		try {
			String filterForms = formList.get();

			Log.i("menu", ".filterForms:"+filterForms );
			editor.putString(PreferencesActivity.KEY_FILTERFORMS, filterForms);
			editor.commit();
			//testSP
			SharedPreferences.Editor editor1 = prefs.edit();
			editor1.clear();
			editor1.commit();
            editor1.putString("stringFilterForms", filterForms);

            // JT:2015-05-15, Obtener ID de Tamizaje del Participante
            tipoID = mostrarTipoID.get();
            if (tipoID[0].IdTAM != null){
                participantID = tipoID[0].IdTAM;
                participantName = tipoID[0].NombreCompleto;
            }
            editor1.putString("stringParticipantID", participantID);
            editor1.putString("stringParticipantName", participantName);
            // JT:2015-05-15, Obtener ID de Tamizaje del Participante
            editor1.commit();
			Intent i;
			PackageManager manager = getPackageManager();
			try {
			    i = manager.getLaunchIntentForPackage("org.odk.collect.android");
			    if (i == null)
			        throw new PackageManager.NameNotFoundException();
			    i.addCategory(Intent.CATEGORY_LAUNCHER);
//			    i.addCategory(Intent.CATEGORY_HOME);
//			    i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			    startActivity(i);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

    }
    public void actualizarVisita(int pos,ArrayList<Visitas> data){
    	EstadoVisitaTask mEstadoVisitaTask = new EstadoVisitaTask();
        String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));		        
        String codigopaciente = mPreferences.getString("CodigoPaciente", "");
        String local_id = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
        String user_id = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        Visitas visrow = data.get(pos);
  
        String project_id = visrow.CodigoProyecto;
        String visit_id = visrow.CodigoVisita;   		
        String visits_id = visrow.CodigoVisitas;  
        String estado_visita_id = "3";
        String codigo_estatus_paciente = "1";
        estadoVisita=mEstadoVisitaTask.execute(
        		local_id,
        		project_id,
        		visit_id,
				visits_id,
				codigopaciente,
				estado_visita_id,
				codigo_estatus_paciente,
				user_id,
				url);

		String actualizado;
		try {
			actualizado = estadoVisita.get();
			if (!actualizado.equals("OK")){
				Toast.makeText(getBaseContext(), "No se actualizo Estado de Visita!!",Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getBaseContext(), "Estado de Visita actualizado!!",Toast.LENGTH_SHORT).show();
			}	
            finish();
	        Intent i = new Intent(this, VisitListActivity.class);
	        startActivityForResult(i, ACTIVITY_VISITLIST);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void call1SeisD(int pos,ArrayList<Visitas> data){
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //testSP
        SharedPreferences prefs = getSharedPreferences("demopref",Context.MODE_WORLD_READABLE);
        //testSP
        String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));
        String userid = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
        String local_id = mPreferences.getString(PreferencesActivity.KEY_LOCAL_ID, "");
        Visitas visrow = data.get(pos);
        String project_id = visrow.CodigoProyecto;
        String visit_group_id = visrow.CodigoGrupoVisita;
        String visit_id = visrow.CodigoVisita;

        Editor editor = mPreferences.edit();

        FormList1Task formList1=new FormList1Task();
        Log.i("call1SeisD", ".userid:"+userid );
        Log.i("call1SeisD", ".local_id:"+local_id );
        Log.i("call1SeisD", ".project_id:"+project_id );
        formList1Task=formList1.execute(userid,local_id,project_id,url);
//		String filterForms;
        // JT:2015-05-15, Obtener ID de Tamizaje del Participante
        MostrarTipoIDTask mostrarID =  new MostrarTipoIDTask();
        mostrarTipoID = mostrarID.execute(local_id,project_id,codigopaciente,url);
        // JT:2015-05-15, Obtener ID de Tamizaje del Participante
        try {
            String filterForms1 = formList1.get();

            Log.i("call1SeisD", ".filterForms1:"+filterForms1 );
            editor.putString(PreferencesActivity.KEY_FILTERFORMS, filterForms1);
            editor.commit();
            //testSP
            SharedPreferences.Editor editor1 = prefs.edit();
            editor1.clear();
            editor1.commit();
            editor1.putString("stringFilterForms", filterForms1);

            // JT:2015-05-15, Obtener ID de Tamizaje del Participante
            tipoID = mostrarTipoID.get();
            if (tipoID[0].IdTAM != null){
                participantID = tipoID[0].IdTAM;
                participantName = tipoID[0].NombreCompleto;
            }
            Log.i("call1SeisD", ".participantID:"+participantID );
            Log.i("call1SeisD", ".participantName:"+participantName );

            editor1.putString("stringParticipantID", participantID);
            editor1.putString("stringParticipantName", participantName);
            // JT:2015-05-15, Obtener ID de Tamizaje del Participante
            editor1.commit();

            Log.i("call1SeisD", ".participantID:"+participantID);
            Log.i("call1SeisD", ".filterForms1:"+filterForms1);

            Intent intents = new Intent(Intent.ACTION_MAIN);
            intents.setComponent(new ComponentName("org.odk.collect.android", "org.odk.collect.android.activities.MainMenuActivity"));
            intents.putExtra("idParticipante", participantID);
            intents.putExtra("idProyecto", filterForms1);

            startActivity(intents);

        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

    }

}

