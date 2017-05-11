package com.winchannel.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    private static String DATE_TIME_FORMATE = "yyyy-MM-dd HH:mm:ss";

    public static String getStandDateTime(){
        return  getStandDateTime(new Date());
    }
    
    public static String getStandDateTime(Date date){
        SimpleDateFormat formate = new SimpleDateFormat(DATE_TIME_FORMATE);
        return  formate.format(date);
    }

	public static String getNextDate(String preDate) {
		String nextDate = "";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(preDate);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 1);
			date = calendar.getTime();
			nextDate = formatter.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return nextDate;
	}



}
