package org.ses.android.soap.database;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Local implements KvmSerializable {
	public int id;
	public String nombre;

	
	public Local()
	{
		id = 0;
		nombre = "";

	}
	
	public Local(int id, String nombre)
	{
		this.id = id;
		this.nombre = nombre;

	}
	
	@Override
	public Object getProperty(int arg0) {

		switch(arg0)
        {
        case 0:
            return id;
        case 1:
            return nombre;

        }
		
		return null;
	}
	
	@Override
	public int getPropertyCount() {
		return 2;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
		switch(ind)
        {
	        case 0:
	            info.type = PropertyInfo.INTEGER_CLASS;
	            info.name = "Id";
	            break;
	        case 1:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "Nombre";
	            break;
	        default:break;
        }
	}
	
	@Override
	public void setProperty(int ind, Object val) {
		switch(ind)
        {
	        case 0:
	            id = Integer.parseInt(val.toString());
	            break;
	        case 1:
	            nombre = val.toString();
	            break;
	        default:
	            break;
        }
	}
}
