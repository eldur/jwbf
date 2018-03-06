package net.sourceforge.jwbf;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

public class Logging {

  private Logging() {
    // do nothing
  }

  public static Supplier<ImmutableList<String>> newLogLinesSupplier() {

    ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

    final ListAppender<ILoggingEvent> mockAppender = new ListAppender<>();
    LoggerContext iLoggerFactory =
        (ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory();

    mockAppender.setContext(iLoggerFactory);
    mockAppender.start();

    root.addAppender(mockAppender);
    return new Supplier<ImmutableList<String>>() {

      @Override
      public ImmutableList<String> get() {
        mockAppender.stop();
        return FluentIterable.from(mockAppender.list) //
            .transform(Functions.toStringFunction()) //
            .toList();
      }
    };
  }
}
