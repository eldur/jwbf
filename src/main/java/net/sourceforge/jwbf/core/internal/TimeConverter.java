package net.sourceforge.jwbf.core.internal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.common.base.Optional;

public class TimeConverter {

  private TimeConverter() {
    // lib
  }

  public static final String YYYYMMDD_T_HHMMSS_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  public static String valueOf(Date date) {
    // XXX change with java8
    SimpleDateFormat simpleDateFormat = newFormatter(YYYYMMDD_T_HHMMSS_Z);
    return simpleDateFormat.format(date);
  }

  static SimpleDateFormat newFormatter(String pattern) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return simpleDateFormat;
  }

  public static Optional<Date> from(String timestamp, String pattern) {
    SimpleDateFormat sdf = newFormatter(pattern);
    try {
      return Optional.of(sdf.parse(timestamp));
    } catch (ParseException e) {
      return Optional.absent();
    }
  }
}
