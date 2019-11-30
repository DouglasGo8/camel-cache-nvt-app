package br.com.itau.camel.cache.nvt;

import org.apache.camel.builder.RouteBuilder;

public class CacheRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Cache Configuration
            from("cache://stsTokenCache" +
                "?maxElementsInMemory=1" +
                "&memoryStoreEvictionPolicy=FIFO" +
                "&overflowToDisk=true" +
                "&eternal=true" +
                "&timeToLiveSeconds=300" +
                "&timeToIdleSeconds=300" +
                "&diskPersistent=true")
                .streamCaching()
                .log("Cache Ready!!")
                .end();

    }
}
