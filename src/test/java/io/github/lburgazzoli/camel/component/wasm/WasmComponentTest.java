package io.github.lburgazzoli.camel.component.wasm;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WasmComponentTest {
    @Test
    public void test() throws Exception {
        try (CamelContext cc = new DefaultCamelContext()) {
            FluentProducerTemplate pt = cc.createFluentProducerTemplate();

            cc.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:in")
                        .to("wasm:process?resource=to_upper.wasm");
                }
            });
            cc.start();

            Exchange out = pt.to("direct:in")
                .withHeader("foo", "bar")
                .withBody("hello")
                .request(Exchange.class);


            assertThat(out.getMessage().getHeaders())
                .containsEntry("foo", "bar");
            assertThat(out.getMessage().getBody())
                .isEqualTo("HELLO");

        }
    }
}
