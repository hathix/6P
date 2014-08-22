/*
 * Copyright (C) 2009 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ses.android.soap.utilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UrlUtils {

    public static boolean isValidUrl(String url) {

        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }

    }
    public static boolean validData(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
          return false;
        }  
	}
    
    public static long daysBetween(String startDate,String endDate) {
        Date sdate = null;
        Date edate = null;
        long daysBetween = 0;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        
        try {
			sdate = df.parse(endDate);
	        edate = df.parse(startDate);
//	        long diff = Math.abs(sdate.getTime() - edate.getTime());
	        long diff = edate.getTime() - sdate.getTime();
	        daysBetween = diff / (24 * 60 * 60 * 1000);
	  		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return daysBetween;
    }
  
}