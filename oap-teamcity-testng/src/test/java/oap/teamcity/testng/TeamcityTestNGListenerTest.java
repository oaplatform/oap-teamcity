package oap.teamcity.testng;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TeamcityTestNGListenerTest {
    @Test
    public void testGetSuiteNameFromOutputDirectory() {
        assertEquals( TeamcityTestNGListener.getSuiteNameFromOutputDirectory( "C:\\workspace\\projects\\oap\\oap-stdlib-test\\target\\surefire-reports\\Surefire suite" ), "oap-stdlib-test" );
        assertEquals( TeamcityTestNGListener.getSuiteNameFromOutputDirectory( "/workspace/projects/oap/oap-stdlib-test/target/surefire-reports/Surefire suite" ), "oap-stdlib-test" );
    }

}