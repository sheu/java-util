package com.cedarsoftware.util;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.lang.Math.E;
import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author John DeRegnaucourt
 * @author sapradhan8
 * <br>
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at <br>
 * <br>
 *         http://www.apache.org/licenses/LICENSE-2.0 <br>
 * <br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 */
public class TestDeepEquals
{
    @Test
    public void testSameObjectEquals()
    {
        Date date1 = new Date();
        Date date2 = date1;
        assertTrue(DeepEquals.deepEquals(date1, date2));
    }

	@Test
	public void testEqualsWithNull()
    {
		Date date1 = new Date();
		assertFalse(DeepEquals.deepEquals(null, date1));
		assertFalse(DeepEquals.deepEquals(date1, null));
	}

	@Test
	public void testDifferentClasses()
    {
		assertFalse(DeepEquals.deepEquals(new Date(), "test"));
	}

    @Test
    public void testPOJOequals()
    {
        Class1 x = new Class1(true, tan(PI / 4), 1);
        Class1 y = new Class1(true, 1.0, 1);
        assertTrue(DeepEquals.deepEquals(x, y));
        assertFalse(DeepEquals.deepEquals(x, new Class1()));

        Class2 a = new Class2((float) atan(1.0), "hello", (short) 2,
                new Class1(false, sin(0.75), 5));
        Class2 b = new Class2((float) PI / 4, "hello", (short) 2,
                new Class1(false, 2 * cos(0.75 / 2) * sin(0.75 / 2), 5)
        );

        assertTrue(DeepEquals.deepEquals(a, b));
        assertFalse(DeepEquals.deepEquals(a, new Class2()));
    }

	@Test
	public void testPrimitiveArrays()
    {
		int array1[] = { 2, 4, 5, 6, 3, 1, 3, 3, 5, 22 };
		int array2[] = { 2, 4, 5, 6, 3, 1, 3, 3, 5, 22 };

		assertTrue(DeepEquals.deepEquals(array1, array2));

		int array3[] = { 3, 4, 7 };

		assertFalse(DeepEquals.deepEquals(array1, array3));

		float array4[] = { 3.4f, 5.5f };
		assertFalse(DeepEquals.deepEquals(array1, array4));
	}

	@Test
	public void testOrderedCollection()
    {
        List<String> a = Arrays.asList("one", "two", "three", "four", "five");
		List<String> b = new LinkedList<>(a);

		assertTrue(DeepEquals.deepEquals(a, b));

		List<Integer> c = Arrays.asList(1, 2, 3, 4, 5);

		assertFalse(DeepEquals.deepEquals(a, c));

		List<Integer> d = Arrays.asList(4, 6);

		assertFalse(DeepEquals.deepEquals(c, d));

		List<Class1> x1 = Arrays.asList(new Class1(true, log(pow(E, 2)), 6), new Class1(true, tan(PI / 4), 1));
		List<Class1> x2 = Arrays.asList(new Class1(true, 2, 6), new Class1(true, 1, 1));
		assertTrue(DeepEquals.deepEquals(x1, x2));

	}

	@Test
	public void testUnorderedCollection()
    {
        Set<String> a = new HashSet<>(Arrays.asList("one", "two", "three", "four", "five"));
		Set<String> b = new HashSet<>(Arrays.asList("three", "five", "one", "four", "two"));
		assertTrue(DeepEquals.deepEquals(a, b));

		Set<Integer> c = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
		assertFalse(DeepEquals.deepEquals(a, c));

		Set<Integer> d = new HashSet<>(Arrays.asList(4, 2, 6));
		assertFalse(DeepEquals.deepEquals(c, d));

		Set<Class1> x1 = new HashSet<>(Arrays.asList(new Class1(true, log(pow(E, 2)), 6), new Class1(true, tan(PI / 4), 1)));
		Set<Class1> x2 = new HashSet<>(Arrays.asList(new Class1(true, 1, 1), new Class1(true, 2, 6)));
		assertTrue(DeepEquals.deepEquals(x1, x2));
	}

    @Test
    public void testEquivalentMaps()
    {
        Map map1 = new LinkedHashMap();
        fillMap(map1);
        Map map2 = new HashMap();
        fillMap(map2);
        assertTrue(DeepEquals.deepEquals(map1, map2));
        assertEquals(DeepEquals.deepHashCode(map1), DeepEquals.deepHashCode(map2));

        map1 = new TreeMap();
        fillMap(map1);
        map2 = new TreeMap();
        map2 = Collections.synchronizedSortedMap((SortedMap) map2);
        fillMap(map2);
        assertTrue(DeepEquals.deepEquals(map1, map2));
        assertEquals(DeepEquals.deepHashCode(map1), DeepEquals.deepHashCode(map2));
    }

    @Test
    public void testInequivalentMaps()
    {
        Map map1 = new TreeMap();
        fillMap(map1);
        Map map2 = new HashMap();
        fillMap(map2);
        // Sorted versus non-sorted Map
        assertFalse(DeepEquals.deepEquals(map1, map2));

        // Hashcodes are equals because the Maps have same elements
        assertEquals(DeepEquals.deepHashCode(map1), DeepEquals.deepHashCode(map2));

        map2 = new TreeMap();
        fillMap(map2);
        map2.remove("kilo");
        assertFalse(DeepEquals.deepEquals(map1, map2));

        // Hashcodes are different because contents of maps are different
        assertNotEquals(DeepEquals.deepHashCode(map1), DeepEquals.deepHashCode(map2));

        // Inequality because ConcurrentSkipListMap is a SortedMap
        map1 = new HashMap();
        fillMap(map1);
        map2 = new ConcurrentSkipListMap();
        fillMap(map2);
        assertFalse(DeepEquals.deepEquals(map1, map2));

        map1 = new TreeMap();
        fillMap(map1);
        map2 = new ConcurrentSkipListMap();
        fillMap(map2);
        assertTrue(DeepEquals.deepEquals(map1, map2));
        map2.remove("papa");
        assertFalse(DeepEquals.deepEquals(map1, map2));
    }

    @Test
    public void testEquivalentCollections()
    {
        // ordered Collection
        Collection col1 = new ArrayList();
        fillCollection(col1);
        Collection col2 = new LinkedList();
        fillCollection(col2);
        assertTrue(DeepEquals.deepEquals(col1, col2));
        assertEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));

        // unordered Collections (Set)
        col1 = new LinkedHashSet();
        fillCollection(col1);
        col2 = new HashSet();
        fillCollection(col2);
        assertTrue(DeepEquals.deepEquals(col1, col2));
        assertEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));

        col1 = new TreeSet();
        fillCollection(col1);
        col2 = new TreeSet();
        Collections.synchronizedSortedSet((SortedSet) col2);
        fillCollection(col2);
        assertTrue(DeepEquals.deepEquals(col1, col2));
        assertEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));
    }

    @Test
    public void testInequivalentCollections()
    {
        Collection col1 = new TreeSet();
        fillCollection(col1);
        Collection col2 = new HashSet();
        fillCollection(col2);
        assertFalse(DeepEquals.deepEquals(col1, col2));
        assertEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));

        col2 = new TreeSet();
        fillCollection(col2);
        col2.remove("lima");
        assertFalse(DeepEquals.deepEquals(col1, col2));
        assertNotEquals(DeepEquals.deepHashCode(col1), DeepEquals.deepHashCode(col2));

        assertFalse(DeepEquals.deepEquals(new HashMap(), new ArrayList()));
        assertFalse(DeepEquals.deepEquals(new ArrayList(), new HashMap()));
    }

    @Test
    public void testArray()
    {
        Object[] a1 = new Object[] {"alpha", "bravo", "charlie", "delta"};
        Object[] a2 = new Object[] {"alpha", "bravo", "charlie", "delta"};

        assertTrue(DeepEquals.deepEquals(a1, a2));
        assertEquals(DeepEquals.deepHashCode(a1), DeepEquals.deepHashCode(a2));
        a2[3] = "echo";
        assertFalse(DeepEquals.deepEquals(a1, a2));
        assertNotEquals(DeepEquals.deepHashCode(a1), DeepEquals.deepHashCode(a2));
    }

	@Test
	public void testHasCustomMethod()
    {
		assertFalse(DeepEquals.hasCustomEquals(EmptyClass.class));
		assertFalse(DeepEquals.hasCustomHashCode(Class1.class));

		assertTrue(DeepEquals.hasCustomEquals(EmptyClassWithEquals.class));
		assertTrue(DeepEquals.hasCustomHashCode(EmptyClassWithEquals.class));
	}

    @Test
    public void testSymmetry()
    {
        boolean one = DeepEquals.deepEquals(new ArrayList<String>(), new EmptyClass());
        boolean two = DeepEquals.deepEquals(new EmptyClass(), new ArrayList<String>());
        assert one == two;
    }

	static class EmptyClass
    {

	}

	static class EmptyClassWithEquals
    {
		public boolean equals(Object obj) {
			return obj instanceof EmptyClassWithEquals;
		}

		public int hashCode() {
			return 0;
		}
	}

	static class Class1
    {
		private boolean b;
		private double d;
		int i;

		public Class1() { }

		public Class1(boolean b, double d, int i)
        {
			super();
			this.b = b;
			this.d = d;
			this.i = i;
		}

	}

	static class Class2
    {
		private Float f;
		String s;
		short ss;
		Class1 c;

		public Class2(float f, String s, short ss, Class1 c)
        {
			super();
			this.f = f;
			this.s = s;
			this.ss = ss;
			this.c = c;
		}

		public Class2() { }
	}

    private void fillMap(Map map)
    {
        map.put("zulu", 26);
        map.put("alpha", 1);
        map.put("bravo", 2);
        map.put("charlie", 3);
        map.put("delta", 4);
        map.put("echo", 5);
        map.put("foxtrot", 6);
        map.put("golf", 7);
        map.put("hotel", 8);
        map.put("india", 9);
        map.put("juliet", 10);
        map.put("kilo", 11);
        map.put("lima", 12);
        map.put("mike", 13);
        map.put("november", 14);
        map.put("oscar", 15);
        map.put("papa", 16);
        map.put("quebec", 17);
        map.put("romeo", 18);
        map.put("sierra", 19);
        map.put("tango", 20);
        map.put("uniform", 21);
        map.put("victor", 22);
        map.put("whiskey", 23);
        map.put("xray", 24);
        map.put("yankee", 25);
    }

    private void fillCollection(Collection col)
    {
        col.add("zulu");
        col.add("alpha");
        col.add("bravo");
        col.add("charlie");
        col.add("delta");
        col.add("echo");
        col.add("foxtrot");
        col.add("golf");
        col.add("hotel");
        col.add("india");
        col.add("juliet");
        col.add("kilo");
        col.add("lima");
        col.add("mike");
        col.add("november");
        col.add("oscar");
        col.add("papa");
        col.add("quebec");
        col.add("romeo");
        col.add("sierra");
        col.add("tango");
        col.add("uniform");
        col.add("victor");
        col.add("whiskey");
        col.add("xray");
        col.add("yankee");
    }
}
