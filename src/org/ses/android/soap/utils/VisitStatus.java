package org.ses.android.soap.utils;

/**
 * Stores the various `CodigoEstadosVisita`s, or status codes for visits indicating whether
 * the patient attended it, missed it, etc.
 * Created by neel on 1/20/16.
 */
public enum VisitStatus {
    PENDING (1),
    // not used much
    POSTPONED (2),
    ATTENDED (3),
    CANCELED (4),
    MISSED (5),
    // not used
    RETIRED (6);

    private final int value;

    VisitStatus(int value){
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
