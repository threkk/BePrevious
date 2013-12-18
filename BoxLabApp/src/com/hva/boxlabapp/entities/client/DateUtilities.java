package com.hva.boxlabapp.entities.client;

import java.util.Calendar;
import java.util.Date;

public class DateUtilities {

	public static int getDayOfWeek(int calendarDay) {
		int result = calendarDay - Calendar.SUNDAY;
		if (result == 0) {
			result = 7;
		}
		return result;
	}

	public static int getTrailingDays(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));

		return 7 - getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
	}

	public static int getLeadingDays(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		return getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)) - 1;
	}

	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH);
	}

	public static Date addDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, day);
		return cal.getTime();
	}

	public static Date getStartOfMonth(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getEndOfMonth(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND,
				cal.getActualMaximum(Calendar.MILLISECOND));

		return cal.getTime();
	}

	public static boolean equalDay(Date dateA, Date dateB) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateA);
		int yearA = cal.get(Calendar.YEAR);
		int dayA = cal.get(Calendar.DAY_OF_YEAR);

		cal.setTime(dateB);
		int yearB = cal.get(Calendar.YEAR);
		int dayB = cal.get(Calendar.DAY_OF_YEAR);

		if (yearA != yearB) {
			return false;
		}

		if (dayA != dayB) {
			return false;
		}

		return true;

	}
}
