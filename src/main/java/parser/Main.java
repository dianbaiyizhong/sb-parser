package parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {

        Path pathToSource = Paths.get("D:\\project\\sb-parser\\src\\");
        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();
        MethodVisitor methodVisitor = new MethodVisitor();

        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver(false));

        JavaParserFacade parserFacade = JavaParserFacade.get(typeSolver);

        for (CompilationUnit cu : compilations) {

            String packageName = cu.getPackageDeclaration().get().getNameAsString();
            if (packageName.equals("com.nntk.sb.domain")) {
                continue;
            }

            if (packageName.equals("com.nntk.sb.mapper")) {
                continue;
            }
            if (packageName.equals("parser")) {
                continue;
            }
            log.info("start parse dir:{}", packageName);

            List<ClassOrInterfaceDeclaration> methodDeclarations = cu.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration methodDeclaration : methodDeclarations) {
                try {
                    List<BodyDeclaration<?>> collect = methodDeclaration.getMembers().stream().filter(member -> {
                        return member.isClassOrInterfaceDeclaration();
                    }).collect(Collectors.toList());

                    System.out.println(collect);
                } catch (Exception e) {

                }

            }
//            cu.accept(methodVisitor, null);
        }

//        ArrayList<MethodDeclaration> nodes = methodVisitor.getMethodDeclarations();
//
//        for (int i = 0; i < nodes.size(); i++) {
//
//            System.out.println(nodes.get(i));
//        }

    }


}