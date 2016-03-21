/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vortex.gui;

import clustering.BarCode;
import java.util.Arrays;

/**
 *
 * @author Nikolay
 */
public class UnityLenBarCode implements BarCode {

    private static final long serialVersionUID = 1L;
    private double[] profile;
    private String[] paramNames;
    private int sideVectorBeginIdx;

    @Override
    public double[] getRawValues() {
        return profile;
    }

    @Override
    public int getSideVectorBeginIdx() {
        return sideVectorBeginIdx;
    }

    public UnityLenBarCode(double[] vec, String[] paramNames, int sideVectorStartIdx) {
        profile = Arrays.copyOf(vec, vec.length);
        this.paramNames = paramNames;
        if (this.paramNames.length > vec.length) {
            this.paramNames = Arrays.copyOf(paramNames, vec.length);
        }
        this.sideVectorBeginIdx = sideVectorStartIdx;
        for (int i = 0; i < profile.length; i++) {
            if (profile[i] < -1.0 || profile[i] > 1.0) {
                profile[i] = Math.signum(profile[i]);
            }
        }
    }

    @Override
    public double[] getProfile() {
        return profile;
    }

    @Override
    public String[] getParameterNames() {
        return paramNames;
    }
}
