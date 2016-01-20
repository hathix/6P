package org.ses.android.soap.database;

import java.util.Hashtable;

import org.ses.android.soap.models.Cacheable;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import android.os.Parcel;
import android.os.Parcelable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;


public class Visitas extends Cacheable implements KvmSerializable, Parcelable {

	public String Proyecto;
	public String Visita;
	public String FechaVisita;
	public String HoraCita;
	public String EstadoVisita;
	public String CodigoProyecto;
	public String CodigoGrupoVisita;
	public String CodigoVisita;
	public String CodigoVisitas;
	public String CodigoEstatusPaciente;
	public String CodigoUsuario;
    public String FechaUpdEstado;

    public Visitas(Cacheable cacheable) {
        JSONObject jsonObject = cacheable.jsonObject;
        try {
            this.Proyecto = jsonObject.getString("Proyecto");
            this.Visita = jsonObject.getString("Visita");
            this.FechaVisita = jsonObject.getString("FechaVisita");
            this.HoraCita = jsonObject.getString("HoraCita");
            this.EstadoVisita = jsonObject.getString("EstadoVisita");
            this.CodigoProyecto = jsonObject.getString("CodigoProyecto");
            this.CodigoGrupoVisita = jsonObject.getString("CodigoGrupoVisita");
            this.CodigoVisita = jsonObject.getString("CodigoVisita");
            this.CodigoVisitas = jsonObject.getString("CodigoVisitas");
            this.CodigoEstatusPaciente = jsonObject.getString("CodigoEstatusPaciente");
            this.CodigoUsuario = jsonObject.getString("CodigoUsuario");
            this.FechaUpdEstado = jsonObject.getString("FechaUpdEstado");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject temp = new JSONObject();
        try {
            temp.put("Proyecto", this.Proyecto);
            temp.put("Visita", this.Visita);
            temp.put("FechaVisita", this.FechaVisita);
            temp.put("HoraCita", this.HoraCita);
            temp.put("EstadoVisita", this.EstadoVisita);
            temp.put("CodigoProyecto", this.CodigoProyecto);
            temp.put("CodigoGrupoVisita", this.CodigoGrupoVisita);
            temp.put("CodigoVisita", this.CodigoVisita);
            temp.put("CodigoVisitas", this.CodigoVisitas);
            temp.put("CodigoEstatusPaciente", this.CodigoEstatusPaciente);
            temp.put("CodigoUsuario", this.CodigoUsuario);
            temp.put("FechaUpdEstado", this.FechaUpdEstado);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp;
    }
	
	public Visitas()
	{
		Proyecto = "";
		Visita = "";
		FechaVisita = "";
		HoraCita = "";
		EstadoVisita= "";
		CodigoProyecto= "";
		CodigoGrupoVisita= "";
		CodigoVisita= "";
		CodigoVisitas= "";
		CodigoEstatusPaciente= "";
		CodigoUsuario= "";
        FechaUpdEstado = "";
	}
	
	public Visitas(
			String Proyecto,
			String Visita,
			String FechaVisita,
			String HoraCita,
			String EstadoVisita,
			String CodigoProyecto,
			String CodigoGrupoVisita,
			String CodigoVisita,
			String CodigoVisitas,
			String CodigoEstatusPaciente,
			String CodigoUsuario,
            String FechaUpdEstado
	)
	{
		this.Proyecto = Proyecto;
		this.Visita = Visita;
		this.FechaVisita = FechaVisita;
		this.HoraCita = HoraCita;
		this.EstadoVisita  = EstadoVisita ;
		this.CodigoProyecto = CodigoProyecto;
		this.CodigoGrupoVisita = CodigoGrupoVisita;
		this.CodigoVisita = CodigoVisita;
		this.CodigoVisitas = CodigoVisitas;
		this.CodigoEstatusPaciente = CodigoEstatusPaciente;
		this.CodigoUsuario = CodigoUsuario;
        this.FechaUpdEstado = FechaUpdEstado;
	}


    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Proyecto);
        dest.writeString(Visita);
        dest.writeString(FechaVisita);
        dest.writeString(HoraCita);
        dest.writeString(EstadoVisita);
        dest.writeString(CodigoProyecto);
        dest.writeString(CodigoVisita);
        dest.writeString(CodigoVisitas);
        dest.writeString(CodigoEstatusPaciente);
        dest.writeString(CodigoUsuario);
    }

    public static final Parcelable.Creator<Visitas> CREATOR =
            new Parcelable.Creator<Visitas>() {
                public Visitas createFromParcel(Parcel in) {
                    return new Visitas(in);
                }

                public Visitas[] newArray(int size) {
                    return new Visitas[size];
                }
            };

    private Visitas(Parcel in) {
        Proyecto = in.readString();
        Visita = in.readString();
        FechaVisita = in.readString();
        HoraCita = in.readString();
        EstadoVisita = in.readString();
        CodigoProyecto = in.readString();
        CodigoVisita = in.readString();
        CodigoVisitas = in.readString();
        CodigoEstatusPaciente = in.readString();
        CodigoUsuario = in.readString();
    }
	
	@Override
	public Object getProperty(int arg0) {

		switch(arg0)
        {
        case 0:
            return Proyecto;
        case 1:
            return Visita;
        case 2:
            return FechaVisita;
        case 3:
            return HoraCita;
        case 4:
            return EstadoVisita;
        case 5:
            return CodigoProyecto;
        case 6:
            return CodigoGrupoVisita;
        case 7:
            return CodigoVisita;
        case 8:
            return CodigoVisitas;
        case 9:
            return CodigoEstatusPaciente;
        case 10:
            return CodigoUsuario;
        case 11:
            return FechaUpdEstado;
        }
		
		return null;
	}
	
	@Override
	public int getPropertyCount() {
		return 12;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void  getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
		switch(ind)
        {
        case 0:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "Proyecto";
            break;
        case 1:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "Visita";
            break;
        case 2:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "FechaVisita";
            break;
        case 3:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "HoraCita";
            break;
        case 4:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "EstadoVisita";
            break;
        case 5:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoProyecto";
            break;
        case 6:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoGrupoVisita";
            break;
        case 7:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoVisita";
            break;            
        case 8:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoVisitas";
            break;
        case 9:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoEstatusPaciente";
            break;
        case 10:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CodigoUsuario";            
            break;
        case 11:
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "FechaUpdEstado";
            break;

        default:break;
        }
	}
	
	@Override
	public void  setProperty(int ind, Object val) {
		switch(ind)
        {
        case 0:
        	Proyecto  = val.toString();
            break;
        case 1:
        	Visita  = val.toString();
            break;
        case 2:
        	FechaVisita = val.toString();
            break;
        case 3:
        	HoraCita  = val.toString();
            break;
        case 4:
        	EstadoVisita  = val.toString();
            break;
        case 5:
        	CodigoProyecto  = val.toString();
            break;
        case 6:
        	CodigoGrupoVisita  = val.toString();
            break;
        case 7:
        	CodigoVisita  = val.toString();
            break;            
        case 8:
        	CodigoVisitas  = val.toString();
            break;
        case 9:
        	CodigoEstatusPaciente  = val.toString();
            break;
        case 10:
        	CodigoUsuario  = val.toString();
            break;
        case 11:
            FechaUpdEstado  = val.toString();
            break;
        
        default:break;
        }
	}
}
