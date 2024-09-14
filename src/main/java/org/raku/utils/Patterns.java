package org.raku.utils;

public class Patterns {
    public final static String MODULE_PATTERN      = "^[A-Za-z0-9-_']+(::[A-Za-z0-9-_']+)*$";
    public final static String SCRIPT_PATTERN      = "^[A-Za-z0-9-_']+(.(p6|pl6|rakuidea))?$";
    public final static String ENTRY_POINT_PATTERN = "^[A-Za-z0-9-_'.]+$";
    public final static String DOC_FILE_PATTERN    = "^[A-Z-a-z0-9-_'.]+(.(rakudoc|pod6))?$";
    public final static String TEST_PATTERN        = "^[.A-Za-z0-9-_']+$";
    public final static String CRO_TEMPLATE_PATTERN= "^[.A-Za-z0-9-_']+$";
}
