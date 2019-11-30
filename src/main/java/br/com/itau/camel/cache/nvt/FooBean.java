package br.com.itau.camel.cache.nvt;

import java.util.UUID;

/**
 *
 */
public class FooBean {
    public String ofRandomUUIDToCache() {
        return UUID.randomUUID().toString();
    }
}
