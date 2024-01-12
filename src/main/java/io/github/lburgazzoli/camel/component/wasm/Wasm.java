package io.github.lburgazzoli.camel.component.wasm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class Wasm {
    public static final String SCHEME = "wasm";
    public static final String FN_ALLOC = "alloc";
    public static final String FN_DEALLOC = "dealloc";

    private Wasm() {
    }

    public static class Headers {

    }


}