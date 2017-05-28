package libai.fuzzy;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kronenthaler on 30/04/2017.
 */
public class Consequent implements XMLSerializer, Iterable<Clause> {
	private List<Clause> clauses = new ArrayList<>();

	public Consequent(Node xmlNode) {
		load(xmlNode);
	}

	public Consequent(Clause... clauses) {
		this.clauses = Arrays.asList(clauses);
	}

	@Override
	public String toXMLString(String indent) {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%s<Consequent>\n", indent));

		for (Clause var : clauses) {
			str.append(String.format("%s\n", var.toXMLString(indent + "\t")));
		}

		str.append(String.format("%s</Consequent>", indent));
		return str.toString();
	}

	@Override
	public void load(Node xmlNode) {
		NodeList children = ((Element) xmlNode).getElementsByTagName("Clause");
		for (int i = 0; i < children.getLength(); i++) {
			clauses.add(new Clause(children.item(i)));
		}
	}

	@Override
	public Iterator<Clause> iterator() {
		return clauses.iterator();
	}
}
