package parser;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.AnnotationFilter;

import java.lang.annotation.Annotation;
import java.util.List;

public class SpoonUtil {

    public static List<CtElement> findWithAnnotation(CtModel ctModel, Class<? extends Annotation> annotationType) {
        return ctModel.getRootPackage().getElements(new AnnotationFilter<>(annotationType));
    }
}
