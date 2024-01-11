package io.github.lburgazzoli.camel.component.wasm;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;

public class WasmComponentTest {
    @Test
    public void test() throws Exception {
        try (CamelContext cc = new DefaultCamelContext()) {
            cc.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("direct:start")
                        .to("wasm:foo/process")
                        .to("mock:result");
                }
            });
            cc.start();
        }
    }
}
