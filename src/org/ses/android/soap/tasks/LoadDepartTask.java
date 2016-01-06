package org.ses.android.soap.tasks;

import android.os.AsyncTask;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.database.Departamento;
import org.ses.android.soap.database.Local;
import org.ses.android.soap.models.Depart;

import java.util.ArrayList;

/**
 * Created by dvillanueva on 06/11/2015.
 */
public class LoadDepartTask extends AsyncTask<String,String,ArrayList<Depart>> {

         private ArrayList<Depart> lstDep;
    @Override
    protected ArrayList<Depart> doInBackground(String... params) {

        ArrayList<Depart> result = null;
        String urlserver = params[0];
        final String NAMESPACE = StringConexion.conexion;
        final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
        final String METHOD_NAME = "ListadoDepartamentos";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;

        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();

            lstDep = new ArrayList<Depart>();
            int num_locales = resSoap.getPropertyCount();

            for (int i = 0; i < num_locales; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Depart dpt = new Depart();

                dpt.cod =  ic.getProperty(0).toString();
                dpt.descrip = ic.getProperty(1).toString();
                lstDep.add(dpt);
            }
            if (resSoap.getPropertyCount()>0){
                result = lstDep;
            }

        }
        catch (Exception e)
        {
            result = null;
        }


        return result;
    }
}
