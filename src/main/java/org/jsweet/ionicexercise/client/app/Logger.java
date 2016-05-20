package org.jsweet.ionicexercise.client;

import static jsweet.dom.Globals.console;
import static jsweet.util.Globals.array;
import static jsweet.util.Globals.object;
import static jsweet.util.Globals.string;

import jsweet.lang.Date;

public class Logger {
	public final static LogLevel GlobalLevelThreshold = LogLevel.Trace;
	private final static LogLevel DefaultLevelThreshold = LogLevel.Trace;

	private static final Logger root = Logger.get("ROOT");

	private String name;
	private LogLevel levelThreshold;

	public Logger(String name, LogLevel levelThreshold) {
		this.name = name;
		this.levelThreshold = levelThreshold;
	}

	public void trace(Object... messages) {
		this.log(LogLevel.Trace, messages);
	}

	public void debug(Object... messages) {
		this.log(LogLevel.Debug, messages);
	}

	public void info(Object... messages) {
		this.log(LogLevel.Info, messages);
	}

	public void warn(Object... messages) {
		this.log(LogLevel.Warn, messages);
	}

	public void error(Object error, Object... messages) {
		if (error != null) {
			String[] errorKeys = jsweet.lang.Object.keys(error);
			for (int i = 0; i < errorKeys.length; i++) {
				array(messages).splice(0, 0, string(errorKeys[i] + "=" + object(error).$get(errorKeys[i])));
			}
			// array(messages).splice(0, 0, "error type: " + typeof error);
		}
		array(messages).splice(0, 0, error);

		console.error(error);
		this.log(LogLevel.Error, messages);
	}

	public void log(LogLevel level, Object... messages) {
		if (level.ordinal() < this.levelThreshold.ordinal()
				|| level.ordinal() < Logger.GlobalLevelThreshold.ordinal()) {
			return;
		}

		if (console != null) {
			// String infos = Dates.formatDate(new Date(), "yyyy-MM-dd
			// hh:mm:ss.SSS") + " " + this.name + " ["
			// + level.name() + "]: ";
			//
			// Array<Object> logParams = new Array<Object>(string(infos));
			// logParams.push(messages);

			// Consumer<Object[]> consoleLog = (Object... params) -> {
			// console.log(params);
			// };
			// consoleLog.accept(array(logParams));
			// Function consoleLog =function((BiConsumer<Object,
			// Object[]>)console::log);
			// consoleLog.apply(console, logParams) ;

			// TODO : use properly console (do not stringify parameters!)
			String infos = Dates.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss.SSS") + " " + this.name + " ["
					+ level.name() + "]: " + array(messages).join(" ");
			console.log(infos);
		}
	}

	public static Logger get(String name) {
		return new Logger(name, DefaultLevelThreshold);
	}

	public static Logger getWithLevel(String name, LogLevel levelThreshold) {
		return new Logger(name, levelThreshold);
	}

	public static void traceRoot(Object... messages) {
		Logger.root.trace(messages);
	}

	public static void debugRoot(Object... messages) {
		Logger.root.debug(messages);
	}

	public static void infoRoot(Object... messages) {
		Logger.root.info(messages);
	}

	public static void warnRoot(Object... messages) {
		Logger.root.warn(messages);
	}

	public static void errorRoot(jsweet.lang.Object error, Object... messages) {
		Logger.root.error(error, messages);
	}
}
