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
    private String resource;

    /**
     * The resource
     */
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
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
