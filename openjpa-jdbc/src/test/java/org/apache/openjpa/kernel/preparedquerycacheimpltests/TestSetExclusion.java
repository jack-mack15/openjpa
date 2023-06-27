package org.apache.openjpa.kernel.preparedquerycacheimpltests;

import org.apache.openjpa.jdbc.kernel.PreparedQueryCacheImpl;
import org.apache.openjpa.jdbc.kernel.PreparedQueryImpl;
import org.apache.openjpa.kernel.PreparedQuery;
import org.apache.openjpa.kernel.PreparedQueryCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;

@RunWith(value= Parameterized.class)
public class TestSetExclusion {
    private PreparedQueryCache cache;
    private boolean expected;
    private String input;
    private String[] splittedInput;
    private boolean isCached;

    @Before
    public void setUp(){
        cache = new PreparedQueryCacheImpl();

        PreparedQuery query1 = new PreparedQueryImpl("testId",   "testSql",null);
        PreparedQuery query2 = new PreparedQueryImpl("testId2",   "testSql2",null);

        cache.cache(query1);
        cache.cache(query2);

        cache.endConfiguration();
        try {
            splittedInput = input.split("\\;");
        }
        catch(NullPointerException e){
            //do nothing
        }
    }

    @Parameters
    public static Collection<Object[]> getParameters(){
        return Arrays.asList(new Object[][]{
                //expected      isCached,       input
                {true,          false,          "test1;test2;test3"},
                {true,          true,           "testId"},
                {false,         false,          ""},
                {false,         false,          null}
        });
    }

    public TestSetExclusion(boolean expected, boolean isCached, String input){
        this.input = input;
        this.isCached = isCached;
        this.expected = expected;
    }

    @Test
    public void testSetExcludes(){

        cache.setExcludes(input);

        List<PreparedQueryCache.Exclusion> resultList = cache.getExcludes();

        int resultLen = resultList.size();

        if (resultLen == 0){
            assertFalse(expected);
        }
        else{
            if (splittedInput.length != resultLen)
                //mi aspetto di non entrare mai in questo ramo
                Assert.assertNotNull(null);

            for(int i = 0; i < resultLen; i++){
                Assert.assertEquals(splittedInput[i],resultList.get(i).getPattern());
                if(isCached)
                    Assert.assertFalse(cache.isCachable(splittedInput[i]));
                else
                    Assert.assertNull(cache.isCachable(splittedInput[i]));

            }

            Assert.assertTrue(expected);
        }

    }

    @After
    public void tearDown(){
        cache.clear();
    }
}
