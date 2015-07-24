package testcase;

import java.util.List;

/**
 * @lecture 2-Asymptotics
 * @problem 1.2.5
 * @author William
 */
public class SwapArrayElements {
	
	private static int N;
	private static int[] a;
	private static int sp;  // split point, range from 1 to N-1, split a[sp-1] and a[sp]
	
	private static void init() {
		N = 9;
		a = new int[N];
		for (int i = 0; i < N; i++) {
			a[i] = i;
		}
		sp = 6;
	}

	// Brute force solution1, space sensitive
	// Time: O(n^2), Space: O(1)
	private static int[] brute1() {
		
		int N1 = a.length;
		int[] b1 = new int[N1];
		for (int i1 = 0; i1 < N1; i1++) {
			b1[i1] = a[i1];
		}
		int[] b = b1;

		// bubble all elements in a[sp..N) forward
		for (int i = sp; i < N; i++) {
			int temp = b[i];
			// bubble a[i] forward sp times
			for (int j = 0; j < sp; j++) {
				b[i-j] = b[i-j-1];
			}
			b[i-sp] = temp;
		}
		return b;
	}
	
	// Brute force solution2, time sensitive
	// Time: O(n), Space: O(n)
	private static int[] brute2() {
		int[] b = new int[N];
		int lengthRight = N - sp;
		for (int i = 0; i < lengthRight; i++) {
			b[i] = a[sp+i];
		}
		for (int i = 0; i < sp; i++) {
			b[lengthRight + i] = a[i];
		}
		return b;
	}
	
	
	private static int[] good() {
		int N1 = a.length;
		int[] b1 = new int[N1];
		for (int i = 0; i < N1; i++) {
			b1[i] = a[i];
		}
		int[] b = b1;
		
		// first pass: all reverse
		Arrai.reverse(b, 0, N);
		
		// second pass: Arrai.reverse both subarrays
		sp = N - sp;
		Arrai.reverse(b, 0, sp);
		Arrai.reverse(b, sp, N);
	
		return b;
	}
	

	public static void main(String[] args) {
		init();
		int[] solution;
		solution = brute1();
		System.out.println("Brute force 1 solution:");
		Arrai.print(solution);
		solution = brute2();
		System.out.println("Brute force 2 solution:");
		Arrai.print(solution);
		solution = good();
		System.out.println("Good algorithm solution:");
		Arrai.print(solution);
	}

}
