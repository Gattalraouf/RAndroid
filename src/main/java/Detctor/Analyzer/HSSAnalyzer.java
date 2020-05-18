package Detctor.Analyzer;

public class HSSAnalyzer extends PaprikaAnalyzer{
    public HSSAnalyzer(String filepath) {
        super(filepath);
        this.codeSmell="HSS";
        this.index=3;
    }

    public String getTargetMethodName(String[] target){
        String[] targetDetails =target[3].split("#", 0);
        return targetDetails[0];
    }
}
