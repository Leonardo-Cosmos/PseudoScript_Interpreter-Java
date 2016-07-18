package org.pseudoscript.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.pseudoscript.assembly.annotation.Executor;
import org.pseudoscript.assembly.annotation.Operation;
import org.pseudoscript.data.DataSource;
import org.pseudoscript.data.file.FileDataSourceFactory;
import org.pseudoscript.program.ExecutorInfo;
import org.pseudoscript.program.ExecutorInfoImpl;

public class Initializer {

	private static final Logger LOGGER = Logger.getLogger(Initializer.class.getSimpleName());
	
	private Map<String, DataSource> dataSources = new HashMap<>();
	
	private Map<String, ExecutorInfo> executors = new HashMap<>();
	
	public Initializer() {
		try {
			DataSource dataSource = FileDataSourceFactory.newDataSource("./sample/page.csv");
			dataSource.load();
			
			dataSources.put("page", dataSource);
		} catch (FileNotFoundException ex) {
			LOGGER.error("Failed to load data source.", ex);
		} catch (IOException ex) {
			LOGGER.error("Failed to load data source.", ex);
		}
		
		try {
			initExecutor("org.pseudoscript.selenium.SeleniumExecutor");
		} catch (Exception ex) {
			LOGGER.error("Failed to initialize executor.", ex);
		}
	}
	
	private void initExecutor(String className) {
		try {
			Class<?> executorClazz = Class.forName(className);
			
			Executor executorAnnotation = executorClazz.getAnnotation(Executor.class);
			if (executorAnnotation == null) {
				throw new IllegalArgumentException("This class is not executor.");
			}
			
			ExecutorInfo executorInfo = new ExecutorInfoImpl();
			
			Object executor = executorClazz.newInstance();
			executorInfo.setInstance(executor);
			
			Method[] methods = executorClazz.getMethods();
			for (Method method : methods) {
				Operation operationAnnotation = method.getAnnotation(Operation.class);
				if (operationAnnotation == null) {
					continue;
				}
				
				String methodName = operationAnnotation.name();
				executorInfo.getMethods().put(methodName, method);
			}

			String executorName = executorAnnotation.name();
			executors.put(executorName, executorInfo);
			
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Class cannot be loaded.", ex);
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new IllegalArgumentException("Create instance failed.", ex);
		}
	}

	public Map<String, DataSource> getDataSources() {
		return dataSources;
	}

	public Map<String, ExecutorInfo> getExecutors() {
		return executors;
	}
	
}
