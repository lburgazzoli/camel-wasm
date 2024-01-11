package io.github.lburgazzoli.camel.component.wasm;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.Configurer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriParams;

@Configurer
@UriParams
public class WasmConfiguration implements Cloneable {

    @Metadata(required = true)
    @UriParam
    private String function;

    /**
     * The function
     */
    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

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
