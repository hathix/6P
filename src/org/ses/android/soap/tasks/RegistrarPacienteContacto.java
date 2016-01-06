package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by dvillanueva on 05/11/2015.
 */
public class RegistrarPacienteContacto extends AsyncTask<String,String,String> {


    @Override
    protected String doInBackground(String... params) {


        String resul = "";
        int count = params.length;

//	    	final String NAMESPACE = "http://demo.sociosensalud.org.pe/";
//			final String URL="http://demo.sociosensalud.org.pe/WSSEIS/WSParticipante.asmx";
//			final String METHOD_NAME = "NuevoParticipanteSimple";
//			final String SOAP_ACTION = "http://demo.sociosensalud.org.pe/NuevoParticipanteSimple";

            String urlserver = params[8];
            final String NAMESPACE = StringConexion.conexion;
            final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
            final String METHOD_NAME = "InsertarPacienteContacto";
            final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("codigoPaciente", params[0]);
            request.addProperty("codigoUbigeo",params[1]);
            request.addProperty("direccion",params[2]);
            request.addProperty("referencia",params[3]);
            request.addProperty("telefono",params[4]);
            request.addProperty("Celular",params[5]);
            request.addProperty("Longit",params[6]);
            request.addProperty("latit",params[7]);

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
                Log.i("mensaje", res);


            }
            catch (Exception e)
            {
                resul = "";
            }

        return resul;



    }
}
