package org.pseudoscript.interpreter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.pseudoscript.program.ExecutorNotFoundException;
import org.pseudoscript.program.OperationNotFoundException;
import org.pseudoscript.program.Program;
import org.pseudoscript.script.Script;
import org.pseudoscript.script.xml.XmlScriptConsumer;

public class App {

	private static final Logger LOGGER = Logger.getLogger(App.class.getSimpleName());
	
	public static void main(String[] args) {
		DOMConfigurator.configure("log4j.xml");
		
		LOGGER.info("Initialize resources.");
		Initializer initializer = new Initializer();
		Interpreter interpreter = new Interpreter(initializer.getDataSources(),
				initializer.getExecutors());
		XmlScriptConsumer consumer = new XmlScriptConsumer();
		consumer.setEnvironmentDataSources(initializer.getDataSources());
		
		LOGGER.info("Read script.");
		Reader reader = null;
		Script script = null;
		try {
			reader = new FileReader("./sample/Script.xml");
			consumer.setInput(reader);
			script = consumer.consume();
		} catch (IOException ex) {
			LOGGER.error("Failed to read script file.", ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					LOGGER.error("Failed to close file.", ex);
				}
			}
		}
		
		if (script == null) {
			return;
		}
		
		LOGGER.info("Interpret script as program.");
		Program program = null;
		try {
			program = interpreter.interpret(script);
		} catch (ExecutorNotFoundException | OperationNotFoundException ex) {
			LOGGER.error("Failed to interpret script.", ex);
		}
		
		if (program == null) {
			return;
		}
		
		LOGGER.info("Execute program.");
		program.execute();
		
		Finalizer finalizer = new Finalizer();
		finalizer.finalize(initializer.getDataSources());
	}

}
