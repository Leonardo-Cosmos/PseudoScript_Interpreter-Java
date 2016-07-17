package org.pseudoscript.interpreter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pseudoscript.data.DataSource;
import org.pseudoscript.program.ExecutorInfo;
import org.pseudoscript.program.ExecutorNotFoundException;
import org.pseudoscript.program.Operation;
import org.pseudoscript.program.OperationImpl;
import org.pseudoscript.program.OperationNotFoundException;
import org.pseudoscript.program.Program;
import org.pseudoscript.program.ProgramImpl;
import org.pseudoscript.script.ArgumentInfo;
import org.pseudoscript.script.OperationInfo;
import org.pseudoscript.script.Script;

public class Interpreter {

	private Map<String, ExecutorInfo> executors = new HashMap<>();

	public Interpreter(Map<String, DataSource> dataSources, Map<String, ExecutorInfo> executors) {
		this.executors = executors;
	}

	public Program interpret(Script script) throws ExecutorNotFoundException, OperationNotFoundException {
		Program program = new ProgramImpl();

		for (OperationInfo operationInfo : script.getOperations()) {
			String executorName = operationInfo.getExecutor();
			ExecutorInfo executorInfo = executors.get(executorName);
			if (executorInfo == null) {
				throw new ExecutorNotFoundException();
			}

			String operationName = operationInfo.getName();
			Method method = executorInfo.getMethods().get(operationName);
			if (method == null) {
				throw new OperationNotFoundException();
			}
			
			List<Object> argumentList = new ArrayList<>();
			for (ArgumentInfo argumentInfo : operationInfo.getArguments()) {
				Object argument = argumentInfo.getValue();
				argumentList.add(argument);
			}
			Object[] arguments = new Object[argumentList.size()];
			argumentList.toArray(arguments);
			
			Operation operation = new OperationImpl();
			operation.setExecutor(executorInfo);
			operation.setName(operationName);
			operation.setArguments(arguments);
			
			program.getOperations().add(operation);
		}

		return program;
	}

}
