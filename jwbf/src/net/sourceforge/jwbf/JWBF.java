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
package net.sourceforge.jwbf;


import net.sourceforge.jwbf.actions.http.Action;
import net.sourceforge.jwbf.misc.LogAppender;

import org.apache.commons.httpclient.HttpClient;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * 
 * to config logger level set env variable "loggerlevel" to 
 * {"info", "all", "warn", "trace", "error"}.
 * 
 * 
 * @author Thomas Stock
 *
 */
public final class JWBF {

	
//	private static Logger log;

	public static final String VERSION = "1.1.0.1";
	
	/**
	 * 
	 *
	 */
	private JWBF() {
//		do nothing 
	}


}
