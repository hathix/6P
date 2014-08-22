package org.ses.android.soap.database;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Login implements KvmSerializable {
	public int CodigoUsuario;
	public String Mensaje;

	
	public Login()
	{
		CodigoUsuario = 0;
		Mensaje = "";

	}
	
	public Login(int CodigoUsuario, String Mensaje)
	{
		this.CodigoUsuario = CodigoUsuario;
		this.Mensaje = Mensaje;

	}
	
	@Override
	public Object getProperty(int arg0) {

		switch(arg0)
        {
        case 0:
            return CodigoUsuario;
        case 1:
            return Mensaje;

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
	            info.name = "CodigoUsuario";
	            break;
	        case 1:
	            info.type = PropertyInfo.STRING_CLASS;
	            info.name = "Mensaje";
	            break;
	        default:break;
        }
	}
	
	@Override
	public void setProperty(int ind, Object val) {
		switch(ind)
        {
	        case 0:
	            CodigoUsuario = Integer.parseInt(val.toString());
	            break;
	        case 1:
	            Mensaje = val.toString();
	            break;
	        default:
	            break;
        }
	}
}
