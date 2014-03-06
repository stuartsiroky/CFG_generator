package cfg_pkg;

public class D {
	public void main(String[] a) {
		foo(a);
		bar(a);
	}

	static void bar(String[] a) {}
	static void foo(String[] a) {
		if(a ==  null) return;
		bar(a);
	}
	
}
