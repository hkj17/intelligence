package com.is.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.is.constant.ParameterKeys;


/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2016�?1�?14�?
 * 类说�? 
 */
public class BusinessHelper {

	public static Map<String, String> changeMap(MultivaluedMap<String, String> formParams){
		Map<String, String> controlMap = new HashMap<String, String>();
		for(Map.Entry<String, List<String>> entry : formParams.entrySet()){
			controlMap.put(entry.getKey(), entry.getValue().get(0));
		}
		return controlMap;
	}
	
	 /**
     * 获取某年第一天日�?
     * @param year 年份
     * @return Date
     */
	 public static Date getYearFirst(int year){
	        Calendar calendar = Calendar.getInstance();
	        calendar.clear();
	        calendar.set(Calendar.YEAR, year);
	        Date currYearFirst = calendar.getTime();
	        return currYearFirst;
	    }
	     
	 public static Date getYearFirstMid(int year){
		 Calendar calendar = Calendar.getInstance();
	     calendar.clear();
	     calendar.set(Calendar.YEAR, year);
	     calendar.set(Calendar.HOUR_OF_DAY, 12);
	     Date currYearFirst = calendar.getTime();
	     return currYearFirst;
	 }
	    /**
	     * 获取某年�?后一天日�?
	     * @param year 年份
	     * @return Date
	     */
	/*    public static Date getYearLast(int year){
	        Calendar calendar = Calendar.getInstance();
	        calendar.clear();
	        calendar.set(Calendar.YEAR, year);
	        calendar.roll(Calendar.DAY_OF_YEAR, -1);
	        Date currYearLast = calendar.getTime();
	         
	        return currYearLast;
	    }*/
	    
	    /**
	     * 获取某年某月第一天日�?
	     * @param year 年份
	     * @param month 月份
	     * @return Date
	     */
	    public static Date getMonthFirst(int year,int month){
	    	Calendar calendar = Calendar.getInstance();
	        calendar.clear();
	        calendar.set(Calendar.YEAR, year);
	        calendar.set(Calendar.MONTH, month - 1);
	        Date currYearFirst = calendar.getTime();
	        return currYearFirst;
	    }
	    
	    public static Date getMonthFirstMid(int year,int month){
	    	Calendar calendar = Calendar.getInstance();
	        calendar.clear();
	        calendar.set(Calendar.YEAR, year);
	        calendar.set(Calendar.MONTH, month - 1);
	        calendar.set(Calendar.HOUR_OF_DAY, 12);
	        Date currYearFirst = calendar.getTime();
	        return currYearFirst;
	    }
	    
	    public static Date getTodayFirst(int year,int month,int day){
	    	Calendar calendar = Calendar.getInstance();
	        calendar.clear();
	        calendar.set(Calendar.YEAR, year);
	        calendar.set(Calendar.MONTH, month - 1);
	        calendar.set(Calendar.DATE, day);
	        Date currYearFirst = calendar.getTime();
	        return currYearFirst;
	    }
	    
	    public static Date getTodayMidDate(Date today){
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(today);
	    	calendar.set(Calendar.HOUR_OF_DAY, 12);
	    	calendar.set(Calendar.MINUTE, 0);
	    	calendar.set(Calendar.SECOND, 0);
	    	return calendar.getTime();
	    }
	    
	    public static Date getDayMidDate(int year,int month,int day){
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.clear();
	    	calendar.set(year, month - 1, day, 12, 0, 0);
	    	return calendar.getTime();
	    }
	    
	    public static Date getNextDayMidDate(int year,int month,int day){
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.clear();
	    	calendar.set(year, month, day, 12, 0, 0);
	    	calendar.add(Calendar.DATE, 1);
	    	return calendar.getTime();
	    }
	    
	    public static Date getNextHourDate(Date date){
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(date);
	    	calendar.add(Calendar.HOUR_OF_DAY, 1);
	    	return calendar.getTime();
	    }
	    
	    public static Date getYesterDayMidDate(Date today){
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(today);
	    	calendar.set(Calendar.HOUR_OF_DAY, 12);
	    	calendar.set(Calendar.MINUTE, 0);
	    	calendar.set(Calendar.SECOND, 0);
	    	calendar.add(Calendar.DATE, -1);
	    	return calendar.getTime();
	    }
	    
	    public static Date getTomorrowMidDate(Date today){
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(today);
	    	calendar.set(Calendar.HOUR_OF_DAY, 12);
	    	calendar.set(Calendar.MINUTE, 0);
	    	calendar.set(Calendar.SECOND, 0);
	    	calendar.add(Calendar.DATE, 1);
	    	return calendar.getTime();
	    }
	    
	    public static Map<String, String> createBlankMonthMap(){
	    	Map<String, String> map = new HashMap<String,String>();
	    	for(int i=1;i<13;i++){
	    		map.put(String.valueOf(i), String.valueOf(0));
	    	}
	    	return map;
	    }
	    
	    public static Map<String, String> createBlankDayMap(int year,int month){
	    	Map<String, String> map = new HashMap<String,String>();
	    	int days = 30;
	    	switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				days = 31;
				break;
			case 2:
				days = isLeapYear(year)?29:28;
				break;
			default:
				break;
			}
	    	for(int i=1;i<=days;i++){
	    		map.put(String.valueOf(i), String.valueOf(0));
	    	}
	    	return map;
	    }
	    
	    public static Map<String, String>  createBlankHourMap(){
	    	Map<String, String> map = new HashMap<String,String>();
	    	for(int i=13;i<24;i++){
	    		map.put(String.valueOf(i), String.valueOf(0));
	    	}
	    	for(int i=0;i<13;i++){
	    		map.put(String.valueOf(i), String.valueOf(0));
	    	}
	    	return map;
	    }
	    
	    public static boolean isYesterday(int year,int month,int day){
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.add(Calendar.DATE, -1);
	    	return (year == calendar.get(Calendar.YEAR) && (month - 1) == calendar.get(Calendar.MONTH) && day == calendar.get(Calendar.DATE));
	    }
	    
	    public static boolean isLeapYear(int year){
	    	return (year % 4 == 0 && year % 100 != 0 || year % 400 == 0);
	    }
	    
	    public static int getYear(Date date){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	return cal.get(Calendar.YEAR);
	    }
	    
	    public static int getMonth(Date date){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	return cal.get(Calendar.MONTH) + 1;
	    }
	    
	    public static int getDay(Date date){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	return cal.get(Calendar.DATE);
	    }
	    
	    public static int getHour(Date date){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	return cal.get(Calendar.HOUR_OF_DAY);
	    }
	    
	    public static boolean isToday(int year,int month,int date){
	    	Calendar cal = Calendar.getInstance();
	    	return (year == cal.get(Calendar.YEAR) && month == cal.get(Calendar.MONTH) && date ==cal.get(Calendar.DATE));
	    }
	    
	    public static Date getAssignedEarlyDate(Date now,int days){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(now);
	    	cal.add(Calendar.DATE, -days);
	    	return cal.getTime();
	    }
	    
	    public static Date getAssignedLatelyDate(Date now,int days){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(now);
	    	cal.add(Calendar.DATE, days);
	    	return cal.getTime();
	    }
	    
	    public static Date getYearFirstDate(Date date){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	cal.set(Calendar.MONTH, 0);
	    	cal.set(Calendar.DATE, 1);
	    	cal.set(Calendar.HOUR, 12);
	    	cal.set(Calendar.MINUTE, 0);
	    	cal.set(Calendar.SECOND, 0);
	    	return cal.getTime();
	    }
	    
	    public static Date getMonthFirstDate(Date date){
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	cal.set(Calendar.DATE, 1);
	    	cal.set(Calendar.HOUR, 12);
	    	cal.set(Calendar.MINUTE, 0);
	    	cal.set(Calendar.SECOND, 0);
	    	return cal.getTime();
	    }
	    
	    public static boolean boxChecked(Map<String, Integer> boxItemMap,String item){
	    	return boxItemMap.containsKey(item) && (boxItemMap.get(item) == 1);
	    }
	    
	    public static float div(float v1,float v2){
			 BigDecimal b1 = new BigDecimal(Float.toString(v1));
		     BigDecimal b2 = new BigDecimal(Float.toString(v2));
		     return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP).floatValue();
		}
	    
	    //如果结束时间早于当前时间，一律返�?0
	    public static int calExpDate(Date endDate){
	    	Calendar endCalendar = Calendar.getInstance();
	    	endCalendar.setTime(endDate);
	    	Calendar today = Calendar.getInstance();
	    	endCalendar.set(Calendar.HOUR_OF_DAY, 0);
	    	endCalendar.set(Calendar.MINUTE, 0);
	    	endCalendar.set(Calendar.SECOND, 0);
	    	today.set(Calendar.HOUR_OF_DAY, 0);
	    	today.set(Calendar.MINUTE, 0);
	    	today.set(Calendar.SECOND, 0);
	    	float fday = ((endCalendar.getTimeInMillis() - today.getTimeInMillis())/(float)(1000*3600*24));
	    	int day =Math.round(fday);
	    	if(day < 0){
	    		day = 0;
	    	}
	    	return day;
	    }
	    
	    //比较时间是否晚于（或等于）标准时�?
	    public static boolean afterOrEqual(String standardDate,String comparedDate){
	    	Date standard = CommonUtil.string2Date(standardDate, ParameterKeys.DATE_FORMAT);
	    	Date compare = CommonUtil.string2Date(comparedDate, ParameterKeys.DATE_FORMAT);
	    	return (compare.after(standard) || compare.equals(standard));
	    }
	    
	    //比较时间是否早于（或等于）标准时�?
	    public static boolean beforeOrEqual(String standardDate,String comparedDate){
	    	Date standard = CommonUtil.string2Date(standardDate, ParameterKeys.DATE_FORMAT);
	    	Date compare = CommonUtil.string2Date(comparedDate, ParameterKeys.DATE_FORMAT);
	    	return (compare.before(standard) || compare.equals(standard));
	    }
	    
	    public static void main(String[] args){
	    	Calendar calendar = Calendar.getInstance();
	    	calendar.add(Calendar.DATE, 1);
	    	Date date = CommonUtil.string2Date("2016-04-23T15:49:44");
	    	
	    	System.out.println(calExpDate(date));
//	    	Calendar endCalendar = Calendar.getInstance();
//	    	endCalendar.set(Calendar.HOUR_OF_DAY, 0);
//	    	endCalendar.set(Calendar.MINUTE, 0);
//	    	endCalendar.set(Calendar.SECOND, 0);
//	    	System.out.println(endCalendar.getTime());
//	    	System.out.println(endCalendar.getTime().getTime());
		}
}
