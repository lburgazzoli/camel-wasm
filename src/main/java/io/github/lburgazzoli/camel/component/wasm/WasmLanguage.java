package io.github.lburgazzoli.camel.component.wasm;

import java.io.IOException;
import java.io.InputStream;

import com.dylibso.chicory.runtime.Module;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.ResourceLoader;
import org.apache.camel.spi.annotations.Language;
import org.apache.camel.support.ExpressionAdapter;
import org.apache.camel.support.LanguageSupport;
import org.apache.camel.support.PluginHelper;
import org.apache.camel.util.StringHelper;

@Language(Wasm.SCHEME)
public class WasmLanguage extends LanguageSupport  {
    @Override
    public Predicate createPredicate(String expression) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Expression createExpression(String expression) {
        final String functionName = StringHelper.before(expression, "@");
        final String resource = StringHelper.after(expression, "@");

        return new ExpressionAdapter() {
            private Module module;
            private WasmFunction function;

            @Override
            public void init(CamelContext context) {
                final ResourceLoader rl = PluginHelper.getResourceLoader(context);
                final Resource res = rl.resolveResource(resource);

                try (InputStream is = res.getInputStream()) {
                    this.module = Module.build(is);
                    this.function = new WasmFunction(module, functionName);
                } catch (IOException e) {
                    throw new RuntimeCamelException(e);
                }
            }

            @Override
            public Object evaluate(Exchange exchange) {
                try {
                    byte[] in = WasmSupport.serialize(exchange);
                    byte[] result = function.run(in);

                    return result;
                } catch (Exception e) {
                    throw new RuntimeCamelException(e);
                }
            }

            @Override
            public String toString() {
                return Wasm.SCHEME + "(" + expression + ")";
            }
        };
    }
}
