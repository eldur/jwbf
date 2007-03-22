/*
 * Copyright 2007 Thomas Stock.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors:
 * 
 */
package net.sourceforge.jwbf.misc;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
/**
 * a simple file apender.
 * @author Thomas Stock
 *
 */
public class LogAppender implements Appender {

	/**
	 *
	 * @see org.apache.log4j.Appender#addFilter(org.apache.log4j.spi.Filter)
	 * @param newFilter a
	 */
	public void addFilter(Filter newFilter) {
	// do nothing
	}
	/**
	 * 
	 * @see org.apache.log4j.Appender#clearFilters()
	 */
	public void clearFilters() {
		// do nothing

	}
	/**
	 * 
	 * @see org.apache.log4j.Appender#close()
	 */
	public void close() {
		// do nothing

	}
	/**
	 *
	 * @see org.apache.log4j.Appender#doAppend(org.apache.log4j.spi.LoggingEvent)
	 * @param event a
	 */
	public void doAppend(LoggingEvent event) {
		if (event.getLevel() == Level.WARN) {
			System.err.println(">>> " + event.getMessage());
		} else {
			System.out.println(">>> " + event.getMessage());
		}

	}
	/**
	 * 
	 * @see org.apache.log4j.Appender#getErrorHandler()
	 * @return null
	 */
	public ErrorHandler getErrorHandler() {
		return null;
	}
	/**
	 *
	 * @see org.apache.log4j.Appender#getFilter()
	 * @return null
	 */
	public Filter getFilter() {
		return null;
	}

	/**
	 * @return null
	 * @see org.apache.log4j.Appender#getLayout()
	 */
	public Layout getLayout() {
		return null;
	}
	/**
	 * @return null
	 * @see org.apache.log4j.Appender#getName()
	 */
	public String getName() {
		return null;
	}
	/**
	 * @return false
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	public boolean requiresLayout() {
		return false;
	}
	/**
	 * not in use.
	 * @param errorHandler the
	 * @see org.apache.log4j.Appender#setErrorHandler(org.apache.log4j.spi.ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		// do nothing

	}
	/**
	 * not used.
	 * @param layout the
	 * @see org.apache.log4j.Appender#setLayout(org.apache.log4j.Layout)
	 */
	public void setLayout(Layout layout) {
		// do nothing

	}
	/**
	 * not used.
	 * @param name the
	 * @see org.apache.log4j.Appender#setName(java.lang.String)
	 */
	public void setName(String name) {
		// do nothing

	}

}
