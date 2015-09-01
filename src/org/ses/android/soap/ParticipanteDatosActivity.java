package org.ses.android.soap;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import org.ses.android.seispapp120.R;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.RegistrarParticipanteTask;
import org.ses.android.soap.utils.UrlUtils;
import org.ses.android.soap.widgets.GrupoBotones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ParticipanteDatosActivity extends Activity {
	
	private EditText edt_nombres;
	private EditText edt_ape_pat;
	private EditText edt_ape_mat;
//	private DatePickerFragment dpf_fecha_nacimiento;
	private RadioGroup rbgSexo;
	
	private GrupoBotones btgNavega;
	
	private TextView tvDisplayDate;
//	private DatePicker dpResult;
//	private Button btnChangeDate;
    private TextView tvwfecha_nacimiento;
	private int year;
	private int month;
	private int day;
 
	static final int DATE_DIALOG_ID = 999;	
	String tip_doc= "2";
	String dni,nombres,ape_pat,ape_mat,fec_nacimiento,sexo,url;
	
	RegistrarParticipanteTask tarea;

	private AsyncTask<String, String, String> registrarParticipante;
	SharedPreferences mPreferences ;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participante_datos_layout);
		
		edt_nombres = (EditText)findViewById(R.id.edt_nombres);
		edt_ape_pat = (EditText)findViewById(R.id.edt_ape_pat);
		edt_ape_mat = (EditText)findViewById(R.id.edt_ape_mat);
		tvwfecha_nacimiento = (TextView)findViewById(R.id.tvwfecha_nacimiento);
		setCurrentDateOnView();
		addListenerOntvwfecha_nacimiento();
		
		rbgSexo = (RadioGroup)findViewById(R.id.rbgSexo);
        rbgSexo.clearCheck();
        rbgSexo.check(R.id.rbgSexo);
    
        btgNavega = (GrupoBotones)findViewById(R.id.btgNavega);
        
        
        rbgSexo.setOnCheckedChangeListener(
        		new RadioGroup.OnCheckedChangeListener() {
        		@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
//        			lblMensaje.setText("ID opcion seleccionada: " + checkedid);
        			Log.i("rgbSexo","ID opcion seleccionada: " + Integer.toString(checkedId));
        			Log.i("rgbSexo","getCheckedRadioButtonId: " + group.getCheckedRadioButtonId());
        			
//        			Toast.makeText(getBaseContext(), "ID opcion seleccionada!!"+ Integer.toString(checkedId),Toast.LENGTH_SHORT).show();
        		}
        });
        
        btgNavega.setOnAnteriorListener(new GrupoBotones.OnAnteriorListener() {
			
			@Override
			public void OnAnterior() {
				Intent intent=new Intent(ParticipanteDatosActivity.this,ParticipanteDNIActivity.class); 
				startActivity(intent); 				}
		});

        btgNavega.setOnSiguienteListener(new GrupoBotones.OnSiguienteListener() {
			
			@Override
			public void OnSiguiente() {


                	mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                	url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                        getString(R.string.default_server_url));				
                	Log.i("URL",url);
		        	dni = mPreferences.getString("doc_identidad", "");
		        	
		        	if (dni.equals("")) tip_doc = "1";

		        	Log.i("Datos_DNI",dni);

			        nombres = edt_nombres.getText().toString();
			        ape_pat = edt_ape_pat.getText().toString();
			        ape_mat = edt_ape_mat.getText().toString();
			        sexo =""; 

	
			        
			        int idSexo= rbgSexo.getCheckedRadioButtonId();

        			Log.i("onSiguiente_rgbSexo","ID opcion seleccionada: " + Integer.toString(idSexo));
                    Log.i("onSiguiente_rboMasculino","R.id.rboMasculino:" + String.valueOf(R.id.rboMasculino));
                    Log.i("onSiguiente_rboFemenino","R.id.rboFemenino:" + String.valueOf(R.id.rboFemenino));
                    if (idSexo== R.id.rboMasculino)	sexo="1";
					if (idSexo==R.id.rboFemenino) sexo="2";
					if (idSexo==-1) sexo="";
					Log.i("onSiguiente sexo:",sexo);
        			
        			fec_nacimiento = tvwfecha_nacimiento.getText().toString();
        			Log.i("fec_nacimiento:",fec_nacimiento);
        			
    				//ok?: "[a-zA-Z]+\\.?"  , Unicode: "^\pL+[\pL\pZ\pP]{0,}$",OK:"^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}"
        			String regExp="^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}";

		        	if (!UrlUtils.validData(nombres, regExp)){
		        		Toast.makeText(getBaseContext(), "Nombre invalido!!",Toast.LENGTH_SHORT).show();
		        	}else{
		        		if (!UrlUtils.validData(ape_pat, regExp)){
		        			Toast.makeText(getBaseContext(), "Apellido Paterno invalido!!",Toast.LENGTH_SHORT).show();
		        		}else{
		        			if (!UrlUtils.validData(ape_mat, regExp)){
			        			Toast.makeText(getBaseContext(), "Apellido Materno invalido!!",Toast.LENGTH_SHORT).show();
			        		}else{
		        				if (sexo.equals("")){
		        					Toast.makeText(getBaseContext(), "Elija el sexo!!",Toast.LENGTH_SHORT).show();
	    		        		}else{
	    		        			final Calendar c = Calendar.getInstance();
	    		        			year = c.get(Calendar.YEAR);
	    		        			month = c.get(Calendar.MONTH);
	    		        			day = c.get(Calendar.DAY_OF_MONTH);
	    		        			StringBuilder sb = new StringBuilder()
	    		        			.append(day).append("/").append(month + 1).append("/")
	    		        			.append(year).append(" ");
	    		        			String fec_hoy = sb.toString();
	    		        			long elapsedDays = UrlUtils.daysBetween(fec_nacimiento,fec_hoy);
		        					if (elapsedDays == 0  || elapsedDays > 10 ){
		        						Toast.makeText(getBaseContext(), "Fecha de Nacimiento invalida!!",Toast.LENGTH_SHORT).show();
		    		        		}else{
		    		    	        	AlertDialog.Builder builder = new AlertDialog.Builder(ParticipanteDatosActivity.this);
		    		    	        	builder.setMessage("Desea Registrar Participante?")
		    		    	        	        .setTitle("Advertencia")
		    		    	        	        .setCancelable(false)
		    		    	        	        .setPositiveButton("Si",
		    		    	        	                new DialogInterface.OnClickListener() {
		    		    	        	                    @Override
		    		    									public void onClick(DialogInterface dialog, int id) {
		    		    		    							String existe;
																try {
			    		    	        	        				tarea = new RegistrarParticipanteTask();
			    		    		    		        			registrarParticipante=tarea.execute(dni,tip_doc,nombres,ape_pat,ape_mat,fec_nacimiento,sexo,url);
																	existe = registrarParticipante.get();
			    		    		    							if (existe.equals("OK")){
			    		    		    								Toast.makeText(getBaseContext(), "Ya existe partipante con ese DNI!!",Toast.LENGTH_SHORT).show();
			    		    		    							}else{
			    		    		    								Toast.makeText(getBaseContext(), "Datos guardados!!",Toast.LENGTH_SHORT).show();
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
		    		        			//
				
		        					}
		        				}
    			        	}
			        	}
		        	}

			}
		});	
	}
	

	// display current date
	public void setCurrentDateOnView() {
 
//		tvDisplayDate = (TextView) findViewById(R.id.tvDate);
//		dpResult = (DatePicker) findViewById(R.id.dpResult);
 
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
 
		// set current date into textview
		tvwfecha_nacimiento.setText(new StringBuilder()
			// Month is 0 based, just add 1
			.append(day).append("/").append(month + 1).append("/")
			.append(year).append(" "));
 
		// set current date into datepicker
//		dpResult.init(year, month, day, null);
 
	}
 
	public void addListenerOntvwfecha_nacimiento() {
 
//		tvwfecha_nacimiento = (TextView) findViewById(R.id.tvwfecha_nacimiento);
		tvwfecha_nacimiento.setOnTouchListener(new OnTouchListener(){
			 
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showDialog(DATE_DIALOG_ID);
				return true;
			}

 
		});
		tvwfecha_nacimiento.setOnClickListener(new OnClickListener() {
 
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
 
				showDialog(DATE_DIALOG_ID);
 
			}
 
		});
 
	}
 
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		   // set date picker as current date
		   return new DatePickerDialog(this, datePickerListener, 
                         year, month,day);
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
			tvwfecha_nacimiento.setText(new StringBuilder().append(day)
			   .append("/").append(month + 1).append("/").append(year)
			   .append(" "));
 
			// set selected date into datepicker also
//			dpResult.init(year, month, day, null);
 
		}
	};	

	
}
