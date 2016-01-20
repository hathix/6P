package org.ses.android.soap.utils;

import org.ses.android.soap.database.Visitas;

/**
 * Created by neel on 1/20/16.
 */
public class VisitUtilities {

    private static final String VISIT_PENDING_STATUS = "Pendiente";

    /**
     * Given an array of a patient's visits (which can be loaded from VisitasListTask),
     * finds their one pending visit, if any. (A patient can have at most 1 pending visit.)
     */
    public static Visitas getPendingVisit(Visitas[] patientVisits) {
        for (Visitas visit : patientVisits) {
            String status = visit.EstadoVisita;
            if (status.equals(VISIT_PENDING_STATUS)) {
                return visit;
            }
        }

        return null;
    }
}
