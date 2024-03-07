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

@Mojo( name = "report", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST )
public class FolderSizeMojo extends AbstractMojo {
    @Parameter( required = true )
    public String directory;

    @Parameter( required = true )
    public String name;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            Path folder = Paths.get( directory );
            try( Stream<Path> stream = Files.walk( folder ) ) {
                long size = stream
                    .filter( p -> p.toFile().isFile() )
                    .mapToLong( p -> p.toFile().length() )
                    .sum();

                Teamcity.statistics( "directory_size_" + name, size );
            }

        } catch( IOException e ) {
            throw new MojoExecutionException( e );
        }

    }
}
