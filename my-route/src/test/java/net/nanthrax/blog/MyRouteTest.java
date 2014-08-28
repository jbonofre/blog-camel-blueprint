package net.nanthrax.blog;

import net.nanthrax.blog.service.EchoService;
import net.nanthrax.blog.service.internal.EchoServiceImpl;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.camel.util.KeyValueHolder;
import org.junit.Test;

import java.util.Dictionary;
import java.util.Map;

public class MyRouteTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/route.xml";
    }

    @Override
    public String isMockEndpointsAndSkip() {
        return "((file)|(timer)):(.*)";
    }

    @Override
    protected void addServicesOnStartup(Map<String, KeyValueHolder<Object, Dictionary>> services) {
        KeyValueHolder serviceHolder = new KeyValueHolder(new EchoServiceImpl(), null);
        services.put(EchoService.class.getName(), serviceHolder);
    }

    @Test
    public void testMyRoute() throws Exception {

        // mocking the file endpoint and define the expectation
        MockEndpoint mockEndpoint = getMockEndpoint("mock:file:camel-output");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived("Echoing Hello Blog");

        // send a message at the timer endpoint level
        template.sendBody("mock:timer:fire", "empty");

        // check if the expectation is satisfied
        assertMockEndpointsSatisfied();
    }

}
