package detectors;

/**
 * @author Otto Hiltunen 2268682h
 *
 */

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class UselessControlFlowDetector extends VoidVisitorAdapter<List<Breakpoints>> {

	private String className;
	private String methodName;
	private List<Breakpoints> collector;

	@Override
	public void visit(ClassOrInterfaceDeclaration cd, List<Breakpoints> collector) {

		this.className = cd.getName().asString();
		this.collector = collector;
		super.visit(cd, collector);
	}

	@Override
	public void visit(MethodDeclaration md, List<Breakpoints> collector) {

		this.methodName = md.getName().asString();

		/*
		 * if method body is empty - just create a breakpoint and return else visit its
		 * child nodes
		 */
		if (md.getBody().get().isEmpty()) {
			newBreakPoint(md);
			return;
		}

		super.visit(md, collector);

	}

	@Override
	public void visit(ConstructorDeclaration cod, List<Breakpoints> collector) {

		// empty constructor is not necessarily useless, since it is used to create an
		// instance object of some class
		if (cod.getBody().isEmpty())
			return;

		super.visit(cod, collector);

	}

	@Override
	public void visit(BlockStmt b, List<Breakpoints> collector) {

		/*
		 * handles block statements, such as, the bodies of if/else if/ else, for,
		 * while, etc statements
		 */
		if (b.isEmpty()) {
			newBreakPoint(b);
			return;
		}

		super.visit(b, collector);

	}

	@Override
	public void visit(SwitchStmt s, List<Breakpoints> collector) {

		NodeList<SwitchEntry> entries = s.getEntries();

		if (entries.isEmpty())
			newBreakPoint(s);

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
