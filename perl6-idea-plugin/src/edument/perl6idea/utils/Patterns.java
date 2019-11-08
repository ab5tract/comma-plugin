package edument.perl6idea.utils;

public class Patterns {
    public final static String MODULE_PATTERN      = "^[A-Za-z0-9-_']+(::[A-Za-z0-9-_']+)*$";
    public final static String SCRIPT_PATTERN      = "^[A-Za-z0-9-_']+(.(p6|pl6))?$";
    public final static String ENTRY_POINT_PATTERN = "^[A-Za-z0-9-_'.]+$";
    public final static String TEST_PATTERN        = "^[.A-Za-z0-9-_']+$";
    public final static String CRO_TEMPLATE_PATTERN= "^[.A-Za-z0-9-_']+$";
}
