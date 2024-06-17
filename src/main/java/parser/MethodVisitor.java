package parser;

import cn.hutool.core.lang.tree.TreeNode;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class MethodVisitor extends VoidVisitorAdapter<Void> {
    List<TreeNode> nodeList = new ArrayList<>();
    private ArrayList<MethodDeclaration> m_Nodes = new ArrayList<MethodDeclaration>();


    @Override
    public void visit(ClassOrInterfaceDeclaration cd, Void arg) {
        super.visit(cd, arg);



//        List<BodyDeclaration<?>> collect = cd.getMembers().stream().filter(member -> true).collect(Collectors.toList());
//
//        System.out.println(collect);
    }

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        super.visit(md, arg);

        TypeDeclaration td = (TypeDeclaration) md.getParentNode().get();
        String packageName = td.findCompilationUnit().get().getPackageDeclaration().get().getNameAsString();
        String className = td.getNameAsString();
        String value = packageName + "." + className;
//        log.info("value:{},{}", value, md.getNameAsString());

//        md.getBody().ifPresent(blockStmt -> {
//            List<FieldAccessExpr> results = blockStmt.findAll(FieldAccessExpr.class);
//            for( FieldAccessExpr expr : results ) {
//                System.out.println(expr.getName());
//            }
//        });

        md.findAll(MethodCallExpr.class).forEach(mce -> {

//            System.out.println(mce.getNameAsString());


        });


//        AnnotationExpr annotation = md.getAnnotationByClass(Override.class).orElse(null);
//        if (annotation != null && md.getBody() != null) {
//
//        }

        log.info("===================================================");


//            m_Nodes.add(md);


    }

    public ArrayList<MethodDeclaration> getMethodDeclarations() {
        return m_Nodes;
    }


}
