package oap.teamcity.testng;

import oap.teamcity.Teamcity;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestResult;
import org.testng.internal.IResultListener;

public class TeamcityTestNGListener implements IResultListener, ISuiteListener {
    static String getSuiteNameFromOutputDirectory( String outputDirectory ) {
        var items = outputDirectory.split( "\\\\|/" );

        return items[items.length - 4];
    }

    @Override
    public void onTestStart( ITestResult result ) {
        Teamcity.testStarted( getTestName( result ) );
    }

    @Override
    public void onTestSuccess( ITestResult result ) {
        Teamcity.testFinished( getTestName( result ), result.getStartMillis() - result.getEndMillis() );
    }

    @Override
    public void onTestSkipped( ITestResult result ) {
        Teamcity.testIgnored( getTestName( result ), "Skipped" );
    }

    @Override
    public void onTestFailure( ITestResult result ) {
        Teamcity.testFailed( getTestName( result ), result.getThrowable(), result.getStartMillis() - result.getEndMillis() );
    }

    private String getTestName( ITestResult result ) {
        return result.getTestClass().getRealClass().getName() + "." + result.getName();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage( ITestResult result ) {
        onTestFailure( result );
    }

    @Override
    public void onTestFailedWithTimeout( ITestResult result ) {
        onTestFailure( result );
    }

    @Override
    public void onStart( ISuite suite ) {
        Teamcity.testSuiteStarted( getSuiteNameFromOutputDirectory( suite.getOutputDirectory() ) );
    }

    @Override
    public void onFinish( ISuite suite ) {
        Teamcity.testSuiteFinished( getSuiteNameFromOutputDirectory( suite.getOutputDirectory() ) );
    }
}
