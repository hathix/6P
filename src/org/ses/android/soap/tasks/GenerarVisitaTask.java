package org.ses.android.soap.tasks;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;

public class GenerarVisitaTask extends AsyncTask<String,String,String> {
	
	 
    @Override
	protected String doInBackground(String... params) {
    	String resul = "OK";
    	int count = params.length;  
    	if(count==9){

	   	 
//	    	final String NAMESPACE = "http://demo.sociosensalud.org.pe/";
//			final String URL="http://demo.sociosensalud.org.pe/WSSEIS/WSParticipante.asmx";
//			final String METHOD_NAME = "NuevoParticipanteSimple";
//			final String SOAP_ACTION = "http://demo.sociosensalud.org.pe/NuevoParticipanteSimple";
			
			String urlserver = params[8];
	    	final String NAMESPACE = urlserver+"/";
			final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
			final String METHOD_NAME = "InsertarVisitas";
			final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//int CodigoLocal, int CodigoProyecto, int CodigoVisita, string CodigoPaciente, string FechaVisita, string HoraCita, int CodigoUsuario
			request.addProperty("CodigoLocal", params[0]); 
			request.addProperty("CodigoProyecto",params[1]); 
			request.addProperty("CodigoGrupoVisita",params[2]); 
			request.addProperty("CodigoVisita",params[3]); 
			request.addProperty("CodigoPaciente",params[4]); 
			request.addProperty("FechaVisita",params[5]); 
			request.addProperty("HoraCita",params[6]); 
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

