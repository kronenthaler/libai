/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libai.ants;

/**
 * This class belong to the core classes of the Ant Framework.
 *
 * This class extends the system native Exception class. This exception will be
 * throw if some particular error condition occurs within the framework.
 *
 * @version 1
 * @author Enrique Areyan, enrique3 at gmail.com
 *
 */
public class AntFrameworkException extends Exception {
    /**
     * Constructor. Calls super() method, i.e.: the constructor of the Exception
     * class.
     *
     * @param msg String with the information of the error
     */
    public AntFrameworkException(String msg) {
        super(msg);
    }
}
