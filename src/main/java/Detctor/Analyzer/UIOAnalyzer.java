package Detctor.Analyzer;

public class UIOAnalyzer extends PaprikaAnalyzer {

    public UIOAnalyzer(String filepath) {
        super(filepath);
        this.codeSmell="UIO";
    }

    public UIOAnalyzer(){
        super();
        this.codeSmell="UIO";
    }

}
