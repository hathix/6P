package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class TienePermisosTask extends AsyncTask<String,String,String> {
	
	 
    @Override
	protected String doInBackground(String... params) {
    	String resul = "OK";
    	int count = params.length;
    	if(count==4){

			String urlserver = params[3];
	    	final String NAMESPACE = urlserver+"/";
			final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
			final String METHOD_NAME = "TienePermisos";
			final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("CodigoLocal", params[0]);
			request.addProperty("CodigoUsuario",params[1]);
            request.addProperty("CodigoProyecto",params[2]);

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
                //resul = res;
                Log.i("TienePermisosTask", "res: " + res);
                int intRes = Integer.parseInt(res);
                if(intRes < 1)
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

