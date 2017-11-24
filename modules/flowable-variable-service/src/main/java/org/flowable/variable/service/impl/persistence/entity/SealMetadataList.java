package org.flowable.variable.service.impl.persistence.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.flowable.variable.service.ISealMetadata;

public class SealMetadataList extends ArrayList<Object> implements ISealMetadata, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean containsString(String str) {
		return this.contains(str);
	}

	@Override
	public boolean containsRegex(String str) {
		if (!isValidRegEx(str)) {
			throw new IllegalArgumentException("Invalid Regex supplied");
		}
		for (Object o : this) {
			if (o instanceof String)
				if (o.toString().matches(str))
					return true;
		}
		return false;
	}

	@Override
	public boolean containsNumber(Number number) {
		for (Object o : this)
			if (o instanceof Number)
				if ((Number) o == number)
					return true;
		return false;
	}

	@Override
	public boolean containsNumberNotEquals(Number number) {
		for (Object o : this)
			if (o instanceof Number)
				if ((Number) o == number)
					return false;
		return true;
	}

	@Override
	public boolean containsNumberLessThan(Number number) {
		for (Object o : this)
			if (o instanceof Number)
				if ((Double) o < (Double) number)
					return true;
		return false;
	}

	@Override
	public boolean containsNumberGreaterThan(Number number) {
		for (Object o : this)
			if (o instanceof Number)
				if ((Double) o > (Double) number)
					return true;
		return false;
	}

	@Override
	public boolean containsNumberLessThanOrEquals(Number number) {
		for (Object o : this)
			if (o instanceof Number)
				if ((Double) o <= (Double) number)
					return true;
		return false;
	}

	@Override
	public boolean containsNumberGreaterThanOrEquals(Number number) {
		for (Object o : this)
			if (o instanceof Number)
				if ((Double) o >= (Double) number)
					return true;
		return false;
	}

	@Override
	public boolean containsDateEquals(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateAsString = "";
		Date dateFromString = null;
		String inputDateAsString = "";
		Date inputDateFromString = null;

		for (Object o : this) {
			if (o instanceof Date) {
				try {
					dateAsString = simpleDateFormat.format((Date) o);
					dateFromString = simpleDateFormat.parse(dateAsString);

					inputDateAsString = simpleDateFormat.format(date);
					inputDateFromString = simpleDateFormat.parse(inputDateAsString);
					
					if (dateFromString.compareTo(inputDateFromString) == 0)
						return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsDateNotEquals(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateAsString = "";
		Date dateFromString = null;
		String inputDateAsString = "";
		Date inputDateFromString = null;

		for (Object o : this) {
			if (o instanceof Date) {
				try {
					dateAsString = simpleDateFormat.format((Date) o);
					dateFromString = simpleDateFormat.parse(dateAsString);

					inputDateAsString = simpleDateFormat.format(date);
					inputDateFromString = simpleDateFormat.parse(inputDateAsString);
					
					if (dateFromString.compareTo(inputDateFromString) != 0)
						return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsDateLessThan(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateAsString = "";
		Date dateFromString = null;
		String inputDateAsString = "";
		Date inputDateFromString = null;

		for (Object o : this) {
			if (o instanceof Date) {
				try {
					dateAsString = simpleDateFormat.format((Date) o);
					dateFromString = simpleDateFormat.parse(dateAsString);

					inputDateAsString = simpleDateFormat.format(date);
					inputDateFromString = simpleDateFormat.parse(inputDateAsString);
					
					if (dateFromString.compareTo(inputDateFromString) < 0)
						return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsDateGreaterThan(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateAsString = "";
		Date dateFromString = null;
		String inputDateAsString = "";
		Date inputDateFromString = null;

		for (Object o : this) {
			if (o instanceof Date) {
				try {
					dateAsString = simpleDateFormat.format((Date) o);
					dateFromString = simpleDateFormat.parse(dateAsString);

					inputDateAsString = simpleDateFormat.format(date);
					inputDateFromString = simpleDateFormat.parse(inputDateAsString);
					
					if (dateFromString.compareTo(inputDateFromString) > 0)
						return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsDateLessThanOrEquals(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateAsString = "";
		Date dateFromString = null;
		String inputDateAsString = "";
		Date inputDateFromString = null;

		for (Object o : this) {
			if (o instanceof Date) {
				try {
					dateAsString = simpleDateFormat.format((Date) o);
					dateFromString = simpleDateFormat.parse(dateAsString);

					inputDateAsString = simpleDateFormat.format(date);
					inputDateFromString = simpleDateFormat.parse(inputDateAsString);
					
					if (dateFromString.compareTo(inputDateFromString) <= 0)
						return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsDateGreaterThanOrEquals(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateAsString = "";
		Date dateFromString = null;
		String inputDateAsString = "";
		Date inputDateFromString = null;

		for (Object o : this) {
			if (o instanceof Date) {
				try {
					dateAsString = simpleDateFormat.format((Date) o);
					dateFromString = simpleDateFormat.parse(dateAsString);

					inputDateAsString = simpleDateFormat.format(date);
					inputDateFromString = simpleDateFormat.parse(inputDateAsString);
					
					if (dateFromString.compareTo(inputDateFromString) >= 0)
						return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public SealMetadataList append(Object obj) {
		this.add(obj);
		return this;
	}

	@Override
	public SealMetadataList removeObject(Object obj) {
		this.remove(obj);
		return this;
	}

	@Override
	public SealMetadataList clearMetadata() {
		this.clear();
		return this;
	}

	public boolean isValidRegEx(String pattern) {
		boolean result = false;
		try {
			Pattern.compile(pattern);
			result = true;
		} catch (PatternSyntaxException patternSyntaxException) {
			patternSyntaxException.printStackTrace();
		}
		return result;
	}
}
