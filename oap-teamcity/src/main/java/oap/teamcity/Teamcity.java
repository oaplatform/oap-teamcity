/*
 * The MIT License (MIT)
 *
 * Copyright (c) Open Application Platform Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package oap.teamcity;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import java.util.Random;
import java.util.function.Supplier;

public class Teamcity {
    public static final String FLOW_ID = String.valueOf( System.currentTimeMillis() ) + new Random().nextInt();

    public static String escape( String value ) {
        StringBuilder sb = new StringBuilder();

        for( int i = 0; i < value.length(); i++ )
            switch( value.charAt( i ) ) {
                case '\'':
                    sb.append( "|'" );
                    break;
                case '\n':
                    sb.append( "|n" );
                    break;
                case '\r':
                    sb.append( "|r" );
                    break;
                case '|':
                    sb.append( "||" );
                    break;
                case '[':
                    sb.append( "|[" );
                    break;
                case ']':
                    sb.append( "|]" );
                    break;
                default:
                    sb.append( value.charAt( i ) );
                    break;
            }

        return sb.toString();
    }

    public static void message( MessageStatus status, String text, String errorDetails ) {
        Preconditions.checkArgument( status == MessageStatus.ERROR );

        if( isTeamcity() )
            System.out.format( "##teamcity[message flowId='%s' text='%s' errorDetails='%s' status='%s']\n",
                FLOW_ID,
                escape( text ),
                escape( errorDetails ),
                status.name() );
    }

    public static void message( MessageStatus status, String text ) {
        Preconditions.checkArgument( status != MessageStatus.ERROR );

        if( isTeamcity() )
            System.out.format( "##teamcity[message flowId='%s' text='%s' status='%s']\n",
                FLOW_ID,
                escape( text ),
                status.name() );
    }

    public static void statistics( String name, Number value ) {
        if( isTeamcity() )
            System.out.format( "##teamcity[buildStatisticValue flowId='%s' key='%s' value='%s']\n",
                FLOW_ID,
                escape( name ),
                value );
    }

    public static <T> T progress( String message, Supplier<T> code ) {
        progressStart( message );
        try {
            return code.get();
        } finally {
            progressEnd( message );
        }
    }

    private static void progressStart( String message ) {
        if( isTeamcity() )
            System.out.format( "##teamcity[progressStart '%s' flowId='%s']\n",
                FLOW_ID,
                escape( message ) );
    }

    private static boolean isTeamcity() {
        return System.getenv( "TEAMCITY_CAPTURE_ENV" ) != null;
    }

    private static void progressEnd( String message ) {
        if( isTeamcity() )
            System.out.format( "##teamcity[progressFinish '%s' flowId='%s']\n",
                FLOW_ID,
                escape( message ) );
    }

    public static void performance( String name, double rate ) {
        statistics( name + ".actions/s", rate );
    }

    public static String buildPrefix() {
        String prefix = "";

        var teamcityBuildconfName = System.getenv( "TEAMCITY_BUILDCONF_NAME" );
        prefix += "_";
        if( teamcityBuildconfName != null ) prefix += teamcityBuildconfName;

        var buildNumber = System.getenv( "BUILD_NUMBER" );
        prefix += "_";
        if( buildNumber != null ) prefix += buildNumber;

        return prefix;
    }

    public static void testStarted( String testName ) {
        if( isTeamcity() ) {
            System.out.format( "##teamcity[testStarted flowId='%s' name='%s' captureStandardOutput='true']\n",
                FLOW_ID,
                escape( testName ) );
        }
    }

    public static void testFinished( String testName, long durationMs ) {
        if( isTeamcity() ) {
            System.out.format( "##teamcity[testFinished flowId='%s' name='%s' duration='%s']\n",
                FLOW_ID,
                escape( testName ), durationMs );
        }
    }

    public static void testIgnored( String testName, String reason ) {
        if( isTeamcity() ) {
            System.out.format( "##teamcity[testIgnored flowId='%s' name='%s' message='%s']\n",
                FLOW_ID,
                escape( testName ), escape( reason ) );
        }
    }

    public static void testFailed( String testName, Throwable throwable, long durationMs ) {
        if( isTeamcity() ) {
            System.out.format( "##teamcity[testFailed flowId='%s' name='%s' message='%s' details='%s']\n",
                FLOW_ID,
                escape( testName ), escape( throwable.getMessage() ),
                escape( Throwables.getStackTraceAsString( throwable ) ) );
        }
        testFinished( testName, durationMs );
    }

    public static void testSuiteStarted( String suiteName ) {
        if( isTeamcity() ) {
            System.out.format( "##teamcity[testSuiteStarted flowId='%s' name='%s']\n",
                FLOW_ID,
                escape( suiteName ) );
        }
    }

    public static void testSuiteFinished( String suiteName ) {
        if( isTeamcity() ) {
            System.out.format( "##teamcity[testSuiteFinished flowId='%s' name='%s']\n",
                FLOW_ID,
                escape( suiteName ) );
        }
    }

    public enum MessageStatus {
        NORMAL, WARNING, FAILURE, ERROR
    }
}
