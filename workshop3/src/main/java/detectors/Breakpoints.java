package detectors;

import java.util.ArrayList;


/**
 * @author Otto Hiltunen 2268682h
 *
 */
public class Breakpoints {
	
	private String className;
	private String methodName;
	private int startLine, endLine;

	public Breakpoints(String className, String methodName, int startLine, int endLine) {
		this.className = className;
		this.methodName = methodName;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	@Override
	public String toString() {
		return "className=" + className + ", methodName=" + methodName + ", startLine=" + startLine
				+ ", endLine=" + endLine;
	}

	
}

