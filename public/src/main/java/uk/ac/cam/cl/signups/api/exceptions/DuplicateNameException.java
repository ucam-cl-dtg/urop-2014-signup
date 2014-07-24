/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.signups.api.exceptions;

/**
 * @author ird28
 *
 */
public class DuplicateNameException extends Exception {

    private static final long serialVersionUID = 3993025394779146635L; // generated
    
    public DuplicateNameException(String name) {
        super(name);
    }

}
