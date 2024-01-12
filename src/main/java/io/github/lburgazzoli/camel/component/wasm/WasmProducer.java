package io.github.lburgazzoli.camel.component.wasm;

import java.io.InputStream;

import com.dylibso.chicory.runtime.Module;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.ResourceLoader;
import org.apache.camel.support.DefaultProducer;
import org.apache.camel.support.PluginHelper;

public class WasmProducer extends DefaultProducer {

    private final String resource;
    private final String functionName;

    private Module module;
    private WasmFunction function;

    public WasmProducer(Endpoint endpoint, String resource, String functionName) throws Exception {
        super(endpoint);

        this.resource = resource;
        this.functionName = functionName;
    }


    @Override
    public void doInit() throws Exception {
        final ResourceLoader rl = PluginHelper.getResourceLoader(getEndpoint().getCamelContext());
        final Resource res = rl.resolveResource(this.resource);

        try (InputStream is = res.getInputStream()) {
            this.module = Module.build(is);
        }
    }

    @Override
    public void doStart() throws Exception {
        super.doStart();

        if (this.module != null && this.function == null) {
            this.function = new WasmFunction(this.module, this.functionName);
        }
    }

    @Override
    public void doStop() throws Exception {
        super.doStop();

        this.function = null;
    }

    @Override
    public void doShutdown() throws Exception {
        super.doShutdown();

        this.function = null;
        this.module = null;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        byte[] in = WasmSupport.serialize(exchange);
        byte[] result = function.run(in);

        WasmSupport.deserialize(result, exchange);
    }
}
