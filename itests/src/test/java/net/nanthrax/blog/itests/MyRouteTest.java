package net.nanthrax.blog.itests;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.karaf.features.FeaturesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import java.io.File;

@RunWith(PaxExam.class)
public class MyRouteTest extends CamelTestSupport {

    @Inject
    protected FeaturesService featuresService;

    @Inject
    protected BundleContext bundleContext;

    @Configuration
    public static Option[] configure() throws Exception {
        return new Option[] {
                karafDistributionConfiguration()
                        .frameworkUrl(maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("2.3.6"))
                        .karafVersion("2.3.6")
                        .useDeployFolder(false)
                        .unpackDirectory(new File("target/paxexam/unpack")),
                logLevel(LogLevelOption.LogLevel.WARN),
                features(maven().groupId("org.apache.camel.karaf").artifactId("apache-camel").type("xml").classifier("features").version("2.12.1"), "camel-blueprint", "camel-test"),
                features(maven().groupId("net.nanthrax.blog").artifactId("camel-blueprint").type("xml").classifier("features").version("1.0-SNAPSHOT"), "blog-camel-blueprint-route"),
                keepRuntimeFolder()
        };
    }

    @Test
    public void testProvisioning() throws Exception {
        // first check that the features are installed
        assertTrue(featuresService.isInstalled(featuresService.getFeature("camel-blueprint")));
        assertTrue(featuresService.isInstalled(featuresService.getFeature("blog-camel-blueprint-route")));

        // now we check if the OSGi services corresponding to the camel context and route are there

    }

    @Test
    public void testMyRoute() throws Exception {
        MockEndpoint itestMock = getMockEndpoint("mock:itest");
        itestMock.expectedMinimumMessageCount(3);
        itestMock.whenAnyExchangeReceived(new Processor() {
            public void process(Exchange exchange) {
                System.out.println(exchange.getIn().getBody(String.class));
            }
        });

        template.start();

        Thread.sleep(20000);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("file:camel-output").to("mock:itest");
            }
        };
    }

}
