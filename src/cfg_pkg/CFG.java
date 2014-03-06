package cfg_pkg;

import java.util.*;

import org.apache.bcel.classfile.*;

public class CFG {

	Set<Node> nodes = new HashSet<Node>();
	Map<Node, Set<Node>> edges = new HashMap<Node, Set<Node>>();

	static class Node {
		int position;
		Method method;
		JavaClass clazz;

		Node(int p, Method m, JavaClass c) {
			position = p;
			method = m;
			clazz = c;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Node))
				return false;
			Node n = (Node) o;
			return (position == n.position) && method.equals(n.method)
					&& clazz.equals(n.clazz);
		}

		public int hashCode() {
			return position + method.hashCode() + clazz.hashCode();
		}

		public String toString() {
			return clazz.getClassName() + "." + method.getName()
					+ method.getSignature() + ": " + position;
		}
	}

	public void addNode(int p, Method m, JavaClass c) {
		Node newNode = new Node(p, m, c);

		if (nodes.contains(newNode))
			return;

		nodes.add(newNode);
		edges.put(newNode, new HashSet<Node>());
	}

	public void addEdge(int p1, Method m1, JavaClass c1, int p2, Method m2,
			JavaClass c2) {
		Node node1 = new Node(p1, m1, c1);
		Node node2 = new Node(p2, m2, c2);
		if (!nodes.contains(node1))
			addNode(p1, m1, c1);
		if (!nodes.contains(node2))
			addNode(p2, m2, c2);
		Set<Node> s = edges.get(node1);
		s.add(node2);
		edges.put(node1, s);
	}

	public void display() {
		System.out.println("Nodes");
		for (Node n: nodes){
			System.out.println(n.toString());
			for (Node s :edges.get(n)){
				System.out.println("\t" + s.toString());
			}
		}	
	}
}
