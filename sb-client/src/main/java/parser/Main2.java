package parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.model.SymbolReference;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class Main2 {
    public static void main(String[] args) throws IOException {

//        Path pathToSource = Paths.get("D:\\project\\sb-parser\\src\\main\\java\\com\\nntk\\sb\\controller\\UserInfoController.java");
//        SourceRoot sourceRoot = new SourceRoot(pathToSource);
//        sourceRoot.tryToParse();

        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver(false));



        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(Paths.get("D:\\project\\sb-parser\\src\\"));

        List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);

        for (int i = 0; i < methodDeclarations.size(); i++) {
            System.out.println(methodDeclarations.get(i).getNameAsString());
        }

    }


}