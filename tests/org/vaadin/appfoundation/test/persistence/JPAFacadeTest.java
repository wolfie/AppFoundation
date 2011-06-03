package org.vaadin.appfoundation.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.vaadin.appfoundation.persistence.facade.JPAFacade;

public class JPAFacadeTest {

    private JPAFacade facade = null;

    @Before
    public void setUp() {
        facade = new JPAFacade("default");
    }

    @After
    public void tearDown() {
        facade.kill();
        facade = null;
    }

    @Test
    public void store() {
        MockPojo pojo = new MockPojo();

        assertNull(pojo.getId());
        facade.store(pojo);
        assertNotNull(pojo.getId());
        assertEquals(Long.valueOf(1L), pojo.getConsistencyVersion());
        pojo.setFoo("foobar");
        facade.store(pojo);
        assertEquals(Long.valueOf(2L), pojo.getConsistencyVersion());
        assertEquals("foobar", facade.find(MockPojo.class, pojo.getId())
                .getFoo());
    }

    @Test
    public void find() {
        MockPojo pojo1 = new MockPojo();
        pojo1.setFoo(UUID.randomUUID().toString());

        facade.store(pojo1);

        MockPojo pojo2 = facade.find(MockPojo.class, pojo1.getId());
        assertEquals(pojo1.getFoo(), pojo2.getFoo());
    }

    @Test
    public void findQuery() {
        MockPojo pojo1 = new MockPojo();
        pojo1.setFoo(UUID.randomUUID().toString());

        facade.store(pojo1);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("foo", pojo1.getFoo());
        MockPojo pojo2 = facade.find(
                "SELECT p FROM MockPojo p WHERE p.foo = :foo", parameters);
        assertEquals(pojo1.getFoo(), pojo2.getFoo());
        assertEquals(pojo1.getId(), pojo2.getId());
    }

    @Test
    public void findNoResult() {
        MockPojo pojo1 = new MockPojo();
        pojo1.setFoo(UUID.randomUUID().toString());

        facade.store(pojo1);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("foo", "bar");
        MockPojo pojo2 = facade.find(
                "SELECT p FROM MockPojo p WHERE p.foo = :foo", parameters);
        assertNull(pojo2);
    }

    @Test
    public void count() {
        for (int i = 0; i < 7; i++) {
            MockPojo pojo = new MockPojo();
            pojo.setFoo(UUID.randomUUID().toString());
            facade.store(pojo);
        }

        assertEquals(Long.valueOf(7L), facade.count(MockPojo.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void countGetException() {
        facade.count(null);
    }

    @Test
    public void list() {
        List<String> uuids = new ArrayList<String>();
        for (int i = 0; i < 7; i++) {
            MockPojo pojo = new MockPojo();
            pojo.setFoo(UUID.randomUUID().toString());
            uuids.add(pojo.getFoo());
            facade.store(pojo);
        }

        List<MockPojo> pojos = facade.list(MockPojo.class);

        assertEquals(7, pojos.size());

        for (MockPojo pojo : pojos) {
            assertTrue(uuids.contains(pojo.getFoo()));
        }
    }

    @Test
    public void listWithLimit() {
        for (int i = 0; i < 17; i++) {
            MockPojo pojo = new MockPojo();
            pojo.setFoo(Integer.toString(i));
            facade.store(pojo);
        }

        List<MockPojo> pojos = facade.list(MockPojo.class, 2, 7);

        assertEquals(7, pojos.size());

        List<String> numbers = new ArrayList<String>();
        numbers.add("2");
        numbers.add("3");
        numbers.add("4");
        numbers.add("5");
        numbers.add("6");
        numbers.add("7");
        numbers.add("8");

        for (MockPojo pojo : pojos) {
            assertTrue(numbers.contains(pojo.getFoo()));
        }
    }

    @Test
    public void listQuery() {
        List<String> uuids = new ArrayList<String>();
        for (int i = 0; i < 14; i++) {
            MockPojo pojo = new MockPojo();
            String prefix = i % 2 == 0 ? "foo" : "bar";
            pojo.setFoo(prefix + UUID.randomUUID().toString());
            if (i % 2 == 0) {
                uuids.add(pojo.getFoo());
            }
            facade.store(pojo);
        }

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("foo", "foo%");
        List<MockPojo> pojos = facade.list(
                "SELECT p FROM MockPojo p WHERE p.foo LIKE :foo", parameters);

        assertEquals(7, pojos.size());

        for (MockPojo pojo : pojos) {
            assertTrue(uuids.contains(pojo.getFoo()));
        }
    }

    @Test
    public void listQueryWithLimit() {
        List<String> uuids = new ArrayList<String>();
        for (int i = 0; i < 14; i++) {
            MockPojo pojo = new MockPojo();
            String prefix = i % 2 == 0 ? "foo" : "bar";
            pojo.setFoo(prefix + UUID.randomUUID().toString());
            if (i % 2 == 0) {
                uuids.add(pojo.getFoo());
            }
            facade.store(pojo);
        }

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("foo", "foo%");
        List<MockPojo> pojos = facade.list(
                "SELECT p FROM MockPojo p WHERE p.foo LIKE :foo", parameters,
                4, 3);

        assertEquals(3, pojos.size());

        for (MockPojo pojo : pojos) {
            assertTrue(uuids.contains(pojo.getFoo()));
        }

        assertEquals(uuids.get(4), pojos.get(0).getFoo());
        assertEquals(uuids.get(5), pojos.get(1).getFoo());
        assertEquals(uuids.get(6), pojos.get(2).getFoo());
    }

    @Test
    public void storeAll() {
        List<String> uuids = new ArrayList<String>();
        List<MockPojo> pojos = new ArrayList<MockPojo>();
        for (int i = 0; i < 7; i++) {
            MockPojo pojo = new MockPojo();
            pojo.setFoo(UUID.randomUUID().toString());

            if (i % 2 == 0) {
                facade.store(pojo);
                pojo.setFoo(UUID.randomUUID().toString());
            }

            uuids.add(pojo.getFoo());
            pojos.add(pojo);
        }

        facade.storeAll(pojos);

        pojos = facade.list(MockPojo.class);

        assertEquals(7, pojos.size());

        for (MockPojo pojo : pojos) {
            assertTrue(uuids.contains(pojo.getFoo()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteWithNull() {
        facade.delete(null);
    }

    @Test()
    public void delete() {
        MockPojo pojo = new MockPojo();
        facade.store(pojo);
        facade.delete(pojo);
        assertNull(facade.find(MockPojo.class, pojo.getId()));
    }

    @Test
    public void deleteNullId() {
        for (int i = 0; i < 7; i++) {
            MockPojo pojo = new MockPojo();
            facade.store(pojo);
        }

        assertEquals(Long.valueOf(7L), facade.count(MockPojo.class));
        facade.delete(new MockPojo());
        assertEquals(Long.valueOf(7L), facade.count(MockPojo.class));
    }

    @Test
    public void deleteAll() {
        List<String> uuids = new ArrayList<String>();
        List<MockPojo> pojos = new ArrayList<MockPojo>();
        for (int i = 0; i < 7; i++) {
            MockPojo pojo = new MockPojo();
            pojo.setFoo(UUID.randomUUID().toString());

            if (i % 2 == 0) {
                pojos.add(pojo);
            } else {
                uuids.add(pojo.getFoo());
            }
            facade.store(pojo);
        }

        pojos.add(new MockPojo());
        facade.deleteAll(pojos);

        pojos = facade.list(MockPojo.class);

        assertEquals(3, pojos.size());

        for (MockPojo pojo : pojos) {
            assertTrue(uuids.contains(pojo.getFoo()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteAllWithNull() {
        facade.deleteAll(null);
    }

    @Test
    public void countQuery() {
        for (int i = 0; i < 7; i++) {
            MockPojo pojo = new MockPojo();
            pojo.setFoo(i % 2 == 0 ? "foo" : "bar");
            facade.store(pojo);
        }

        String whereClause = "p.foo = :foo";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("foo", "foo");
        assertEquals(Long.valueOf(4L), facade.count(MockPojo.class,
                whereClause, parameters));
    }

    @Test(expected = IllegalArgumentException.class)
    public void countQueryClassIsNull() {
        facade.count(null, "", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void countQueryWhereIsNull() {
        facade.count(MockPojo.class, null, null);
    }

    @Test
    public void getFieldValues() {
        List<String> uuids = new ArrayList<String>();
        for (int i = 0; i < 7; i++) {
            MockPojo pojo = new MockPojo();
            String uuid = UUID.randomUUID().toString();
            if (i % 2 == 0) {
                pojo.setFoo("foo" + uuid);
                uuids.add("foo" + uuid);
            } else {
                pojo.setFoo("bar" + uuid);
            }

            facade.store(pojo);
        }

        String whereClause = "p.foo LIKE :foo";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("foo", "foo%");

        List<?> fieldsValues = facade.getFieldValues(MockPojo.class, "foo",
                whereClause, parameters);
        assertEquals(4, fieldsValues.size());

        for (String uuid : uuids) {
            fieldsValues.contains(uuid);
        }
    }

}
