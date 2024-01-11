package io.github.lburgazzoli.camel.component.wasm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.dylibso.chicory.runtime.Module;
import com.fasterxml.jackson.annotation.JsonValue;
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
        byte[] in = serialize(exchange);
        byte[] result = function.run(in);

        deserialize(result, exchange);
    }

    //
    // Terrible code here below with a lot of assumptions ...
    // But good enough for the POC
    //

    public byte[] serialize(Exchange exchange) throws Exception {
        Envelope env = new Envelope();
        env.body = exchange.getMessage().getBody(byte[].class);

        for (String headerName: exchange.getMessage().getHeaders().keySet()) {
            env.headers.put(headerName, exchange.getMessage().getHeader(headerName, String.class));
        }

        return Wasm.MAPPER.writeValueAsBytes(env);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void deserialize(byte[] in, Exchange out) throws Exception {
        // cleanup
        out.getMessage().getHeaders().clear();
        out.getMessage().setBody(null);

        Envelope env = Wasm.MAPPER.readValue(in, Envelope.class);
        out.getMessage().setBody(env.body);

        if (env.headers != null) {
            out.getMessage().setHeaders((Map) env.headers);
        }
    }

    public static class Envelope {
        @JsonValue
        public Map<String, String> headers = new HashMap<>();

        @JsonValue
        public byte[] body;
    }
}
