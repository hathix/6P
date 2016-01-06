package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.models.Distrit;
import org.ses.android.soap.models.Prov;
import org.ses.android.soap.models.Saveable;

import java.util.ArrayList;

/**
 * Created by dvillanueva on 18/11/2015.
 */
public class LoadDsitritoTask extends AsyncTask<String,String ,ArrayList<Distrit>> {

    private ArrayList<Distrit> lisDitri;
    @Override
    protected ArrayList<Distrit> doInBackground(String... params) {


        ArrayList<Distrit> result = null;
        String urlserver = params[1];
        final String NAMESPACE = StringConexion.conexion;
        final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
        final String METHOD_NAME = "ListadoDistritos";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("codDistrito", params[0]);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;

        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();

            lisDitri = new ArrayList<Distrit>();
            int num_locales = resSoap.getPropertyCount();

            for (int i = 0; i < num_locales; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Distrit dpt = new Distrit();

                dpt.cod =  ic.getProperty(0).toString();
                dpt.descrip = ic.getProperty(1).toString();
                lisDitri.add(dpt);
            }
            if (resSoap.getPropertyCount()>0){
                result = lisDitri;
            }

        }
        catch (Exception e)
        {
            result = null;
        }
        return result;
    }
}
