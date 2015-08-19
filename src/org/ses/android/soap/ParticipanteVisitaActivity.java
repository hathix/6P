package org.ses.android.soap;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import org.ses.android.seispapp.R;
import org.ses.android.soap.database.Local;
import org.ses.android.soap.database.Proyecto;
import org.ses.android.soap.database.Visita;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.EstadoENRTask;
import org.ses.android.soap.tasks.EstadoTAMTask;
import org.ses.android.soap.tasks.GenerarVisitaTask;
import org.ses.android.soap.tasks.LocalLoadTask;
import org.ses.android.soap.tasks.ProyectoLoadTask;
import org.ses.android.soap.tasks.VisitaLoadTask;
import org.ses.android.soap.utilities.UrlUtils;
import org.ses.android.soap.widgets.GrupoBotones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ParticipanteVisitaActivity extends Activity {
	
	private EditText edt_nombres;
	private Spinner spnLocal;
	private Spinner spnProyecto;
	private Spinner spnGrupo;
	private Spinner spnVisita;
	
	private GrupoBotones btgNavega;
	
//	private TextView tvDisplayDate;
//	private EditText edt_hora_visita;
//	private Button btnChangeDate;
	private TextView tvwfecha_visita;
	private TextView tvwhora_visita;	
	private int year;
	private int month;
	private int day;
 
//	private TimePicker timePicker1;
	private int hour;
	private int minute;
	
	static final int DATE_DIALOG_ID = 1;
	static final int TIME_DIALOG_ID = 2;
	private String url= "";
	String codigopaciente = "";
	String selLocal = "";
	String selProyecto  = "";
	String selGrupo  = "";
	String selVisita  = "";
	String fec_visita  = "";
	String hora_visita = "";
	String codigousuario = ""; 
	GenerarVisitaTask tarea;
	private AsyncTask<String, String, String> generarVisita;
	private AsyncTask<String, String, Local[]> loadLocal;
	private AsyncTask<String, String, Proyecto[]> loadProyecto;
	private AsyncTask<String, String, Visita[]> loadVisita;
    //private AsyncTask<String, String, String> loadEstadoENR_TAM;
    private AsyncTask<String, String, String> loadEstadoENR;
    private AsyncTask<String, String, String> loadEstadoTAM;
    EstadoENRTask estadoENR;
    EstadoTAMTask estadoTAM;
	SharedPreferences mPreferences ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participante_visita_layout);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));
        String nombres = mPreferences.getString("patient_name", "");
        Log.i("URL",url);
		edt_nombres = (EditText)findViewById(R.id.edt_nombres);
//		tvDisplayDate = (TextView) findViewById(R.id.tvDate);
		tvwfecha_visita = (TextView) findViewById(R.id.tvwfecha_visita);
//		edt_hora_visita = (EditText)findViewById(R.id.edt_hora_visita);
		tvwhora_visita = (TextView) findViewById(R.id.tvwhora_visita);
		setCurrentDateOnView();
		addListenerOntvwfecha_visita();
		setCurrentTimeOnView();
		addListenerOntvwhora_visita();

		edt_nombres.setText(nombres);
		
        btgNavega = (GrupoBotones)findViewById(R.id.btgNavega);
		spnLocal = (Spinner) findViewById(R.id.spnLocal);
		spnProyecto = (Spinner) findViewById(R.id.spnProyecto);
		spnGrupo = (Spinner) findViewById(R.id.spnGrupo);
		spnVisita = (Spinner) findViewById(R.id.spnVisita);
		
		loadLocalSpinner();

		spnLocal.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent,
				android.view.View v, int position, long id) {
					selLocal = Integer.toString(position);
                    codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");
                    Log.i("Visita","Seleccionado(1): pos: "+ selLocal + " valor:" + parent.getItemAtPosition(position));
                    if ( selLocal != null) {
                        if (!selLocal.equals("0")) loadProyectoSpinner(selLocal,codigousuario);
                    }

					Log.i("Visita","Seleccionado(2): pos: "+ selLocal + " valor:" + parent.getItemAtPosition(position));
                    Log.i("Visita"," valor:" + selLocal);
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					Toast.makeText(getBaseContext(), "Seleccione un Local!!",Toast.LENGTH_SHORT).show();
				}
		});

		spnProyecto.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent,
						android.view.View v, int position, long id) {
					//selProyecto = parent.getItemAtPosition(position).toString().substring(0,1);
                    // 20 - XXX
                    // 01234567
                    selProyecto = parent.getItemAtPosition(position).toString();
                    selProyecto = selProyecto.substring(0,selProyecto.indexOf("-",0)-1).trim();
					codigopaciente = mPreferences.getString("CodigoPaciente", "");
					Log.i("Visita","codigopaciente:"+codigopaciente+",selLocal:"+selLocal+",selProyecto:"+selProyecto);
					if (position > -1) loadVisitaSpinner(codigopaciente,selLocal,selProyecto);
					Log.i("Visita","Proyecto: pos: "+ selProyecto + " valor:" + parent.getItemAtPosition(position));
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					Toast.makeText(getBaseContext(), "Seleccione un Proyecto!!",Toast.LENGTH_SHORT).show();
				}
		});
		
		spnGrupo.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent,
				android.view.View v, int position, long id) {
					selGrupo = parent.getItemAtPosition(position).toString().substring(0,1);
//					if (!selGrupo.equals("0")) loadProyectoSpinner(selGrupo);
					Log.i("Visita","Grupo: pos: "+ selGrupo + " valor:" + parent.getItemAtPosition(position));
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					Toast.makeText(getBaseContext(), "Seleccione un Local!!",Toast.LENGTH_SHORT).show();
				}
		});		
		
		spnVisita.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent,
				android.view.View v, int position, long id) {
                    // JT:20150817
				    //selVisita = parent.getItemAtPosition(position).toString().substring(0,1);
                    selVisita = parent.getItemAtPosition(position).toString();
                    selVisita = selVisita.substring(0,selVisita.indexOf("-",0)-1).trim();

//					if (!selVisita.equals("0")) loadProyectoSpinner(selVisita);
					Log.i("Visita","Visita: pos: "+ selVisita + " valor:" + parent.getItemAtPosition(position));
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					Toast.makeText(getBaseContext(), "Seleccione un Local!!",Toast.LENGTH_SHORT).show();
				}
		});	
		
        btgNavega.setOnAnteriorListener(new GrupoBotones.OnAnteriorListener() {
			
			@Override
			public void OnAnterior() {
				Intent intent=new Intent(ParticipanteVisitaActivity.this,ParticipanteBusquedaActivity.class); 
				startActivity(intent); 				}
		});

        btgNavega.setOnSiguienteListener(new GrupoBotones.OnSiguienteListener() {
			
			@Override
			public void OnSiguiente() {


		        	codigopaciente = mPreferences.getString("CodigoPaciente", "");
		        	Log.i("codigopaciente",codigopaciente);
		        	codigousuario = mPreferences.getString(PreferencesActivity.KEY_USERID, "");

		        	fec_visita = tvwfecha_visita.getText().toString();
        			Log.i("fec_visita:",fec_visita);
        			
        			hora_visita = tvwhora_visita.getText().toString();
        			final Calendar c = Calendar.getInstance();
        			year = c.get(Calendar.YEAR);
        			month = c.get(Calendar.MONTH);
        			day = c.get(Calendar.DAY_OF_MONTH);
        			StringBuilder sb = new StringBuilder()
        			.append(day).append("/").append(month + 1).append("/")
        			.append(year).append(" ");
        			String fec_hoy = sb.toString();
        			long elapsedDays = UrlUtils.daysBetween(fec_visita,fec_hoy);
        			Log.i("Visita","elapsedDays: "+ Long.toString(elapsedDays));
        			Log.i("Visita","Loc:"+selLocal+"-Proy:"+selProyecto+"-Grupo:"+selGrupo+"-Vis:"+selVisita);
        			Log.i("Visita","codigopaciente:"+codigopaciente+"-fec_visita:"+fec_visita+"-hora_visita:"+hora_visita+"-codigousuario:"+codigousuario);
    				if (selLocal.equals("0")){
                        Toast.makeText(getBaseContext(), "Elija Local!!",Toast.LENGTH_SHORT).show();
                    }else{
	    				if (selProyecto.equals("")){
	    					Toast.makeText(getBaseContext(), "Elija Proyecto!!",Toast.LENGTH_SHORT).show();
		        		}else{
		    				if (selGrupo.equals("")){
		    					Toast.makeText(getBaseContext(), "Elija Grupo!!",Toast.LENGTH_SHORT).show();
			        		}else{
			    				if (selVisita.equals("")){
			    					Toast.makeText(getBaseContext(), "Elija Visita!!",Toast.LENGTH_SHORT).show();
				        		}else{
									if (elapsedDays > 0){
										Toast.makeText(getBaseContext(), "Fecha de Visita invalida, F.Visita > F.Hoy!!",Toast.LENGTH_SHORT).show();
					        		}else{
					        			if (!UrlUtils.validData(hora_visita, "^(0?[0-9]|1?[0-9]|2[0-3]):[0-5][0-9]$")){
					        				Toast.makeText(getBaseContext(), "Hora de Visita invalida!!",Toast.LENGTH_SHORT).show();
					        			}else{
					        				// Alert Dialog
					        	        	AlertDialog.Builder builder = new AlertDialog.Builder(ParticipanteVisitaActivity.this);
					        	        	builder.setMessage("Desea Generar Visita?")
					        	        	        .setTitle("Advertencia")
					        	        	        .setCancelable(false)
					        	        	        .setPositiveButton("Si",
					        	        	                new DialogInterface.OnClickListener() {
					        	        	                    @Override
					        									public void onClick(DialogInterface dialog, int id) {
                                                                    GenerarVisitaTask tarea = new GenerarVisitaTask();
					    					        				//int CodigoLocal, int CodigoProyecto, int CodigoVisita, string CodigoPaciente, string FechaVisita, string HoraCita, int CodigoUsuario
					    						        			generarVisita = tarea.execute(
					    						        					selLocal,
					    						        					selProyecto,
					    						        					selGrupo,
					    						        					selVisita,
					    						        					codigopaciente,
					    						        					fec_visita,
					    						        					hora_visita,
					    						        					codigousuario,
					    						        					url);

					    						        			String guardado;
                                                                    String estadoENR;
                                                                    String estadoTAM;
																	try {


																		guardado = generarVisita.get();
						    											if (!guardado.equals("OK")){
						    												Toast.makeText(getBaseContext(), "No se creo visita!!",Toast.LENGTH_SHORT).show();
						    											}else{
						    												Toast.makeText(getBaseContext(), "Datos guardados!!",Toast.LENGTH_SHORT).show();
                                                                            EstadoENRTask tareaEstadoENR = new EstadoENRTask();
                                                                            EstadoTAMTask tareaEstadoTAM = new EstadoTAMTask();
                                                                            loadEstadoENR = tareaEstadoENR.execute("ENR",selProyecto,url);
                                                                            estadoENR = loadEstadoENR.get();
                                                                            loadEstadoTAM = tareaEstadoTAM.execute("TAM",selProyecto,url);
                                                                            estadoTAM = loadEstadoTAM.get();
                                                                            //selGrupo=1 TAM, selGrupo=2  ENR
                                                                            Log.i("Visita","estadoENR: "+ estadoENR.toString() + "--- estadoTAM: "+ estadoTAM.toString());
//                                                                            if (estadoENR.equals("1") || estadoTAM.equals("1")){
//                                                                                // Asignar IDs de acuerdo al tipo de visita (TAM o ENR)
//                                                                                if ((selGrupo.equals("1") || selGrupo.equals("2"))  && selVisita.equals("1")) {
//                                                                                    Intent pass = new Intent(getApplicationContext(),ParticipanteAsignarIdActivity.class);
//                                                                                    Bundle extras = new Bundle();
//                                                                                    extras.putString("selLocal", selLocal);
//                                                                                    extras.putString("selProyecto", selProyecto);
//                                                                                    extras.putString("codigopaciente",codigopaciente);
//                                                                                    extras.putString("selGrupo", selGrupo);
//                                                                                    extras.putString("selVisita",selVisita);
//                                                                                    extras.putString("codigousuario",codigousuario);
//                                                                                    extras.putString("url",url);
//                                                                                    pass.putExtras(extras);
//                                                                                    startActivity(pass);
//                                                                                }
//                                                                            }
                                                                            Boolean asignarID = false;
                                                                            if (estadoTAM.equals("1") && estadoENR.equals("1")){
                                                                                if ((selGrupo.equals("1") || selGrupo.equals("2"))  && selVisita.equals("1")) {
                                                                                    asignarID = true;
                                                                                }
                                                                            }
                                                                            if (estadoTAM.equals("0") && estadoENR.equals("1")){
                                                                                if ((selGrupo.equals("2"))  && selVisita.equals("1")) {
                                                                                    asignarID = true;
                                                                                }
                                                                            }
                                                                            if (estadoTAM.equals("1") && estadoENR.equals("0")){
                                                                                if (selGrupo.equals("1") && selVisita.equals("1")) {
                                                                                    asignarID = true;
                                                                                }
                                                                            }
                                                                            if (asignarID.equals(true)){
                                                                                // Asignar IDs de acuerdo al tipo de visita (TAM o ENR)
                                                                                //if ((selGrupo.equals("1") || selGrupo.equals("2"))  && selVisita.equals("1")) {
                                                                                    Intent pass = new Intent(getApplicationContext(),ParticipanteAsignarIdActivity.class);
                                                                                    Bundle extras = new Bundle();
                                                                                    extras.putString("selLocal", selLocal);
                                                                                    extras.putString("selProyecto", selProyecto);
                                                                                    extras.putString("codigopaciente",codigopaciente);
                                                                                    extras.putString("selGrupo", selGrupo);
                                                                                    extras.putString("selVisita",selVisita);
                                                                                    extras.putString("codigousuario",codigousuario);
                                                                                    extras.putString("url",url);
                                                                                    pass.putExtras(extras);
                                                                                    startActivity(pass);
                                                                                //}
                                                                            }
						    											}	
						        	        	                        finish();
																	} catch (InterruptedException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	} catch (ExecutionException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	}

                                                                }
					        	        	                })
					        	        	        .setNegativeButton("No",
					        	 	                new DialogInterface.OnClickListener() {
					        	 	                    @Override
					        							public void onClick(DialogInterface dialog, int id) {
					        	 	                        dialog.cancel();
					        	 	                    }
					        	 	                });
					        	        	AlertDialog alert = builder.create();
					        	        	alert.show(); 
										}
					        			
									}
		        			
				        		}
			        		}
		        		}
	        		}

			}
		});	
        
//        InputFilter[] timeFilter = new InputFilter[1];
        InputFilter timeFilter;
        timeFilter   = new InputFilter() {
	        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
	                int dstart, int dend) {
	
	
	            if (source.length() == 0) {
	                return null;// deleting, keep original editing
	            }
	            String result = "";
	            result += dest.toString().substring(0, dstart);
	            result += source.toString().substring(start, end);
	            result += dest.toString().substring(dend, dest.length());
	
	            if (result.length() > 5) {
	                return "";// do not allow this edit
	            }
	            boolean allowEdit = true;
	            char c;
	            if (result.length() > 0) {
	                c = result.charAt(0);
	                allowEdit &= (c >= '0' && c <= '2' && !(Character.isLetter(c)));
	            }
	            if (result.length() > 1) {
	                c = result.charAt(1);
	                allowEdit &= (c >= '0' && c <= '9' && !(Character.isLetter(c)));
	            }
	            if (result.length() > 2) {
	                c = result.charAt(2);
	                allowEdit &= (c == ':'&&!(Character.isLetter(c)));
	            }
	            if (result.length() > 3) {
	                c = result.charAt(3);
	                allowEdit &= (c >= '0' && c <= '5' && !(Character.isLetter(c)));
	            }
	            if (result.length() > 4) {
	                c = result.charAt(4);
	                allowEdit &= (c >= '0' && c <= '9'&& !(Character.isLetter(c)));
	            }
	            return allowEdit ? null : "";
	        }
        };

        tvwhora_visita.setFilters(new InputFilter[] { timeFilter });		

	}
	

	// display current date
	public void setCurrentDateOnView() {
 
//		tvDisplayDate = (TextView) findViewById(R.id.tvDate);

//		dpResult = (DatePicker) findViewById(R.id.dpResult);
 
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
//		edt_hora_visita.setText(c.getTime().toString());
		


		// set current date into textview
//		tvDisplayDate.setText(new StringBuilder()
//			// Month is 0 based, just add 1
//			.append(day).append("/").append(month + 1).append("/")
//			.append(year).append(" "));
 
		tvwfecha_visita.setText(new StringBuilder()
		// Month is 0 based, just add 1
		.append(day).append("/").append(month + 1).append("/")
		.append(year).append(" "));
		
//		dpResult.init(year, month, day, null);
 
	}
 
	public void addListenerOntvwfecha_visita() {
		tvwfecha_visita.setOnTouchListener(new OnTouchListener(){
			 
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(DATE_DIALOG_ID);
				return true;
			}

 
		});

 
		tvwfecha_visita.setOnClickListener(new OnClickListener() {
 
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
 
				showDialog(DATE_DIALOG_ID);
 
			}
 
		});
 
	}
	
	// display current date
	public void setCurrentTimeOnView() {
 
 
		final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
//		String sHour = "0"+String.valueOf(hour);
//		String sMinute = "0"+String.valueOf(minute);
//		if (sHour.length() > 2) sHour = sHour.substring(1, 2);
////		sHour = sHour.substring(1, 2);
//		if (sMinute.length() > 2) sMinute = sMinute.substring(1, 2);
////		sMinute = sMinute.substring(1, 2);	

//		tvwhora_visita.setText(new StringBuilder()
//		.append(sHour).append(":").append(sMinute).append(" "));
		// set current time into textview
		tvwhora_visita.setText(
                    new StringBuilder().append(pad(hour))
                                       .append(":").append(pad(minute)));
 
	}
	
	public void addListenerOntvwhora_visita() {
		tvwhora_visita.setOnTouchListener(new OnTouchListener(){
			 
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(TIME_DIALOG_ID);
				return true;
			}

 
		});

 
		tvwhora_visita.setOnClickListener(new OnClickListener() {
 
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
 
				showDialog(TIME_DIALOG_ID);
 
			}
 
		});
 
	}
	public void loadLocalSpinner(){
			LocalLoadTask tareaLocal = new LocalLoadTask();
			
			loadLocal = tareaLocal.execute(url);
			Local[] objLocal;
			String[] wee;
			try {

				objLocal = loadLocal.get();
				wee = new String[objLocal.length];
				
			     for(int i = 0;i < objLocal.length; i++){
			         wee[i]= objLocal[i].nombre;
			     }
			     ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
			             this, android.R.layout.simple_spinner_item, wee);
			    spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
			    spnLocal.setAdapter(spinnerArrayAdapter);	
			     
				Log.i("Visita","Local Array");
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

	}
	public void loadProyectoSpinner(String local,String codigousuario){
		ProyectoLoadTask tareaProyecto = new ProyectoLoadTask();
		
		loadProyecto = tareaProyecto.execute(local,codigousuario,url);

        if (loadProyecto != null){
            Proyecto[] objProyecto;
            String[] wee;
            try {

                objProyecto = loadProyecto.get();
//JT:2015-06-08
                if (objProyecto != null){
                    wee = new String[objProyecto.length];

                    for(int i = 0;i < objProyecto.length; i++){
                        wee[i]= String.valueOf(objProyecto[i].id) +" - "+objProyecto[i].nombre;
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                            this, android.R.layout.simple_spinner_item, wee);
                    spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
                    spnProyecto.setAdapter(spinnerArrayAdapter);

                    Log.i("Visita","Proyecto Array");
                }
//JT:2015-06-08

            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

}

	public void loadVisitaSpinner(String codigopaciente,String local,String proyecto){
		VisitaLoadTask tareaVisita = new VisitaLoadTask();
		
		loadVisita = tareaVisita.execute(codigopaciente,local,proyecto,url);
		Visita[] objVisita;
		String[] wee,wee1,empty;
		empty = new String[0];
	    ArrayAdapter<String> emptyArrayAdapter = new ArrayAdapter<String>(
	             this, android.R.layout.simple_spinner_item, empty);
	    emptyArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		spnGrupo.setAdapter(emptyArrayAdapter);	
		spnVisita.setAdapter(emptyArrayAdapter);

		try {

			objVisita = loadVisita.get();
			if (objVisita != null){
				wee = new String[objVisita.length];
				wee1 = new String[objVisita.length];
			     for(int i = 0;i < objVisita.length; i++){
			         wee[i]= String.valueOf(objVisita[i].CodigoGrupoVisita) +" - "+objVisita[i].NombreGrupoVisita;
			         wee1[i]= String.valueOf(objVisita[i].CodigoVisita) +" - "+objVisita[i].DescripcionVisita;
			     }
			     ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
			             this, android.R.layout.simple_spinner_item, wee);
			    spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
			    spnGrupo.setAdapter(spinnerArrayAdapter);	
			     ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(
			             this, android.R.layout.simple_spinner_item, wee1);
			    spinnerArrayAdapter1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
			    spnVisita.setAdapter(spinnerArrayAdapter1);
			    
				Log.i("Visita","Visita Array");				
			}

			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		   // set date picker as current date
		   return new DatePickerDialog(this, datePickerListener, 
                         year, month,day);
		case TIME_DIALOG_ID:
			   // set date picker as current date
			   return new TimePickerDialog(this, timePickerListener, 
	                         hour, minute,true);
		}
		return null;
	}
 
	private DatePickerDialog.OnDateSetListener datePickerListener 
                = new DatePickerDialog.OnDateSetListener() {
 
		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
 
			// set selected date into textview
//			tvDisplayDate.setText(new StringBuilder().append(day)
//			   .append("/").append(month + 1).append("/").append(year)
//			   .append(" "));
 
			tvwfecha_visita.setText(new StringBuilder().append(day)
					   .append("/").append(month + 1).append("/").append(year)
					   .append(" "));
 
		}
	};	

	private TimePickerDialog.OnTimeSetListener timePickerListener 
	    = new TimePickerDialog.OnTimeSetListener() {
	
		// when dialog box is closed, below method will be called.
		@Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//			String sHour = "0"+String.valueOf(selectedHour);
//			String sMinute = "0"+String.valueOf(selectedMinute);
//			if (sHour.length() > 2) sHour = sHour.substring(1, 2);
////			sHour = sHour.substring(1, 2);
//			if (sMinute.length() > 2) sMinute = sMinute.substring(1, 2);
////			sMinute = sMinute.substring(1, 2);	
//			
//			tvwhora_visita.setText( sHour + ":" + sMinute);
			
			hour = selectedHour;
			minute = selectedMinute;
 
			// set current time into textview
			tvwhora_visita.setText(new StringBuilder().append(pad(hour))
					.append(":").append(pad(minute)));
 
			// set current time into timepicker
//			timePicker1.setCurrentHour(hour);
//			timePicker1.setCurrentMinute(minute);
			
        }
		
	};
	
	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}

}
