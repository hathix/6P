package org.ses.android.soap.tasks;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;

public class RegistrarParticipanteTask extends AsyncTask<String,String,String> {
	
	 
    @Override
	protected String doInBackground(String... params) {
    	String resul = "OK";
    	int count = params.length;  
    	if(count==8){

	   	 
//	    	final String NAMESPACE = "http://demo.sociosensalud.org.pe/";
//			final String URL="http://demo.sociosensalud.org.pe/WSSEIS/WSParticipante.asmx";
//			final String METHOD_NAME = "NuevoParticipanteSimple";
//			final String SOAP_ACTION = "http://demo.sociosensalud.org.pe/NuevoParticipanteSimple";
			
			String urlserver = params[7];
	    	final String NAMESPACE = urlserver+"/";
			final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
			final String METHOD_NAME = "NuevoParticipanteSimple";
			final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			request.addProperty("DocumentoIdentidad", params[0]); 
			request.addProperty("CodigoTipoDocumento",params[1]); 
			request.addProperty("Nombres",params[2]); 
			request.addProperty("ApellidoP",params[3]); 
			request.addProperty("ApellidoM",params[4]); 
			request.addProperty("FechaNacimiento",params[5]); 
			request.addProperty("Sexo",params[6]); 
			
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
				
				if(!res.equals("Los datos se grabaron correctamente"))
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

