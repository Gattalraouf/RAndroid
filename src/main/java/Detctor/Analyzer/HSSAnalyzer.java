package Detctor.Analyzer;

public class HSSAnalyzer extends PaprikaAnalyzer{

    public HSSAnalyzer(String filepath) {
        super(filepath);
        this.codeSmell="HSS";
        this.index=3;
    }

    public HSSAnalyzer(){
        super();
        this.codeSmell="HSS";
        this.index=3;
    }
}
