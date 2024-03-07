package oap.temcity.mavan;

import oap.teamcity.Teamcity;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Mojo( name = "folder-size", threadSafe = true, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST )
public class FolderSizeMojo extends AbstractMojo {
    @Parameter( required = true )
    public String directory;

    @Parameter( alias = "statistic-name", required = true )
    public String statisticName;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            getLog().info( "statisticName " + statisticName + " directory " + directory );
            Path folder = Paths.get( directory );
            try( Stream<Path> stream = Files.walk( folder ) ) {
                long size = stream
                    .filter( p -> p.toFile().isFile() )
                    .mapToLong( p -> {
                        getLog().debug( "file " + p + " size " + p.toFile().length() );

                        return p.toFile().length();
                    } )
                    .sum();

                getLog().info( "statisticName " + statisticName + " directory " + directory + " size " + size );

                Teamcity.statistics( statisticName, size );
            }

        } catch( IOException e ) {
            throw new MojoExecutionException( e );
        }

    }
}
