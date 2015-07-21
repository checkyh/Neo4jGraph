package testcase;

import java.util.Iterator;

public class Arrai {
	
	public static void swap(int[] a, int i, int j) {
		int temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
	
	public static void reverse(int[] a, int begin, int end) {
		int i = begin;
		int j = end - 1;
		while (i < j) {
			swap(a, i, j);
			i++;
			j--;
		}
	}
	
	public static void print(Object[] a) {
		print(a, " ");
	}
	
	public static void print(Object[] a, String seperator) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]);
			if (i < a.length - 1) {
				System.out.print(seperator);
			} else {
				System.out.println();
			}
		}
	}
	
	public static void print(int[] a) {
		print(a, " ");
	}
	
	public static void print(int[] a, String seperator) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]);
			if (i < a.length - 1) {
				System.out.print(seperator);
			} else {
				System.out.println();
			}
		}
	}
	
	public static void print(Iterable<?> a) {
		print(a, " ");
	}
	
	public static void print(Iterable<?> a, String seperator) {
		Iterator<?> it = a.iterator();
		while (true) {
			Object o = it.next();
			System.out.print(o);
			if (it.hasNext()) {
				System.out.print(seperator);
			} else {
				System.out.println();
				break;
			}
		}
	}

}
