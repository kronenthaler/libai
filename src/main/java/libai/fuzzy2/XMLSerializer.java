package libai.fuzzy2;

import org.w3c.dom.Node;

/**
 * Created by kronenthaler on 23/04/2017.
 */
public interface XMLSerializer {
	/**
	 * Returns the XML representation of this Fuzzy set according with the FML schema definition.
	 *
	 * @return XML representation of this Fuzzy Set.
	 **/
	public String toXMLString(String indent);

	/**
	 * Initializes the instance with the xml node information.
	 *
	 * @param xmlNode XML node with the information to load the current object with.
	 **/
	public void load(Node xmlNode) throws Exception;
}
