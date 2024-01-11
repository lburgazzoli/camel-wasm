package io.github.lburgazzoli.camel.component.wasm;

import org.apache.camel.Category;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

/**
 * Invoke Wasm functions.
 */
@UriEndpoint(
    firstVersion = "4.4.0",
    scheme = Wasm.SCHEME,
    title = "Wasm",
    syntax = "wasm:resource",
    producerOnly = true,
    category = {
        Category.CORE,
        Category.SCRIPT
    },
    headersClass = Wasm.Headers.class)
public class WasmEndpoint extends DefaultEndpoint {

    @Metadata(required = true)
    @UriPath(description = "The Function Name")
    private final String functionName;

    @UriParam
    private WasmConfiguration configuration;


    public WasmEndpoint(
            String endpointUri,
            Component component,
            String functionName,
            WasmConfiguration configuration) {

        super(endpointUri, component);

        this.functionName = functionName;
        this.configuration = configuration;
    }

    public WasmConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new WasmProducer(this, configuration.getResource(), functionName);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("You cannot consume from a wasm endpoint");
    }
}
