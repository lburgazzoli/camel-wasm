package io.github.lburgazzoli.camel.component.wasm;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.Configurer;
import org.apache.camel.spi.UriParams;

@Configurer
@UriParams
public class WasmConfiguration implements Cloneable {

    // ************************
    //
    // Clone
    //
    // ************************

    public WasmConfiguration copy() {
        try {
            return (WasmConfiguration) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeCamelException(e);
        }
    }
}
