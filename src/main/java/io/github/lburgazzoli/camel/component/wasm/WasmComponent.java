package io.github.lburgazzoli.camel.component.wasm;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.StringHelper;

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
            throw new IllegalArgumentException("Expecting URI in the form of: 'wasm:resource/function', got '" + uri + "'");
        }

        final String resource = StringHelper.before(remaining, "/");
        final String function = StringHelper.after(remaining, "/");

        if (ObjectHelper.isEmpty(resource)) {
            throw new IllegalArgumentException("Expecting resource to be set', got '" + uri + "'");
        }
        if (ObjectHelper.isEmpty(function)) {
            throw new IllegalArgumentException("Expecting function to be set', got '" + uri + "'");
        }

        WasmConfiguration configuration = this.configuration.copy();

        WasmEndpoint endpoint = new WasmEndpoint(uri, this, resource, function, configuration);
        setProperties(endpoint, parameters);

        return endpoint;
    }
}
