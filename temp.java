package com.CS180.app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.printer.YamlPrinter;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.Statement;

/**
 * Hello world!
 *
 */
public class App 
{
   public static void main(String[] args) throws Exception {
        // Parse the input file
        String path = "my-app/src/main/java/com/CS180/files/SimpleComparison.java";
        FileInputStream in = new FileInputStream(path);
        CompilationUnit cu = StaticJavaParser.parse(in);
        in.close();

        // Process the refactoring
        cu.findAll(IfStmt.class).forEach(ifStmt -> {
            if (ifStmt.getCondition().toString().equals("!=") && ifStmt.getElseStmt().isPresent()) {
                // Swap "then" and "else" statements
                NodeList<Statement> thenStmts = ifStmt.getThenStmt().asBlockStmt().getStatements();
                NodeList<Statement> elseStmts = ifStmt.getElseStmt().get().asBlockStmt().getStatements();
                ifStmt.setThenStmt(new BlockStmt(elseStmts));
                ifStmt.setElseStmt(new BlockStmt(thenStmts));

                // Change "!=" to "=="
                ifStmt.getCondition().replace(StaticJavaParser.parseExpression("=="));
            }
        });

        // Save the refactored AST to a YAML file
        YamlPrinter printer = new YamlPrinter(true);
        String astYaml = printer.output(cu);

        // Write the AST to the YAML file
        try (PrintWriter out = new PrintWriter(new FileOutputStream("SimpleComparison_ast.yaml"))) {
            out.print(astYaml);
        }
    }
}
