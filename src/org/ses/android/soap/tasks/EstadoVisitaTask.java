package org.ses.android.soap.tasks;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;

public class EstadoVisitaTask extends AsyncTask<String,String,String> {
	
	 
    @Override
	protected String doInBackground(String... params) {
    	String resul = "OK";
    	int count = params.length;  
    	if(count==9){

	   	 
			
			String urlserver = params[8];
	    	final String NAMESPACE = urlserver+"/";
			final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
			final String METHOD_NAME = "EstadoVisita";
			final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("CodigoLocal", params[0]); 
			request.addProperty("CodigoProyecto",params[1]); 
			request.addProperty("CodigoVisita",params[2]);
			request.addProperty("CodigoVisitas",params[3]); 
			request.addProperty("CodigoPaciente",params[4]); 
			request.addProperty("CodigoEstadoVisita",params[5]); 
			request.addProperty("CodigoEstatusPaciente",params[6]); 
			request.addProperty("CodigoUsuario",params[7]); 

			
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
				
				if(!res.equals("1"))
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

