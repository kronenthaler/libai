/*
 * MIT License
 *
 * Copyright (c) 2009-2016 Ignacio Calderon <https://github.com/kronenthaler>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
