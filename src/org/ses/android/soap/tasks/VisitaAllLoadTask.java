package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Visita;

public class VisitaAllLoadTask extends AsyncTask<String,String,Visita[]> {

	
	private Visita[] lstVisita;

	// Params: codigoProyecto and url
    @Override
	protected Visita[] doInBackground(String... params) {
    	
    	Visita[] resul= null;

		String urlserver = params[1];
    	final String NAMESPACE = StringConexion.conexion;
		final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
		final String METHOD_NAME = "ListadoGrupoVisita1";
		final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("CodigoProyecto", Integer.parseInt(params[0]));
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
					vis.DiasVisitaProx = Integer.parseInt(ic.getProperty(7).toString());
					vis.DiasAntes = Integer.parseInt(ic.getProperty(8).toString());
					vis.DiasDespues = Integer.parseInt(ic.getProperty(9).toString());
					vis.OrdenVisita = Integer.parseInt(ic.getProperty(10).toString());
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
