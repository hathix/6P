package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class EstadoENR_TAMTask extends AsyncTask<String,String,String> {
	
	 
    @Override
	protected String doInBackground(String... params) {
    	String resul = "OK";
    	int count = params.length;  
    	if(count==3){

			String urlserver = params[2];
	    	final String NAMESPACE = urlserver+"/";
			final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
			final String METHOD_NAME = "EstadoENR_TAM";
			final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("Tipo", params[0]);
			request.addProperty("CodigoProyecto",params[1]); 

			SoapSerializationEnvelope envelope = 
					new SoapSerializationEnvelope(SoapEnvelope.VER11);
			
			envelope.dotNet = true; 

			envelope.setOutputSoapObject(request);

			HttpTransportSE transporte = new HttpTransportSE(URL);

			try 
			{
				transporte.call(SOAP_ACTION, envelope);

				SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
				String res = resultado_xml.toString();
				
				if(!res.equals("0") || !res.equals("1"))
					resul = "";
			} 
			catch (Exception e) 
			{
				resul = "";
			}  
	    }
	    return resul;		
    }
}

