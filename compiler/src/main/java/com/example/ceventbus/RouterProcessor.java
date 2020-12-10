package com.example.ceventbus;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/*****************************************************************
 * * File: - RouterProcessor
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/12/8
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/8    1.0         create
 ******************************************************************/
@AutoService(Processor.class)

public class RouterProcessor extends AbstractProcessor {
    private Messager log;
    private Elements elements;
    private Types types;
    private Filer filer;
    private HashSet<ModuleBean> beans = new HashSet<>();
    /**
     * module name(such as app)
     */
    private String mModuleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        log = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
//            mModuleName = options.get(OPTION_MODULE_NAME);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(ModuleEvents.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (annotations == null || annotations.isEmpty()) {
            log.printMessage(Diagnostic.Kind.NOTE, "process set is empty");
            return false;
        }

        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(ModuleEvents.class);

        if (routeElements == null || routeElements.isEmpty()) {
            log.printMessage(Diagnostic.Kind.NOTE, "process roundEnvironment is empty");
            return false;
        }
        extractModuleInfo(routeElements);
        if (!beans.isEmpty()) {
            generateModuleFile(beans);
        }
        return true;
    }


    private void extractModuleInfo(Set<? extends Element> routeElements) {
        log.printMessage(Diagnostic.Kind.NOTE, "start to classify module info");
        for (Element ele : routeElements) {
            if (ele.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) ele;
                String fullClassName = typeElement.getQualifiedName().toString();
                log.printMessage(Diagnostic.Kind.NOTE, "fullClassName=" + fullClassName);
                String className = typeElement.getSimpleName().toString();
                log.printMessage(Diagnostic.Kind.NOTE, "className=" + className);
                ModuleEvents moduleEvents = ele.getAnnotation(ModuleEvents.class);
                String module = moduleEvents.module();
                log.printMessage(Diagnostic.Kind.NOTE, "module=" + module);
                ModuleBean moduleBean = new ModuleBean(fullClassName, className, module);
                List<? extends Element> elements = typeElement.getEnclosedElements();
                if (elements != null) {
                    List<EventBean> eventBeanList = new ArrayList<>();
                    for (Element e : elements) {
                        if (e.getKind() == ElementKind.FIELD) {
                            VariableElement variableElement = (VariableElement) e;
                            String event = variableElement.getSimpleName().toString();
                            log.printMessage(Diagnostic.Kind.NOTE, "event=" + event);
                            //可以用constant作为生成的方法名
                            String constant=variableElement.getConstantValue().toString();
                            log.printMessage(Diagnostic.Kind.NOTE, "constant=" + constant);
                            EventType type = e.getAnnotation(EventType.class);
                            TypeMirror value = null;
                            String aClass = null;
                            if (type != null) {
                                try {
                                    type.value();
                                } catch (MirroredTypeException mte) {
                                    value = mte.getTypeMirror();
                                }
                                aClass = value.toString();
                                log.printMessage(Diagnostic.Kind.NOTE, "value=" + value);
                            } else {
                                log.printMessage(Diagnostic.Kind.NOTE, "type null");
                                aClass = Object.class.getCanonicalName();
                            }
                            eventBeanList.add(new EventBean(constant, aClass));
                        }
                    }
                    moduleBean.eventList = eventBeanList;
                }

                beans.add(moduleBean);
            }
        }
        log.printMessage(Diagnostic.Kind.NOTE, "classify module info  -- finish -- group size is " + beans.size());
    }

    private void generateModuleFile(HashSet<ModuleBean> beans) {
        log.printMessage(Diagnostic.Kind.NOTE, "generateModuleFile bean " + beans.size());
        for (ModuleBean moduleBean : beans) {
            TypeSpec.Builder mainActivityBuilder = TypeSpec.interfaceBuilder(Cons.PREFIX + moduleBean.className)
                    .addModifiers(Modifier.PUBLIC);
            for (EventBean eventBean : moduleBean.eventList) {
                ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get("com.example.ceventbus", "BusMutableLiveData"), ClassName.bestGuess(eventBean.aClass));
                MethodSpec onCreate = MethodSpec.methodBuilder(eventBean.methodName)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(parameterizedTypeName)
                        .build();
                mainActivityBuilder.addMethod(onCreate);
            }
            mainActivityBuilder.addField(FieldSpec.builder(String.class, Cons.CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", moduleBean.fullClassName)
                    .build());
            mainActivityBuilder.addField(FieldSpec.builder(String.class, Cons.MODULE)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", moduleBean.module)
                    .build());
            TypeSpec mainActivity = mainActivityBuilder.build();
            JavaFile file = JavaFile.builder(Cons.FILE_MODULE, mainActivity).build();
            try {
                file.writeTo(filer);
            } catch (IOException e) {
                log.printMessage(Diagnostic.Kind.ERROR, "generate  file exception");
            }

        }
    }

}
