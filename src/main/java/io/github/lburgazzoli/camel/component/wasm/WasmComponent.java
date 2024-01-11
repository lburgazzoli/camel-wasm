package io.github.lburgazzoli.camel.component.wasm;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;
import org.apache.camel.util.ObjectHelper;

@Component(Wasm.SCHEME)
public class WasmComponent extends DefaultComponent {
    @Metadata
    private WasmConfiguration configuration;

    public WasmComponent() {
        this(null);
    }

    public WasmComponent(CamelContext context) {
        super(context);

        this.configuration = new WasmConfiguration();
    }

    @Override
    protected Endpoint createEndpoint(
            String uri,
            String remaining,
            Map<String, Object> parameters) throws Exception {

        if (ObjectHelper.isEmpty(remaining)) {
            throw new IllegalArgumentException("Expecting URI in the form of: 'wasm:resource/', got '" + uri + "'");
        }

        WasmConfiguration configuration = this.configuration.copy();

        WasmEndpoint endpoint = new WasmEndpoint(uri, this, remaining, configuration);
        setProperties(endpoint, parameters);

        return endpoint;
    }
}
