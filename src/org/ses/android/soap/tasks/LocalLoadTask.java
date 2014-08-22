package org.ses.android.soap.tasks;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Local;

import android.os.AsyncTask;

public class LocalLoadTask extends AsyncTask<String,String,Local[]> {

	
	private Local[] lstLocal;
	 
    @Override
	protected Local[] doInBackground(String... params) {
    	
    	Local[] resul= null;

		String urlserver = params[0];
    	final String NAMESPACE = urlserver+"/";
		final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
		final String METHOD_NAME = "ListadoLocales";
		final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

//		request.addProperty("DocIdentidad", params[0]);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);

		HttpTransportSE transporte = new HttpTransportSE(URL);
		transporte.debug = true;
		try 
		{
			transporte.call(SOAP_ACTION, envelope);

			SoapObject resSoap =(SoapObject)envelope.getResponse();
			
			lstLocal = new Local[resSoap.getPropertyCount()];

			for (int i = 0; i < lstLocal.length; i++) 
			{
		           SoapObject ic = (SoapObject)resSoap.getProperty(i);
		            
		           Local loc = new Local();

		           loc.id = Integer.parseInt(ic.getProperty(0).toString());
		           loc.nombre = ic.getProperty(1).toString();
		           lstLocal[i] = loc;
		    }
			if (resSoap.getPropertyCount()>0){
				resul = lstLocal;
			}

		} 
		catch (Exception e) 
		{
			resul = null;
		} 
 
        return resul;
    }

}
