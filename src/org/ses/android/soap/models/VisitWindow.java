package org.ses.android.soap.models;

import android.util.Log;

import org.ses.android.soap.database.Visita;
import org.ses.android.soap.database.Visitas;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Encapsulates a Visitas and the window around it during which the patient can check in.
 * Created by neel on 1/21/16.
 */
public class VisitWindow {
    // start of window
    private Date start;
    // center of window -- originally scheduled time
    private Date center;
    // end of window
    private Date end;

    private Visitas visitas;
    private Visita visita;

    public VisitWindow(Visitas visitas, Visita visita) {
        this.visitas = visitas;
        this.visita = visita;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // center of window
            this.center = dateFormat.parse(visitas.FechaVisita);

            Calendar c = Calendar.getInstance();

            // left (start) of window
            c.setTime(this.getCenter());
            c.add(Calendar.DATE, -1 * visita.DiasAntes);
            this.start = c.getTime();

            // right (end) of window
            c.setTime(this.getCenter());
            c.add(Calendar.DATE, visita.DiasDespues);
            this.end = c.getTime();
        } catch (Exception e) {
            Log.e("VisitWindow", "Parsing dates failed");
            e.printStackTrace();
        }
    }

    public Date getStart() {
        return start;
    }

    public Date getCenter() {
        return center;
    }

    public Date getEnd() {
        return end;
    }

    /**
     * Returns true if the current date is past the end of the window, false otherwise
     * (i.e. if it is in the window or before it.)
     */
    public boolean isPastEnd() {
        if (this.end == null) {
            Log.w("VisitWindow", "End date is null! Returning false to avoid crash.");
            return false;
        }

        Date currentDate = new Date();
        return currentDate.after(this.end);
    }
}
