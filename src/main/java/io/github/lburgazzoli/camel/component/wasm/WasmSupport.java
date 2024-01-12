package io.github.lburgazzoli.camel.component.wasm;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.camel.Exchange;

public final class WasmSupport {
    public static final ObjectMapper MAPPER = JsonMapper.builder().build();

    private WasmSupport() {
    }

    public static byte[] serialize(Exchange exchange) throws Exception {
        Wrapper env = new Wrapper();
        env.body = exchange.getMessage().getBody(byte[].class);

        for (String headerName: exchange.getMessage().getHeaders().keySet()) {
            env.headers.put(headerName, exchange.getMessage().getHeader(headerName, String.class));
        }

        return MAPPER.writeValueAsBytes(env);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void deserialize(byte[] in, Exchange out) throws Exception {
        // cleanup
        out.getMessage().getHeaders().clear();
        out.getMessage().setBody(null);

        Wrapper w = MAPPER.readValue(in, Wrapper.class);
        out.getMessage().setBody(w.body);

        if (w.headers != null) {
            out.getMessage().setHeaders((Map) w.headers);
        }
    }

    public static class Wrapper {
        @JsonProperty
        public Map<String, String> headers = new HashMap<>();

        @JsonProperty
        public byte[] body;
    }
}
