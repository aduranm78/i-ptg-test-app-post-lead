package com.redhat;

//import com.redhat.dto.Customer;
//import com.redhat.dto.CustomerSuccess;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class Routes extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    restConfiguration()
      .component("netty-http")
      .port("8080")
      .bindingMode(RestBindingMode.auto);
    
    rest()
      .path("/").consumes("application/json").produces("application/json")
        .put("/post-lead")
//          .type(Customer.class).outType(CustomerSuccess.class)
          .to("direct:put-customer")
        .post("/post-lead")
//          .type(Customer.class).outType(CustomerSuccess.class)
          .to("direct:post-customer");
    
    from("direct:post-customer")
      .setHeader("HTTP_METHOD", constant("POST"))
      .to("direct:request");
    from("direct:put-customer")
      .setHeader("HTTP_METHOD", constant("PUT"))
      .to("direct:request");

    from("direct:request")
      .setHeader("backend", simple("{{redhat.backend}}"))
      .to("log:DEBUG?showBody=true&showHeaders=false")
      //.toD("https://${header.backend}&bridgeEndpoint=true&throwExceptionOnFailure=false")
      .toD("https://5298967-sb1.restlets.api.netsuite.com/app/site/hosting/restlet.nl?script=581&deploy=1&bridgeEndpoint=true&throwExceptionOnFailure=false")
      .to("log:DEBUG?showBody=true&showHeaders=false");
      
//      .choice()
//        .when(simple("${header.CamelHttpResponseCode} != 201 && ${header.CamelHttpResponseCode} != 202"))
//          .log("err")
//          .transform(constant("Error"))
//        .otherwise()
//          .log("ok")
//      .endChoice();
  }
}