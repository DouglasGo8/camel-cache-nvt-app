package br.com.itau.camel.cache.nvt;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cache.CacheConstants;

/**
 * @author dbatista
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {

        onException(RuntimeException.class)
                .handled(true)
                //.useOriginalMessage()
                .log("Fail with message -- ${body}")
                .maximumRedeliveries(1)
                .useExponentialBackOff()
                .backOffMultiplier(2)
                .redeliveryDelay(5000)
                .end();

        from("timer:fromUpdateStsCache?period=50s&delay=8s")
                .setHeader(CacheConstants.CACHE_OPERATION, simple(CacheConstants.CACHE_OPERATION_CHECK))
                .setHeader(CacheConstants.CACHE_KEY, constant("key"))
                .to("cache://stsTokenCache")
                .choice()
                .when(header(CacheConstants.CACHE_ELEMENT_WAS_FOUND).isNull())
                    .log("Adding value to cache")
                    .bean(FooBean.class)
                    .setHeader(CacheConstants.CACHE_OPERATION, simple(CacheConstants.CACHE_OPERATION_ADD))
                    .setHeader(CacheConstants.CACHE_KEY, constant("key"))
                    .to("cache://stsTokenCache")
                .otherwise()
                    .log("Updating value to cache")
                    .bean(FooBean.class)
                    .setHeader(CacheConstants.CACHE_OPERATION, simple(CacheConstants.CACHE_OPERATION_UPDATE))
                    .setHeader(CacheConstants.CACHE_KEY, constant("key"))
                    .to("cache://stsTokenCache")
                .end();


        from("timer:fromWMQ1Timer?period=5s&fixedRate=true")
                .setBody(simple("${date:now}"))
                .log("Receiving a Message from WMQ at - ${body}")
                .setHeader(CacheConstants.CACHE_OPERATION, simple(CacheConstants.CACHE_OPERATION_CHECK))
                .setHeader(CacheConstants.CACHE_KEY, constant("key"))
                .to("cache://stsTokenCache")
                .choice()
                    .when(header(CacheConstants.CACHE_ELEMENT_WAS_FOUND).isNull())
                        .transform(simple("${body}"))
                        .throwException(new RuntimeException())
                .otherwise()
                    .log("Happy Forever at - ${body}")
                    .setHeader(CacheConstants.CACHE_OPERATION, simple(CacheConstants.CACHE_OPERATION_GET))
                    .setHeader(CacheConstants.CACHE_KEY, constant("key"))
                    .to("cache://stsTokenCache")
                    .log("----> ${body}")
                .end()

        .end();


    }


}
