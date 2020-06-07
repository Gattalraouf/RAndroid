package Detctor.CSVReadingManager;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.*;
import com.intellij.util.NotNullFunction;
import com.intellij.util.Processor;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.picocontainer.PicoContainer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

class CSVPaprikaReadingManagerTest {



    @Test
    void getTargetC() {
        CSVPaprikaReadingManager manager=new CSVPaprikaReadingManager();
        assertEquals(manager.targetC,manager.getTargetC());
    }

    @Mock
    private Project project;
    @Mock
    private ArrayList<PsiFile> targetClassFile;
    private PsiJavaFile psijavafile;
    private ArrayList<PsiClass> classes;
    private PsiClass[] theclasses;
    @Mock
    private PsiClass targetC;
    @Mock
    private CSVPaprikaReadingManager manager;
    private GlobalSearchScope scope;
    private GlobalSearchScope globalscope;
    private FilenameIndex filenameindex;
    private PsiClass firstClass;
    private ProjectScope projectscope;
    private PsiFile file;

    private String filePath ="/Users/abir/Desktop/testSalat2020_5_6_1_15_HSS.csv";
    private String title= "Refactoring HSS code smell";
    private String codeSmell= "HSS";
    private int index= 3;
    private String[] target=new String[]{"1","8","109","onStartCommand#com.skanderjabouzi.salat.AdhanService","1"};

    private NotNullLazyKey ALL_SCOPE_KEY;
    private NotNullFunction function;
    private ProjectScopeBuilder builder;
    private ServiceManager service;
    private PicoContainer pico;
    private ProjectScopeBuilder p;
    private ComponentManager cmanager;
    private Class c= ProjectScopeBuilder.class;
    private Application app;
    private ApplicationManager appmanager;
    private Class classFile;
    private Collection<VirtualFile> files;
    private Processor<PsiFileSystemItem> processor;




    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    <T> void getTargetClass() {
//        project = mock(Project.class);
//        projectscope=mock(ProjectScope.class);
//        scope=mock(GlobalSearchScope.class) ;
//        targetC=mock(PsiClass.class);
//        manager=new CSVPaprikaReadingManager();
//        filenameindex=mock(FilenameIndex.class);
//        targetClassFile=mock(ArrayList.class);
//        psijavafile=mock(PsiJavaFile.class);
//        classes=mock(ArrayList.class);
//        theclasses=(PsiClass[]) classes.toArray();
//        firstClass=mock(PsiClass.class);
//        file=mock(PsiFile.class);
//        //ALL_SCOPE_KEY=mock(NotNullLazyKey.class);
//        function=mock(NotNullFunction.class);
//        builder=mock(ProjectScopeBuilder.class);
//        //service=mock(ServiceManager.class);
//        pico=mock(PicoContainer.class);
//        p=mock(ProjectScopeBuilder.class);
//        cmanager=mock(ComponentManager.class);
//        app=mock(Application.class);
//        appmanager=mock(ApplicationManager.class);
//        files=mock(Collection.class);
//        processor=mock(Processor.class);
//
//
//
//        ALL_SCOPE_KEY=NotNullLazyKey.create(
//                "ALL_SCOPE_KEY",
//                project -> ProjectScopeBuilder.getInstance((Project) project).buildAllScope());
//
//
//        when(function.fun(project)).thenReturn(scope);
//        when(project.getPicoContainer()).thenReturn(pico);
//        when(project.getComponent(c)).thenReturn(builder);
//        when((ProjectScopeBuilder)pico.getComponentInstance(c.getName())).thenReturn(builder);
//
//        when(ServiceManager.getService(project,c)).thenReturn(builder);
//        when(builder.getInstance(project)).thenReturn(builder);
//        when(project.getUserData(ALL_SCOPE_KEY)).thenReturn(scope);
//        if(project instanceof UserDataHolderEx){
//            when(((UserDataHolderEx)project).putUserDataIfAbsent(ALL_SCOPE_KEY, scope)).thenReturn(scope);
//        }
//        when(ALL_SCOPE_KEY.getValue(project)).thenReturn(scope);
//        when(projectscope.getAllScope(project)).thenReturn(scope);
//        when(scope.allScope(project)).thenReturn(scope);
//         // when().thenReturn(app);
//
//
//      //  when(ServiceManager.getService(classFile)).thenReturn(files);
//
//        //when(filenameindex.processFilesByName("AdhanService.java", false, processor, scope, project, null)).thenReturn(true);
//        when(filenameindex.getFilesByName(project,"AdhanService.java",scope)).thenReturn((PsiFile[]) targetClassFile.toArray());
//
//
//
//        when(psijavafile.getClasses()).thenReturn(theclasses);
//        when(firstClass.findInnerClassByName("com.skanderjabouzi.salat.AdhanService",false)).thenReturn(targetC);
//        //when(manager.getTargetC()).thenReturn(targetC);
//
//        manager.getTargetClass(target,filePath,title,project,codeSmell,index);
//
//        assertNotNull(manager.getTargetC());
//    }
}