package org.ses.android.soap.database;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Participant implements KvmSerializable {

	public String CodigoPaciente;
	public String Nombres;
	public String ApellidoPaterno;
	public String ApellidoMaterno;
	public int CodigoTipoDocumento;
	public String DocumentoIdentidad;
	public String FechaNacimiento ;
	public int Sexo;
	
	public Participant()
	{
		CodigoPaciente = "";
		Nombres = "";
		ApellidoPaterno = "";
		ApellidoMaterno = "";
		CodigoTipoDocumento = 0;
		DocumentoIdentidad = "";
		FechaNacimiento = "";
		Sexo = 0;
	}
	
	public Participant(String CodigoPaciente,
			String Nombres,
			String ApellidoPaterno,
			String ApellidoMaterno ,
			int CodigoTipoDocumento,
			String DocumentoIdentidad,
			String FechaNacimiento,
			int Sexo
			)
	{
		this.CodigoPaciente = CodigoPaciente;
		this.Nombres  = Nombres ;
		this.ApellidoPaterno = ApellidoPaterno;
		this.ApellidoMaterno = ApellidoMaterno ;
		this.CodigoTipoDocumento  = CodigoTipoDocumento ;
		this.DocumentoIdentidad = DocumentoIdentidad;
		this.FechaNacimiento = FechaNacimiento;
		this.Sexo = Sexo;

	}
	
	@Override
	public Object getProperty(int arg0) {

		switch(arg0)
        {
        case 0:
            return CodigoPaciente ;
        case 1:
            return Nombres ;
        case 2:
            return ApellidoPaterno ;
        case 3:
            return ApellidoMaterno ;
        case 4:
            return CodigoTipoDocumento ;
        case 5:
            return DocumentoIdentidad ;
        case 6:
            return FechaNacimiento ;
        case 7:
            return Sexo ;
        }
		
		return null;
	}
	
	@Override
	public int getPropertyCount() {
		return 8;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void  getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
		switch(ind)
        {
        case 0:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoPaciente";
            break;
        case 1:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "Nombres";
            break;
        case 2:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "ApellidoPaterno";
            break;
        case 3:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "ApellidoMaterno";
            break;
        case 4:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "CodigoTipoDocumento";
            break;
        case 5:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "DocumentoIdentidad";
            break;
        case 6:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "FechaNacimiento";
            break;	            
        case 7:
            info.type = PropertyInfo.INTEGER_CLASS;
            info.name = "Sexo";
            break;		            
	            
	        default:break;
        }
	}
	
	@Override
	public void  setProperty(int ind, Object val) {
		switch(ind)
        {
        case 0:
        	CodigoPaciente  = val.toString();
            break;
        case 1:
            Nombres  = val.toString();
            break;
        case 2:
        	ApellidoPaterno  = val.toString();
            break;
        case 3:
        	ApellidoMaterno  = val.toString();
            break;
        case 4:
            CodigoTipoDocumento  = Integer.parseInt(val.toString());
            break;
        case 5:
        	DocumentoIdentidad  = val.toString();
            break;
        case 6:
        	FechaNacimiento  = val.toString();
            break;
        case 7:
        	Sexo  = Integer.parseInt(val.toString());
            break;
	    default:
	        break;
        }
	}
}
