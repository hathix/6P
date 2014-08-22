package org.ses.android.soap.tasks;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Visita;

import android.os.AsyncTask;

public class VisitaLoadTask extends AsyncTask<String,String,Visita[]> {

	
	private Visita[] lstVisita;
	 
    @Override
	protected Visita[] doInBackground(String... params) {
    	
    	Visita[] resul= null;

		String urlserver = params[3];
    	final String NAMESPACE = urlserver+"/";
		final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
		final String METHOD_NAME = "ListadoGrupoVisitas";
		final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("CodigoPaciente", params[0]);
		request.addProperty("CodigoLocal", params[1]);
		request.addProperty("CodigoProyecto", params[2]);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);

		HttpTransportSE transporte = new HttpTransportSE(URL);
		transporte.debug = true;
		try 
		{
			transporte.call(SOAP_ACTION, envelope);

			SoapObject resSoap =(SoapObject)envelope.getResponse();
			
			lstVisita = new Visita[resSoap.getPropertyCount()];

			for (int i = 0; i < lstVisita.length; i++) 
			{
		           SoapObject ic = (SoapObject)resSoap.getProperty(i);
		            
		           Visita vis = new Visita();

		           vis.CodigoProyecto = Integer.parseInt(ic.getProperty(0).toString());
		           vis.CodigoGrupoVisita = Integer.parseInt(ic.getProperty(1).toString());
		           vis.NombreGrupoVisita = ic.getProperty(2).toString();
		           vis.CodigoVisita = Integer.parseInt(ic.getProperty(3).toString());
		           vis.DescripcionVisita = ic.getProperty(4).toString();
		           vis.GenerarAuto = Boolean.valueOf(ic.getProperty(5).toString());
		           vis.Dependiente = Integer.parseInt(ic.getProperty(6).toString());
		           lstVisita[i] = vis;
		    }
			if (resSoap.getPropertyCount()>0){
				resul = lstVisita;
			}

		} 
		catch (Exception e) 
		{
			resul = null;
		} 
 
        return resul;
    }

}
