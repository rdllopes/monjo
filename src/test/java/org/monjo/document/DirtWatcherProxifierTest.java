package org.monjo.document;

import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.util.Set;

import org.monjo.example.SimplePOJO;

public class DirtWatcherProxifierTest {
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InterruptedException {
		SimplePOJO simplePOJO = new SimplePOJO();
		pause();
		long initial = System.currentTimeMillis();
		final int maxint = 100000000;
//		for (int i = 0; i < maxint; i++)
//			simplePOJO.setAnIntegerField(i);
		System.out.println(System.currentTimeMillis() - initial);
		
		
		DirtWatcherProxifier proxifier = new DirtWatcherProxifier();
		Object object = proxifier.proxify(simplePOJO);
		SimplePOJO simplePOJO2 = (SimplePOJO) object;
		initial = System.currentTimeMillis();
		for (int i = 0; i < maxint; i++)
			simplePOJO2.setAnIntegerField(i);
		System.out.println(System.currentTimeMillis() - initial);
		simplePOJO2.setaDoubleField(null);
		System.out.println(simplePOJO2.getaDoubleField());
		InternalMonjoObject internalMonjoObject = (InternalMonjoObject) object;
		Set<String> set = internalMonjoObject.dirtFields();
		for (String string : set) {
			System.out.println(string);
		}
		pause();
	}

	private static void pause() {
		Scanner sc = new Scanner(System.in);
		while (!sc.nextLine().equals(""));
	}
}

