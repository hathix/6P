package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ses.android.soap.models.Project;

import java.util.ArrayList;

/**
 * Created by User on 25/08/2015.
 */


public class ProjectLoadTask extends AsyncTask<String,String,ArrayList<Project>> {


    private ArrayList<Project>  lstProjects;

    @Override
    protected ArrayList<Project> doInBackground(String... params) {

        ArrayList<Project> resul= null;

        String urlserver = params[1];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
        final String METHOD_NAME = "ListadoProyectos";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("local", params[0]);
        //request.addProperty("CodigoUsuario", params[1]);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();

            //lstProyecto = new Proyecto[resSoap.getPropertyCount()];

            // instantiate the locales list
            lstProjects = new ArrayList<Project>();
            int num_projects = resSoap.getPropertyCount();

            for (int i = 0; i < num_projects; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Project pro = new Project();

                pro.id = Integer.parseInt(ic.getProperty(0).toString());
                pro.name = ic.getProperty(1).toString();
                lstProjects.add(pro);
            }
            if (resSoap.getPropertyCount()>0){
                resul = lstProjects;
            }

        }
        catch (Exception e)
        {
            resul = null;
        }

        return resul;
    }

}
