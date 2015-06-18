package ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class ExampleVisitor extends ASTVisitor{


	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println("method: "+node.getName());
		return super.visit(node);
	}


}
