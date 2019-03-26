
package detectors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * @author Otto Hiltunen 2268682h
 *
 */

public class Driver {

	private static Scanner reader = new Scanner(System.in);

	public static void main(String[] args) {
		FileInputStream inStrm = null;

		// get path as user input
		String FILE_PATH;
		
		if (args.length>0) {
			FILE_PATH = args[0];
		} else {
			System.out.println("please enter a file path");
			FILE_PATH = reader.nextLine();
		}
		

		try {
			// default path, if a path was not specified
			if (FILE_PATH.equals(""))
				FILE_PATH = "src/main/java/detectors/Calculator.java";
			// parse the file
			inStrm = new FileInputStream(FILE_PATH);
			CompilationUnit cu = JavaParser.parse(inStrm);

			// create visitors
			UselessControlFlowDetector cfVisitor = new UselessControlFlowDetector();
			RecursionDetector rdVisitor = new RecursionDetector();

			// Create collectors to store the breakpoints
			List<Breakpoints> uselessControlFlowCollector = new ArrayList<>();
			List<Breakpoints> recursionCollector = new ArrayList<>();

			// visit the file
			cfVisitor.visit(cu, uselessControlFlowCollector);
			rdVisitor.visit(cu, recursionCollector);

			// Output the breakpoints
			System.out.println("Useless control flows:");
			for (Breakpoints b : uselessControlFlowCollector)
				System.out.println(b);
			System.out.println("Polymorphic recursions:");
			for (Breakpoints b : recursionCollector)
				System.out.println(b);

			// handle exceptions
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParseProblemException e) {
			e.printStackTrace();
		} finally {
			try {
				inStrm.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
