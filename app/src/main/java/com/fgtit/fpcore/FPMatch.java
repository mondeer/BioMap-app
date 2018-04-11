package com.fgtit.fpcore;

/**
 * Developed by root on 11/8/17.
 */

public class FPMatch {
    private static FPMatch mMatch=null;

    public static FPMatch getInstance(){
        if(mMatch==null){
            mMatch=new FPMatch();
        }
        return mMatch;
    }

    public native int InitMatch();
    public native int MatchTemplate( byte[] piFeatureA, byte[] piFeatureB);

    static {
        System.loadLibrary("fgtitalg");
        System.loadLibrary("fpcore");
    }
}
