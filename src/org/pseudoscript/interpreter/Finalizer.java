package org.pseudoscript.interpreter;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.pseudoscript.data.DataSource;

public class Finalizer {

	private static final Logger LOGGER = Logger.getLogger(Finalizer.class.getSimpleName());
	
	public Finalizer() {
		
	}
	
	public void finalize(Map<String, DataSource> dataSources) {
		if (dataSources == null) {
			return;
		}
		
		for (Entry<String, DataSource> entry : dataSources.entrySet()) {
			DataSource dataSource = entry.getValue();
			try {
				dataSource.save();
			} catch (IOException ex) {
				LOGGER.error("Failed to save data source.", ex);
			}
		}
	}
	
}
