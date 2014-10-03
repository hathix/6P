package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.PatId;
import org.ses.android.soap.database.Visitas;

public class IdsListTask extends AsyncTask<String,String,PatId[]> {
	
	private PatId[] lstIds;
	 
    @Override
	protected PatId[] doInBackground(String... params) {

        PatId[] resul= null;

		String urlserver = params[2];
    	final String NAMESPACE = urlserver+"/";
		final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
		final String METHOD_NAME = "ListadoIds";
		final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		request.addProperty("CodigoPaciente", params[0]);
        request.addProperty("CodigoUsuario", params[1]);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);

		HttpTransportSE transporte = new HttpTransportSE(URL);
		transporte.debug = true;
		try 
		{
			transporte.call(SOAP_ACTION, envelope);

			SoapObject resSoap =(SoapObject)envelope.getResponse();

            lstIds = new PatId[resSoap.getPropertyCount()];

			for (int i = 0; i < lstIds.length; i++)
			{
		        SoapObject ic = (SoapObject)resSoap.getProperty(i);

                PatId reg = new PatId();
                reg.CodigoProyecto = ic.getProperty(0).toString();
                reg.Proyecto = ic.getProperty(1).toString();
                reg.IdTAM= ic.getProperty(2).toString();
                reg.IdENR= ic.getProperty(3).toString();

                lstIds[i] = reg;
                Log.i("", reg.CodigoProyecto + "-" + reg.IdTAM + "-" + reg.IdENR);
		    }
			if (resSoap.getPropertyCount()>0){
				resul = lstIds;
			}

		} 
		catch (Exception e) 
		{
			resul = null;
		} 
 
        return resul;
    }
    
}
