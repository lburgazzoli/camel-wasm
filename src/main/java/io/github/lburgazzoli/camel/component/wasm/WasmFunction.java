package io.github.lburgazzoli.camel.component.wasm;

import java.util.Objects;

import com.dylibso.chicory.runtime.ExportFunction;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Module;
import com.dylibso.chicory.wasm.types.Value;

public class WasmFunction implements AutoCloseable {
    private final Module module;
    private final String functionName;

    private final Instance instance;
    private final ExportFunction function;
    private final ExportFunction alloc;
    private final ExportFunction  dealloc;

    public WasmFunction(Module module, String functionName) {
        this.module = Objects.requireNonNull(module);
        this.functionName = Objects.requireNonNull(functionName);

        this.instance = this.module.instantiate();
        this.function = this.instance.getExport(this.functionName);
        this.alloc = this.instance.getExport(Wasm.FN_ALLOC);
        this.dealloc = this.instance.getExport(Wasm.FN_DEALLOC);
    }

    public byte[] run(byte[] in) throws Exception {
        Objects.requireNonNull(in);

        int inPtr = -1;
        int inSize = in.length;
        int outPtr = -1;
        int outSize = 0;

        try {
            inPtr = alloc.apply(Value.i32(inSize))[0].asInt();
            instance.getMemory().write(inPtr, in);

            Value[] results = function.apply(Value.i32(inPtr), Value.i32(inSize));
            long ptrAndSize = results[0].asUInt();

            outPtr = (int)(ptrAndSize >> 32);
            outSize = (int)ptrAndSize;

            return instance.getMemory().readBytes(outPtr, outSize);
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
    }

    @Override
    public void close() throws Exception {

    }
}
