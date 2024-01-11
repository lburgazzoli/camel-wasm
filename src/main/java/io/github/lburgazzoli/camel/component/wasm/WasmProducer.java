package io.github.lburgazzoli.camel.component.wasm;

import java.io.InputStream;

import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.runtime.Module;
import com.dylibso.chicory.wasm.types.Value;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.ResourceLoader;
import org.apache.camel.support.DefaultAsyncProducer;
import org.apache.camel.support.PluginHelper;

public class WasmProducer extends DefaultAsyncProducer {

    private final String resource;
    private final String functionName;

    private Module module;
    private Instance instance;
    private ExportFunction function;

    private ExportFunction alloc;
    private ExportFunction dealloc;

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

        if (this.module != null && this.instance == null) {
            this.instance = this.module.instantiate();

            this.function = instance.getExport(this.functionName);
            this.alloc = instance.getExport(Wasm.FN_ALLOC);
            this.dealloc = instance.getExport(Wasm.FN_DEALLOC);
        }
    }

    @Override
    public void doStop() throws Exception {
        super.doStop();

        this.instance = null;
    }

    @Override
    public void doShutdown() throws Exception {
        super.doShutdown();

        this.instance = null;
        this.module = null;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        Memory memory = instance.getMemory();
        int inPtr = -1;
        int inSize = 0;
        int outPtr = -1;
        int outSize = 0;

        try {
            // TODO: serialize exchange
            // TODO: compute inSize
            inPtr = alloc.apply(Value.i32(inSize))[0].asInt();
            memory.write(inPtr, (byte[])null);

            long ptrAndSize = function.apply(Value.i32(inPtr), Value.i32(inSize))[0].asLong();

            outPtr = (int)(ptrAndSize >> 32);
            outSize = (int)ptrAndSize;

            byte[] answer = memory.readBytes(outPtr, outSize);

            // TODO: de-serialize exchange

        } finally {
            if (inPtr != -1) {
                // TODO: check for error
                dealloc.apply(Value.i32(inPtr), Value.i32(inSize));
            }
            if (outPtr != -1) {
                // TODO: check for error
                dealloc.apply(Value.i32(outPtr), Value.i32(outSize));
            }
        }

        throw new UnsupportedOperationException("TODO");
    }
}
