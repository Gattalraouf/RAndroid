package Corrector.Recommanding;

import Detctor.Analyzer.PaprikaAnalyzer;
import Detctor.Analyzer.UIOAnalyzer;
import Detctor.CSVReadingManager.CSVPaprikaReadingManager;
import Detctor.CSVReadingManager.CSVReadingManager;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyKey;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.util.NotNullFunction;
import com.intellij.util.Processor;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class UIORefactoringUtilityTest {

    @Mock
    private Project project;
    private UIOAnalyzer analyzer;
    private String title="Refactoring UIO code smell";
    private String filepath="/Users/abir/Desktop/testSalat2020_5_6_1_15_UIO.csv";
    private ArrayList<String> t;
    private String[] target;
    private PsiClass targetClass;
    private CSVPaprikaReadingManager manager;

    private GlobalSearchScope scope;
    private GlobalSearchScope globalscope;
    private FilenameIndex filenameindex;
    private PsiClass firstClass;
    private ProjectScope projectscope;
    private PsiFile file;
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
//
//    @Test
//    void onRefactor() {
//       // target=(String[]) t.toArray();
//
//        project=mock(Project.class);
//        analyzer=mock(UIOAnalyzer.class);
//        targetClass=mock(PsiClass.class);
//        manager=mock(CSVPaprikaReadingManager.class);
//        UIORefactoringUtility corrector=new UIORefactoringUtility();
//
//
//        projectscope=mock(ProjectScope.class);
//        scope=mock(GlobalSearchScope.class) ;
//        manager=new CSVPaprikaReadingManager();
//        filenameindex=mock(FilenameIndex.class);
//        firstClass=mock(PsiClass.class);
//        file=mock(PsiFile.class);
//        ALL_SCOPE_KEY=mock(NotNullLazyKey.class);
//        function=mock(NotNullFunction.class);
//        builder=mock(ProjectScopeBuilder.class);
//        pico=mock(PicoContainer.class);
//        p=mock(ProjectScopeBuilder.class);
//        cmanager=mock(ComponentManager.class);
//        app=mock(Application.class);
//        appmanager=mock(ApplicationManager.class);
//        files=mock(Collection.class);
//        processor=mock(Processor.class);
//
//
//        when((PaprikaAnalyzer)analyzer.getTargetClass(target,filepath, title, project)).thenReturn(analyzer);
//        when(analyzer.getTargetC()).thenReturn(targetClass);
//
//        when(function.fun(project)).thenReturn(scope);
//        System.out.println(scope);
//        when(project.getPicoContainer()).thenReturn(pico);
//        when(project.getComponent(c)).thenReturn(builder);
//        when((ProjectScopeBuilder)pico.getComponentInstance(c.getName())).thenReturn(builder);
//
//        when(ServiceManager.getService(project,c)).thenReturn(builder);
//        when(builder.getInstance(project)).thenReturn(builder);
//        when(project.getUserData(ALL_SCOPE_KEY)).thenReturn(scope);
//
//
//        corrector.onRefactor(filepath,title,project);
//        assertTrue(corrector.refactored);
//    }
}