package org.ses.android.soap.tasks;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Participant;

import android.os.AsyncTask;

public class ParticipantLoadTask extends AsyncTask<String,String,Participant> {
	
//	private Participant[] lstParticipant;
	 
    @Override
	protected Participant doInBackground(String... params) {
    	
    	Participant resul= null;

		String urlserver = params[1];
    	final String NAMESPACE = urlserver+"/";
		final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
		final String METHOD_NAME = "BuscarParticipante";
		final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("DocIdentidad", params[0]);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);

		HttpTransportSE transporte = new HttpTransportSE(URL);
		transporte.debug = true;
		try 
		{
			transporte.call(SOAP_ACTION, envelope);

			SoapObject resSoap =(SoapObject)envelope.getResponse();
			
//			lstParticipant = new Participant[resSoap.getPropertyCount()];
//			
//			for (int i = 0; i < lstParticipant.length; i++) 
//			{
		           SoapObject ic = (SoapObject)resSoap.getProperty(0);
		            
		           Participant par = new Participant();

		           par.CodigoPaciente = ic.getProperty(0).toString();
		           par.Nombres = ic.getProperty(1).toString();
		           par.ApellidoPaterno = ic.getProperty(2).toString();
		           par.ApellidoMaterno = ic.getProperty(3).toString();
		           par.CodigoTipoDocumento = Integer.parseInt(ic.getProperty(4).toString());
		           par.DocumentoIdentidad = ic.getProperty(5).toString(); 
		           par.FechaNacimiento = ic.getProperty(6).toString();
		           par.Sexo = Integer.parseInt(ic.getProperty(7).toString());
		           
//		           lstParticipant[i] = par;
//				   long id = mDbHelper.createParticipant(
//						   par.Nombres, 
//						   par.ApellidoPaterno,
//						   par.ApellidoMaterno,
//						   par.CodigoTipoDocumento,
//						   par.DocumentoIdentidad,
//						   par.FechaNacimiento,
//						   par.Sexo);
//		            if (id > 0) {
//		            	Log.i("dbParticipant.creado", "si");
//		            }
//		    }
			if (resSoap.getPropertyCount()>0){
				resul = par;
			}

		} 
		catch (Exception e) 
		{
			resul = null;
		} 
 
        return resul;
    }
	}
