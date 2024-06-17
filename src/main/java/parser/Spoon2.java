package parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Spoon2 {
    public static void main(String[] args) throws IOException {


        Launcher launcher = new Launcher();
        launcher.addInputResource("D:\\project\\sb-parser\\src\\main\\java\\");
        launcher.getEnvironment().setComplianceLevel(8);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();

        CtModel ctModel = launcher.getModel();


        List<ParserNode> nodeList = new ArrayList<>();

        List<CtElement> elements = SpoonUtil.findWithAnnotation(ctModel, RequestMapping.class);
        for (CtElement element : elements) {
            CtMethod<?> method = (CtMethod<?>) element;
            CtType<?> declaringType = method.getDeclaringType();
            String packageName = declaringType.getPackage().getQualifiedName();
            String className = declaringType.getSimpleName();
            nodeList.add(new ParserNode(packageName, className, method.getSimpleName()));
        }


        for (ParserNode parserNode : nodeList) {
            findNextCalls(ctModel, parserNode);
        }


    }


    public static void findNextCalls(CtModel ctModel, ParserNode node) {
        System.out.println(node);

        String clazz = node.getClassName();
        String pkg = node.getPkg();
        String methodName = node.getMethodName();

        ctModel.getElements(new TypeFilter<CtMethod<?>>(CtMethod.class) {
            @Override
            public boolean matches(CtMethod<?> method) {
                if (!(method.getParent() instanceof CtClass)) {
                    return false;
                }
                CtClass cz = (CtClass) method.getParent();
                String curClazz = cz.getSimpleName();
                String curPkg = null != cz.getPackage() ? cz.getPackage().getQualifiedName() : "-";
                return pkg.equals(curPkg) && clazz.equals(curClazz) && method.getSimpleName().equals(methodName);
            }
        }).forEach(m -> {
            List<CtInvocation<?>> invocations = m.getElements(new TypeFilter<>(CtInvocation.class));
            for (CtInvocation<?> invocation : invocations) {

                String newPkg = invocation.getExecutable().getDeclaringType().getPackage().getQualifiedName();
                String newMethodName = invocation.getExecutable().getActualMethod().getName();
                String newClassName = invocation.getExecutable().getDeclaringType().getSimpleName();

                ParserNode parserNode = new ParserNode(newPkg, newClassName, newMethodName);
                System.out.println(parserNode);

                findNextCalls(ctModel, parserNode);

            }
        });
    }


}