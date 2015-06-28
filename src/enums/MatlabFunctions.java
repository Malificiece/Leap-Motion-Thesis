package enums;

public enum MatlabFunctions {
    VERTCAT("vertcat"),
    ANOVA1("anova1"),
    TRANSPOSE("transpose"),
    MATRIX("matrix");

    private final String functionName;
    
    private MatlabFunctions(String functionName) {
        this.functionName = functionName;
    }
    
    public String getFunction(String...args) {
        if(args != null && args.length > 0) {
            String function = functionName + "(" + args[0];
            for(int i = 1; i < args.length; i++) {
                function += ", " + args[i];
            }
            function += ")";
            return function;
        }
        return null;
    }
}
