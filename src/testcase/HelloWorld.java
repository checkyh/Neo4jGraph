package testcase;

import java.util.Deque;
import java.util.LinkedList;

public class HelloWorld {
	
	private static boolean t = true;
	private static boolean s = true;
	private static boolean f = false;
	
	private static int ONE = 1;
	private static int TWO = 2;
	private static int THREE = 3;
	private static int FOUR = 4;
	
	private Deque<Integer> list = new LinkedList<>();
	
	public void push(int d)	{
		list.addLast(d);
	}
	
	public int pop() {
		return list.removeLast();
	}

	public static void main(String[] args) {
		HelloWorld hw = new HelloWorld();
		if (t == true && s == true && f == false) {
			hw.push(1);
			hw.push(2);
			hw.push(1);
			hw.push(1);
			hw.push(2);
			hw.pop();
		}
		if (ONE == 1 && TWO == 2 && FOUR - THREE == 1) {
			System.out.println("Hello world.");
		}
	}
}