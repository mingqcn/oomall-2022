package cn.edu.xmu.javaee.core.aop;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedAnnotationTypes("cn.edu.xmu.javaee.core.aop.CopyFrom")
public class CopyFromProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty())
            return false;

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder("CloneFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        messager.printMessage(Diagnostic.Kind.NOTE, "CopyFromProcessor start");
        roundEnv.getElementsAnnotatedWith(CopyFrom.class).stream()
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(element -> (TypeElement)element)
                .forEach(element -> {
//                    messager.printMessage(Diagnostic.Kind.NOTE, getSourceClass(element, "value").toString());
                    messager.printMessage(Diagnostic.Kind.NOTE, element.getSimpleName().toString());

                    List<ExecutableElement> targetMethods = getAllMethods(element)
                            .stream()
                            .filter(method -> method.getParameters().size() == 1 && method.getSimpleName().toString().startsWith("set"))
                            .collect(Collectors.toList());
                    messager.printMessage(Diagnostic.Kind.NOTE, targetMethods.toString());

                    getSourceClass(element, "value").forEach(sourceClass -> {
                        List<ExecutableElement> sourceMethods = getAllMethods((TypeElement)sourceClass.asElement())
                                .stream()
                                .filter(method -> method.getParameters().isEmpty() && !method.getReturnType().getKind().equals(TypeKind.VOID) && method.getSimpleName().toString().startsWith("get"))
                                .collect(Collectors.toList());
                        messager.printMessage(Diagnostic.Kind.NOTE, sourceMethods.toString());

                        MethodSpec.Builder copyMethodBuilder = MethodSpec.methodBuilder("copy")
                                .addJavadoc("Copy all fields from source to target\n")
                                .addJavadoc("@param target the target object\n")
                                .addJavadoc("@param source the source object\n")
                                .addJavadoc("@return the copied target object\n")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(TypeName.get(element.asType()))
                                .addParameter(TypeName.get(element.asType()), "target")
                                .addParameter(TypeName.get(sourceClass), "source");

                        targetMethods.stream().filter(targetMethod -> {
                            String targetMethodName = targetMethod.getSimpleName().toString().substring(3);
                            return sourceMethods.stream().anyMatch(sourceMethod -> sourceMethod.getSimpleName().toString().substring(3).equals(targetMethodName));
                        }).map(method -> method.getSimpleName().toString().substring(3)).forEach(methodName -> {
                            copyMethodBuilder.addStatement("target.set" + methodName + "(source.get" + methodName + "())");
                        });

                        copyMethodBuilder.addStatement("return target");

                        typeSpecBuilder.addMethod(copyMethodBuilder.build());
                    });
                });

        JavaFile javaFile = JavaFile.builder("cn.edu.xmu.javaee.core.utils", typeSpecBuilder.build()).build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return true;
    }

    private List<ExecutableElement> getAllMethods(TypeElement type) {
        return new ArrayList<>(ElementFilter.methodsIn(type.getEnclosedElements()));
    }

    private Optional<AnnotationMirror> getAnnotationMirror(TypeElement element, Class<?> clazz) {
        String clazzName = clazz.getName();
        for(AnnotationMirror m : element.getAnnotationMirrors()) {
            if(m.getAnnotationType().toString().equals(clazzName)) {
                return Optional.ofNullable(m);
            }
        }
        return Optional.empty();
    }

    private Optional<AnnotationValue> getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() ) {
            if(entry.getKey().getSimpleName().toString().equals(key)) {
                messager.printMessage(Diagnostic.Kind.NOTE, String.format("Entry: %s, value: %s", entry.getKey().getSimpleName().toString(), entry.getValue().toString()));
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    private List<DeclaredType> getSourceClass(TypeElement clazz, String key) {
        return getAnnotationMirror(clazz, CopyFrom.class)
                .flatMap(annotation -> getAnnotationValue(annotation, key))
                // ^ note that annotation value here corresponds to Class[],
                .map(annotation -> (List<AnnotationValue>)annotation.getValue())
                .map(fromClasses -> fromClasses.stream()
                        .map(fromClass -> (DeclaredType)fromClass.getValue())
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
}
