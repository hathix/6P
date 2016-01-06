package org.ses.android.soap;

import java.util.concurrent.ExecutionException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.seispapp120.R;
import org.ses.android.soap.preferences.PreferencesActivity;
import org.ses.android.soap.tasks.StringConexion;
import org.ses.android.soap.utils.UrlUtils;
import org.ses.android.soap.widgets.GrupoBotones;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ParticipanteDNIActivity extends Activity {
	
	private EditText edt_doc_identidad;
//	private Button btnAnterior;
//	private Button btnSiguiente;
	private RadioGroup rbgTipoDoc;
	private GrupoBotones btgNavega;
	private AsyncTask<String, String, String> asyncTask;
	private SharedPreferences mPreferences ;
	private RadioButton  rboDN ;
	private RadioButton  rboOtro ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.participante_dni_layout);
		
		edt_doc_identidad = (EditText)findViewById(R.id.edt_doc_identidad);
		rboDN = (RadioButton)findViewById(R.id.rboDNI);
		rboOtro  = (RadioButton)findViewById(R.id.rboOtros);
		
		rbgTipoDoc = (RadioGroup)findViewById(R.id.rbgTipoDoc);
        rbgTipoDoc.clearCheck();
//        rbgTipoDoc.check(23145678);

//
        btgNavega = (GrupoBotones)findViewById(R.id.btgNavega);

        rbgTipoDoc.setOnCheckedChangeListener(
        		new RadioGroup.OnCheckedChangeListener() {
        		@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
//        			lblMensaje.setText("ID opcion seleccionada: " + checkedid);
        			Log.i("dni","rbgTipoDoc_ ID opcion seleccionada: " + Integer.toString(checkedId));
        		}
        });
        
        
        btgNavega.setOnAnteriorListener(new GrupoBotones.OnAnteriorListener() {
			
			@Override
			public void OnAnterior() {
				Intent intent=new Intent(ParticipanteDNIActivity.this,Menu_principal.class); 
				startActivity(intent); 			}
		});

        btgNavega.setOnSiguienteListener(new GrupoBotones.OnSiguienteListener() {
			
			@Override
			public void OnSiguiente() {
				ExisteParticipante tarea = new ExisteParticipante();

		        try {

					if (rboDN.isChecked()==false && rboOtro.isChecked()==false){

						Toast.makeText(getApplicationContext(),"Seleccionar una opcion de documento",Toast.LENGTH_LONG).show();
					}

					 else {


	                mPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			        int tipdocSeleccionado = rbgTipoDoc.getCheckedRadioButtonId();			        
        			Log.i("dni","OnSiguiente_ID Tipo Doc. opcion seleccionada: " + Integer.toString(tipdocSeleccionado));
			        String doc_identidad = edt_doc_identidad.getText().toString();

                    Log.i("dni","onSiguiente_ID Tipo Doc.:R.id.rboDNI:" + String.valueOf(R.id.rboDNI));
//                  if (tipdocSeleccionado==2131427368){
                    if (tipdocSeleccionado==R.id.rboDNI){
    			        if (!UrlUtils.validData(doc_identidad, "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]")){
    						Toast.makeText(getBaseContext(), "Nro. de DNI invalido!!",Toast.LENGTH_SHORT).show();
    					}
    			        else
    					{

    		                String url = mPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
    		                        getString(R.string.default_server_url));
    			        	asyncTask=tarea.execute(doc_identidad,url);
    				        String existe = asyncTask.get();
    						Log.i("doc_identidad",doc_identidad );
    						if (existe.equals("si")){
    							Toast.makeText(getBaseContext(), "Ya existe partipante con ese DNI!!",Toast.LENGTH_SHORT).show();
    						}else{
    							//Guardamos el mensaje personalizado en las preferencias
    	                    	
    	                    	Editor editor = mPreferences.edit();
    							editor.putString("doc_identidad",doc_identidad);
    							
    							editor.commit();
    									
    							Intent intent=new Intent(ParticipanteDNIActivity.this,ParticipanteDatosActivity.class); 
    							startActivity(intent); 
    						}
    						
    					}

        			}else{
                    	Editor editor = mPreferences.edit();
						editor.putString("doc_identidad",doc_identidad);
					
						editor.commit();
						Intent intent=new Intent(ParticipanteDNIActivity.this,ParticipanteDatosActivity.class); 
						startActivity(intent); 
        			}

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
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//Tarea Asincrona para llamar al WS de consulta en segundo plano
	private class ExisteParticipante extends AsyncTask<String,String,String>  {
		
	 
	    @Override
		protected String doInBackground(String... params) {
	    	
	    	String existe = "no";
	    	String resp;
			String urlserver = params[1];
	    	final String NAMESPACE = StringConexion.conexion;
			final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
			final String METHOD_NAME = "ExisteParticipante";
			final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
// para usar en la busqueda del DNI del Participante
					 
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("DocIdentidad", params[0]);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet = true;

			envelope.setOutputSoapObject(request);

			HttpTransportSE transporte = new HttpTransportSE(URL);
			transporte.debug = true;
			try 
			{
				transporte.call(SOAP_ACTION, envelope);
				SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
				resp = resultado_xml.toString();
				Log.i("DNI", "doInBackground_resp:" + resp);
				
				if (resp.equals("1")){
					existe = "si";
				}
				
			} 
			catch (Exception e) 
			{
				existe = "no";
			} 
	 
	        return existe;
	    }
	    
	}
	


}
