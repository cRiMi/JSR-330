/**
 * @author julien
 */

package com.serli.jderay.jsr330.exceptions;


public class AmbiguousImplementationsException extends Exception {

    public AmbiguousImplementationsException(String impl1, String impl2, String interf) {
        super("At least " + impl1 + " and " + impl2 + " implement the interface " + interf);
    }

}
