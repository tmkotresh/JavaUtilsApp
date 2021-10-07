package string;

import static string.ConstantUtility.BLANK;
import static string.ConstantUtility.COMMA;
import static string.ConstantUtility.HASH;
import static string.ConstantUtility.LEFT_PRNTHS;
import static string.ConstantUtility.PERIOD;
import static string.ConstantUtility.RIGHT_PRNTHS;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.math.NumberUtils;

public class StringUtility {

	private static final String SPLIT_REGEX_SLASH = "\\";

	private StringUtility() {
	}

	public static String trimToDefault(String value, String defaultValue) {
		if (Objects.nonNull(value) && value.trim().length() > 0) {
			return value.trim();
		} else {
			return defaultValue;
		}
	}

	public static boolean isBlank(String value) {
		String tmp = trimToDefault(value, BLANK);
		return BLANK.equals(tmp);
	}

	public static boolean isNotBlank(String value) {
		return !isBlank(value);
	}

	public static Double toDouble(String value) {
		Double numValue = null;
		try {
			value = trimToDefault(value, null);
			if (value == null || value.length() == 0) {
				return null;
			}
			numValue = Double.valueOf(value);
		} catch (NumberFormatException e) {
		}
		return numValue;
	}

	public static String unionArrayNonEmptyOnly(String delimiter, String[] strArray) {
		return Arrays.stream(strArray).filter(StringUtility::isNotBlank).collect(Collectors.joining(delimiter));
	}

	public static String join(String delimeter, String... str) {
		StringJoiner joiner = new StringJoiner(delimeter);
		for (int i = 0; i < str.length; i++) {
			if (isNotBlank(str[i])) {
				joiner.add(str[i]);
			}
		}
		return joiner.toString();
	}

	public static String unionMultiple(String delimiter, String sourceStr, String... toAppendStr) {
		StringBuilder sbr = new StringBuilder();
		for (int i = 0; i < toAppendStr.length; i++) {
			sbr.append(union(delimiter, sourceStr, toAppendStr[i]));
		}
		return sbr.toString();
	}

	public static String union(String delimiter, String sourceStr, String toAppendStr) {
		boolean sourceExists = isNotBlank(sourceStr);
		boolean toAppendExists = isNotBlank(toAppendStr);
		String result = null;
		if (sourceExists && toAppendExists) {
			return uniqueUnion(delimiter, sourceStr, toAppendStr);
		} else if (sourceExists) {
			result = sourceStr;
		} else if (toAppendExists) {
			result = toAppendStr;
		} else {
			result = BLANK;
		}
		return result;
	}

	private static String uniqueUnion(String delimiter, String source, String toAppendStr) {
		List<String> contents = Arrays.asList(split(source, delimiter.trim())).stream().map(String::trim).distinct()
				.collect(Collectors.toList());
		List<String> toAppendStrList = Arrays.asList(split(toAppendStr, delimiter.trim())).stream().map(String::trim)
				.distinct().collect(Collectors.toList());
		Set<String> set = new LinkedHashSet<>();
		set.addAll(contents);
		set.addAll(toAppendStrList);
		return set.stream().filter(StringUtility::isNotBlank).collect(Collectors.joining(delimiter));
	}

	public static Double[] toDoubleArray(String str, int length) {
		if (null == str || isBlank(str)) {
			return new Double[0];
		}
		String[] strArray = split(str, COMMA);
		if (strArray.length != length) {
			return new Double[0];
		}
		Double[] result = new Double[6];
		for (int i = 0; i < strArray.length; i++) {
			result[i] = toDouble(strArray[i]);
		}
		return result;
	}

	public static Integer toInteger(String value) {
		Integer numValue = null;
		try {
			value = trimToDefault(value, null);
			if (value == null || value.length() == 0) {
				return null;
			}
			numValue = Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
		return numValue;
	}

	public static int toInteger(Double value) {
		int numValue = 0;
		try {
			value = (Objects.nonNull(value) ? value : 0);
			numValue = value.intValue();
		} catch (NumberFormatException e) {
			numValue = 0;
		}
		return numValue;
	}

	public static char toChar(String str) {
		str = trimToDefault(str, BLANK);
		char result = Character.MIN_VALUE;
		if (str.length() == 1) {
			result = str.charAt(0);
		}
		return result;
	}

	public static boolean toBoolean(String str, char toMatch) {
		char c = toChar(str);
		return toMatch == c;
	}

	public static String trimToDefault(Object newValue, String defaultValue) {
		if (Objects.nonNull(newValue)) {
			return newValue.toString();
		}
		return defaultValue;
	}

	public static String[] split(String source, String delimiter) {
		source = trimToDefault(source, BLANK);
		if (null == source || isBlank(source)) {
			return new String[0];
		}
		return source.split(SPLIT_REGEX_SLASH + delimiter);
	}

	public static String convertToRequiredPrecisionFormat(Double value, int precision, int scale) {
		if (Objects.isNull(value)) {
			return null;
		}
		StringBuilder precisionLength = new StringBuilder();
		for (int i = 0; i < precision; i++) {
			precisionLength.append(HASH);
		}
		precisionLength.append(PERIOD);
		for (int i = 0; i < scale; i++) {
			precisionLength.append(HASH);
		}
		DecimalFormat df = new DecimalFormat(precisionLength.toString());
		return df.format(value);
	}

	public static List<String> toList(String commaSepeartedValues) {
		if (StringUtility.isBlank(commaSepeartedValues)) {
			return Collections.emptyList();
		}
		String[] items = split(commaSepeartedValues, COMMA);
		return Arrays.asList(items);
	}

	public static String appendPrefixTime(String source, String target) {
		source = trimToDefault(source, BLANK);
		target = trimToDefault(target, BLANK);

		if (BLANK.equals(target)) {
			return source;
		}

		if ("".equals(source)) {
			return target;
		}
		return source + System.lineSeparator() + target;
	}

	public static String trimExtraSpaces(String strVal) {
		String result = StringUtility.trimToDefault(strVal, "");
		while (result.contains("  ")) {
			result = result.replace("  ", " ");
		}
		return result;
	}

	public static boolean hasAnyKeyInLine(String line, List<String> keys) {
		if (StringUtility.isBlank(line)) {
			return false;
		}
		for (String key : keys) {
			key = StringUtility.trimToDefault(key, "");
			if (StringUtility.isNotBlank(key) && line.toUpperCase().contains(key.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	public static String removeLeadingZero(String str) {
		if (Objects.isNull(str)) {
			return null;
		}
		String[] commaSeperatedValues = str.split(COMMA);
		StringBuilder finalValue = new StringBuilder();
		for (String eachValue : commaSeperatedValues) {
			eachValue = eachValue.trim();
			int i = 0;
			while (i < eachValue.length() && eachValue.charAt(i) == '0') {
				i++;
			}
			StringBuilder sb = new StringBuilder(eachValue);
			if (finalValue.length() != 0) {
				finalValue.append(COMMA);
			}
			finalValue.append(sb.replace(0, i, ""));
		}
		return finalValue.toString();
	}

	public static String removeTrailingCharIfExist(String source, char tChar) {
		if (isNotBlank(source) && source.charAt(source.length() - 1) == tChar) {
			source = source.trim();
			return source.substring(0, source.length() - 1);
		}
		return source;
	}

	public static int countOccurences(String str, String wrd, String sep) {
		int count = 0;
		if (Objects.nonNull(str) && Objects.nonNull(wrd)) {
			String[] splStr = str.split(sep);
			for (int i = 0; i < splStr.length; i++) {
				if (splStr[i].equals(wrd)) {
					count++;
				}
			}
		}
		return count;
	}

	/*
	 * public static boolean isDifferentDoubleValues(String val1, String val2) {
	 * boolean isVal1ValidNumber = NumberUtils.isCreatable(val1); boolean
	 * isVal2ValidNumber = NumberUtils.isCreatable(val2);
	 * 
	 * if (!isVal1ValidNumber && !isVal2ValidNumber) { return false; } if
	 * (!isVal1ValidNumber || !isVal2ValidNumber) { return false; } Double value1 =
	 * Double.valueOf(val1); Double value2 = Double.valueOf(val2); return
	 * !value1.equals(value2); }
	 */

	public static String encloseWithInParentheses(String input) {
		if (isNotBlank(input)) {
			return LEFT_PRNTHS + input + RIGHT_PRNTHS;
		}
		return BLANK;
	}

	public static String replaceUmlaute(String output) {
		return output.replace("\u00fc", "ue").replace("\u00f6", "oe").replace("\u00e4", "ae").replace("\u00df", "ss")
				.replaceAll("\u00dc(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Ue")
				.replaceAll("\u00d6(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Oe")
				.replaceAll("\u00c4(?=[a-z\u00e4\u00f6\u00fc\u00df ])", "Ae").replace("\u00dc", "UE")
				.replace("\u00d6", "OE").replace("\u00c4", "AE").replace("\u00df", "ss");

	}

	/*
	 * public static String getLeftPaddedString(String input, char paddedCharacter,
	 * int length) {
	 * 
	 * StringUtils.leftPad(input, length, paddedCharacter); return
	 * Objects.nonNull(input) ? StringUtils.leftPad(input, length, paddedCharacter)
	 * : input; }
	 */

}
