package com.CS180.app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.printer.YamlPrinter;


/**
 * Hello world!
 *
 */
public class App 
{
   public static void main(String[] args) throws Exception {

        String path = "../my-app/src/main/java/com/CS180/files/SimpleComparison.java";
        FileInputStream input = new FileInputStream(path);
        CompilationUnit compUnit = StaticJavaParser.parse(input);
        input.close();

        ModifierVisitor javaVisitor = new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(IfStmt ifstmt, Void arg) {
                Expression expression = ifstmt.getCondition();
                if (expression instanceof BinaryExpr) {
                    BinaryExpr condition = (BinaryExpr) expression;
                    if (condition.getOperator() == BinaryExpr.Operator.NOT_EQUALS && ifstmt.getElseStmt().isPresent()) {
                        Statement thenStatement = ifstmt.getThenStmt().clone();
                        Statement elseStatement = ifstmt.getElseStmt().get().clone();
                        ifstmt.setThenStmt(elseStatement);
                        ifstmt.setElseStmt(thenStatement);
                        condition.setOperator(BinaryExpr.Operator.EQUALS);
                    }

                }
                return super.visit(ifstmt, arg);
            }
        };
        

        compUnit.accept(javaVisitor, null);
        
        System.out.println(compUnit.toString());
        YamlPrinter printer = new YamlPrinter(true);
        String astYaml = printer.output(compUnit);

        try (PrintWriter out = new PrintWriter(new FileOutputStream("SimpleComparisonAst.yaml"))) {
            out.print(astYaml);
        }
    }
}
