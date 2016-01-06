package org.ses.android.soap.tasks;

import android.app.Application;
import android.content.res.Resources;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by dvillanueva on 10/11/2015.
 */
public class StringConexion extends Application {

    public  static String  conexion = ReaderFile();


        //"http://demo.sociosensalud.org.pe/"


    public  static void SetConexion (String con){

        try {
            Clear();
            File root = new File(Environment.getExternalStorageDirectory() + File.separator + "text.txt");
           // File gpxfile = new File(root, "tex.txt");
            FileWriter writer = new FileWriter(root);
            writer.append(con);
            writer.flush();
            writer.close();
            }

        catch (IOException e){}

        }









    public  static String ReaderFile(){

        String aBuffer = "";
        try {


            File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + "text.txt");
            ;
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";
        //    String aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
            }

            myReader.close();


        }
        catch (IOException e){}

      return  aBuffer.toString().trim();

    }


    public static void Clear(){

        try {



            File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + "text.txt");
            if (!myFile.exists()) {
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write("");

                myOutWriter.close();
                fOut.close();
            }

        }

        catch (IOException e){

            e.printStackTrace();
        }


    }

    public  static void ifExistCreateFile (){

        boolean value ;
        try
        {

             String  redval = ReaderFile();
             if (redval.toString()== null || redval ==""){
          //  File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test.txt");

            String data="http://demo2.sociosensalud.org.pe/";
//write the bytes in file

                File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + "text.txt");
             //    if (!myFile.exists()) {
                     myFile.createNewFile();
                     FileOutputStream fOut = new FileOutputStream(myFile);
                     OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                     myOutWriter.append(data);
                     myOutWriter.close();
                     fOut.close();
                // }
        }
             else {


             }
            // Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {


        }


    }

    public boolean existFile (){

        boolean  val = false;

        StringBuilder text = new StringBuilder();
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard,"con.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
               // text.append('\n');
            }

            if (text.toString()!= "" || text.toString()!=null){

                val = true;

            }
            else {
                val=false;
            }

            br.close() ;
        }catch (IOException e) {
            e.printStackTrace();
            val =false;
        }

        return  val;
    }
}
