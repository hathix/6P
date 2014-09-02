package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by User on 26/08/2014.
 */

public class GenerarIdENRTask extends AsyncTask<String,String,String> {


    @Override
    protected String doInBackground(String... params) {
        String resul = "OK";
        int count = params.length;
        if(count==7){


//	    	final String NAMESPACE = "http://demo.sociosensalud.org.pe/";
//			final String URL="http://demo.sociosensalud.org.pe/WSSEIS/WSParticipante.asmx";
//			final String METHOD_NAME = "NuevoParticipanteSimple";
//			final String SOAP_ACTION = "http://demo.sociosensalud.org.pe/NuevoParticipanteSimple";
            String urlserver = params[6];
            final String NAMESPACE = urlserver+"/";
            final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
            final String METHOD_NAME = "AsignarID_ENR";

            final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//int TipoTAM/TipoENR ,int CodigoLocal, int CodigoProyecto, string CodigoPaciente, String IdTAM/IdENR,int CodigoUsuario
            request.addProperty("TipoENR", params[0]);
            request.addProperty("CodigoLocal",params[1]);
            request.addProperty("CodigoProyecto",params[2]);
            request.addProperty("CodigoPaciente",params[3]);
            request.addProperty("IdENR",params[4]);
            request.addProperty("CodigoUsuario",params[5]);

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
                resul = res;
//                if(!res.equals("1"))
//                    resul = "";
            }
            catch (Exception e)
            {
                resul = "";
            }
        }
        return resul;
    }
}
