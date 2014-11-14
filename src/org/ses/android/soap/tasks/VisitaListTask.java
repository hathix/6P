package org.ses.android.soap.tasks;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Visitas;

import android.os.AsyncTask;

public class VisitaListTask extends AsyncTask<String,String,Visitas[]> {
	
	private Visitas[] lstVisitas;
	 
    @Override
	protected Visitas[] doInBackground(String... params) {
    	
    	Visitas[] resul= null;

		String urlserver = params[2];
    	final String NAMESPACE = urlserver+"/";
		final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
//		final String METHOD_NAME = "ListadoVisitas";
		final String METHOD_NAME = "ListadoVisitas2";
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
			
			lstVisitas = new Visitas[resSoap.getPropertyCount()];
			
			for (int i = 0; i < lstVisitas.length; i++) 
			{
		           SoapObject ic = (SoapObject)resSoap.getProperty(i);
		            
		           Visitas vis = new Visitas();
		           vis.Proyecto = ic.getProperty(0).toString();
		           vis.Visita = ic.getProperty(1).toString();
		           vis.FechaVisita= ic.getProperty(2).toString(); 
		           vis.HoraCita = ic.getProperty(3).toString();
		           vis.EstadoVisita= ic.getProperty(4).toString();
		           
		           vis.CodigoProyecto= ic.getProperty(5).toString();
		           vis.CodigoGrupoVisita= ic.getProperty(6).toString();
		           vis.CodigoVisita= ic.getProperty(7).toString();
		           vis.CodigoVisitas= ic.getProperty(8).toString();
		           lstVisitas[i] = vis;

		    }
			if (resSoap.getPropertyCount()>0){
				resul = lstVisitas;
			}

		} 
		catch (Exception e) 
		{
			resul = null;
		} 
 
        return resul;
    }
    
}
