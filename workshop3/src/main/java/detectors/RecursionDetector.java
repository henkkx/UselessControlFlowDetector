package detectors;

import java.util.List;
import java.util.Stack;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * @author Otto Hiltunen 2268682h
 *
 */

public class RecursionDetector extends VoidVisitorAdapter<List<Breakpoints>> {

	private String methodName;
	private String className;
	private Stack<MethodDeclaration> mdStack = new Stack<>();
	private List<Breakpoints> collector;

	/**
	 * whether the methodDeclaration on top of the stack contains recursion
	 */
	private boolean isRecursive = false;

	@Override
	public void visit(ClassOrInterfaceDeclaration cd, List<Breakpoints> collector) {

		this.className = cd.getName().asString();
		this.collector = collector;

		super.visit(cd, collector);

	}

	@Override
	public void visit(MethodDeclaration md, List<Breakpoints> collector) {

		mdStack.push(md);

		// visit child nodes
		super.visit(md, collector);

		if (isRecursive) {
			this.methodName = md.getName().asString();

			newBreakPoint(mdStack.pop());
			isRecursive = false;
		}
	}

	@Override
	public void visit(MethodCallExpr m, List<Breakpoints> collector) {

		String methodName = mdStack.peek().getName().asString();

		/*
		 * if the method that was called has the same name as the methodDeclaration
		 * -> indicate that the method on top of the stack contains recursion
		 */
		if (m.getName().asString().equals(methodName)) {
			isRecursive = true;
		}

	}

	/**
	 * @param n - any Node object e.g. A blockStmt or a MethodDeclaration
	 * 
	 *          creates a new breakpoint object and adds it to the collector list
	 *          that stores the breakpoints.
	 */
	public void newBreakPoint(Node n) {

		this.collector.add(
				/*
				 * creates a breakpoint object, storing the name of the class and the method
				 * containing the node as well as the start and end lines of the node
				 */
				new Breakpoints(className, methodName, n.getRange().get().begin.line, n.getRange().get().end.line));

	}

}
