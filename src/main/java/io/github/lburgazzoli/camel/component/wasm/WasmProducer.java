package io.github.lburgazzoli.camel.component.wasm;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultAsyncProducer;

public class WasmProducer extends DefaultAsyncProducer {
    public WasmProducer(Endpoint endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        throw new UnsupportedOperationException("TODO");
    }
}
