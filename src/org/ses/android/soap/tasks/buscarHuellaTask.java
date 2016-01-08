package org.ses.android.soap.tasks;

import android.os.AsyncTask;

import org.ses.android.soap.database.Participant;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGANSITemplateInfo;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;
import SecuGen.FDxSDKPro.SGISOTemplateInfo;
import SecuGen.FDxSDKPro.SGImpressionType;

/**
 * Created by franciscorivera on 1/8/16.
 */
public class buscarHuellaTask extends AsyncTask<Byte, Void, Participant> {

    private JSGFPLib sgfplib;

    @Override
    protected Participant doInBackground(Byte ... params)
    {
        return new Participant("", "", "", "", 0, "", "", 0);
    }
}
