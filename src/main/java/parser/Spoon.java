package parser;

import lombok.extern.slf4j.Slf4j;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class Spoon {
    public static void main(String[] args) throws IOException {


        MavenLauncher launcher = new MavenLauncher("D:\\project\\sb-parser\\", MavenLauncher.SOURCE_TYPE.APP_SOURCE, "D:/software/apache-maven-3.9.5/");

        launcher.getEnvironment().setComplianceLevel(8);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();

        CtModel model = launcher.getModel();

        findNextCalls(model, "com.nntk.sb.service", "IUserInfoService", "queryUser");

    }


    public static void findNextCalls(CtModel ctModel, String pkg, String clazz, String methodName) {
        //find this method
        ctModel.getElements(new TypeFilter<CtMethod<?>>(CtMethod.class) {
            @Override
            public boolean matches(CtMethod<?> method) {
                if (!(method.getParent() instanceof CtClass)) {
                    return false;
                }

                CtClass cz = (CtClass) method.getParent();
                String curClazz = cz.getSimpleName();
                String curPkg = null != cz.getPackage() ? cz.getPackage().getQualifiedName() : "-";

                if (method.getSimpleName().equals(methodName)) {
                    System.out.println(curPkg + " - " + curClazz + " - " + method.getSimpleName());
                }
                return pkg.equals(curPkg) && clazz.equals(curClazz) && method.getSimpleName().equals(methodName);
            }
        }).forEach(m -> {
            System.out.println("Method found: " + m.getSimpleName());
            //find next calls
            List<CtInvocation<?>> invocations = m.getElements(new TypeFilter<>(CtInvocation.class));
            for (CtInvocation<?> invocation : invocations) {
                // Check if the invocation is a client call
                CtExecutableReference exec = invocation.getExecutable();
                System.out.println(m.getSimpleName() + " -> " + invocation.getExecutable().getDeclaringType() + "."
                        + invocation.getExecutable().getSignature());
            }
        });
    }


}