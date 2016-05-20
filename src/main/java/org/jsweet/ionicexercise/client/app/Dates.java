package org.jsweet.ionicexercise.client;

import static jsweet.lang.Globals.isNaN;
import static jsweet.lang.Globals.parseInt;
import static jsweet.util.Globals.array;
import static jsweet.util.Globals.string;

import jsweet.lang.Date;
import jsweet.lang.Object;
import jsweet.lang.RegExp;
import jsweet.lang.RegExpExecArray;

public class Dates {

	private static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	private static String DEFAULT_TIME_FORMAT = "hh:mm";

	public static void setDefaultDateFormat(String dateFormat) {
		DEFAULT_DATE_FORMAT = dateFormat;
	}

	public static void setDefaultTimeFormat(String timeFormat) {
		DEFAULT_TIME_FORMAT = timeFormat;
	}
	
	public static Date parseDate(String formattedDate) {
		return parseDate(formattedDate, DEFAULT_DATE_FORMAT, false);
	}

	/**
	 * you can specify format year: yyyy, month: MM, day: dd, hour: hh, min: mm,
	 * sec: ss
	 * 
	 * isUTC: is passed date a UTC one?
	 * 
	 * returns LOCAL TIME
	 */
	public static Date parseDate(String formattedDate, String format, boolean isUTC) {

		jsweet.lang.String formatString = string(format);

		formatString = string(formatString.replace("HH", "hh"));

		jsweet.lang.String dateParseRegexString = string(formatString.replace("yyyy", "(\\d{4})"));
		dateParseRegexString = string(dateParseRegexString.replace("MM", "(\\d{2})"));
		dateParseRegexString = string(dateParseRegexString.replace("dd", "(\\d{2})")); //
		dateParseRegexString = string(dateParseRegexString.replace("hh", "(\\d{2})")); //
		dateParseRegexString = string(dateParseRegexString.replace("mm", "(\\d{2})")); //
		dateParseRegexString = string(dateParseRegexString.replace("ss", "(\\d{2})"));

		dateParseRegexString = string(dateParseRegexString.replace(new RegExp("/\\//g"), "\\\\/"));

		Object dateFieldIndexesInString = new Object();
		dateFieldIndexesInString.$set("" + format.indexOf("yyyy"), "year");
		dateFieldIndexesInString.$set("" + format.indexOf("MM"), "month");
		dateFieldIndexesInString.$set("" + format.indexOf("dd"), "day");
		dateFieldIndexesInString.$set("" + format.indexOf("hh"), "hours");
		dateFieldIndexesInString.$set("" + format.indexOf("mm"), "minutes");
		dateFieldIndexesInString.$set("" + format.indexOf("ss"), "seconds");
		dateFieldIndexesInString = sortObjectByKey(dateFieldIndexesInString);

		Object captureGroupIndexes = new Object();
		int i = 1;
		String[] indexes = Object.keys(dateFieldIndexesInString);
		for (String idx : indexes) {
			if (idx.toString() != "-1") {
				captureGroupIndexes.$set(dateFieldIndexesInString.$get(idx).toString(), i);
				i++;
			}
		}

		RegExp dateRegex = new RegExp(dateParseRegexString + ".*", "g");
		RegExpExecArray matches = dateRegex.exec(formattedDate);
		if (matches != null && matches.length > 3) {
			String year = (String) matches.$get(captureGroupIndexes.$get("year").toString());
			String month = (String) matches.$get(captureGroupIndexes.$get("month").toString());
			String day = (String) matches.$get(captureGroupIndexes.$get("day").toString());
			String hours = captureGroupIndexes.$get("hours") != null
					? (String) matches.$get(captureGroupIndexes.$get("hours").toString()) : "0";
			String minutes = captureGroupIndexes.$get("minutes") != null
					? (String) matches.$get(captureGroupIndexes.$get("minutes").toString()) : "0";
			String seconds = captureGroupIndexes.$get("seconds") != null
					? (String) matches.$get(captureGroupIndexes.$get("seconds").toString()) : "0";
			if (isUTC) {
				return new Date(Date.UTC(parseInt(year), parseInt(month) - 1, parseInt(day), parseInt(hours),
						parseInt(minutes), parseInt(seconds)));
			} else {
				return new Date(parseInt(year), parseInt(month) - 1, parseInt(day), parseInt(hours), parseInt(minutes),
						parseInt(seconds));
			}
		}

		return null;
	}

	private static Object sortObjectByKey(Object obj) {
		String[] keys = Object.keys(obj);
		Object sortedObj = new Object();
		array(keys).sort();

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			sortedObj.$set(key, obj.$get(key));
		}

		return sortedObj;
	}

	public static boolean isDate(Object date) {
		return date != null && date instanceof Date && !isNaN(((Date) date).getTime());
	}

	public static String formatDate(Date date) {
		return formatDate(date, DEFAULT_DATE_FORMAT);
	}

	public static String formatDate(Date date, String format) {
		if (!Dates.isDate(date)) {
			return "";
		}

		return fullFormatDate(date, format);
	}

	private static String fullFormatDate(Date date, String format) {
		jsweet.lang.String dateFormatted = string(format);

		dateFormatted = string(dateFormatted.replace("yyyy", formatNumber(date.getFullYear(), 4)));
		dateFormatted = string(dateFormatted.replace("MM", formatNumber(date.getMonth() + 1, 2)));
		dateFormatted = string(dateFormatted.replace("dd", formatNumber(date.getDate(), 2)));
		dateFormatted = string(dateFormatted.replace("hh", formatNumber(date.getHours(), 2)));
		dateFormatted = string(dateFormatted.replace("mm", formatNumber(date.getMinutes(), 2)));
		dateFormatted = string(dateFormatted.replace("ss", formatNumber(date.getSeconds(), 2)));
		dateFormatted = string(dateFormatted.replace("SSS", formatNumber(date.getMilliseconds(), 3)));

		return string(dateFormatted);
	}
	
	public static String formatDateTime(Date date) {
		return fullFormatDate(date, DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT);
	}

	private static String formatNumber(Number num, int length) {
		return formatNumber(num, length, "0");
	}

	private static String formatNumber(Number num, int length, String paddingChar) {
		String str = "" + num;
		while (str.length() < length) {
			str = paddingChar + str;
		}

		return str;
	}
}