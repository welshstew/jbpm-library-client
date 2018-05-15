/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.nullendpoint;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends RouteBuilder {

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    LibraryClient libraryClient;

    @Override
    public void configure() throws Exception {

        from("timer://foo?fixedRate=true&period=5000&repeatCount=1").routeId("mainRoute")
            .setBody(constant("Zombie"))
            .bean(libraryClient, "getSuggestion")
            .log("Received suggestion for book: ${body.book.title}  ${body.book.isbn}")
            .setProperty("book1_WorldWarZ", simple("${body.book}"))
            .log("Attempting 1st loan for isbn: ${body.book.isbn}")
            .setBody(simple("${body.book.isbn}"))
            .bean(libraryClient, "attemptLoan")
            .log("1st loan approved? ${body.approved}")
            .setProperty("loan1_WorldWarZ", simple("${body}"))
            .setBody(simple("${property.book1_WorldWarZ.isbn}"))
             .log("2nd loan should not be approved since 1st loan hasn't been returned")
            .bean(libraryClient, "attemptLoan")
            .log("2nd loan approved? ${body.approved}")
            .setProperty("loan2_WorldWarZ", simple("${body}"))
            .log("return 1st loan")
            .log("Returning 1st loan for isbn: ${property.book1_WorldWarZ.isbn}")
            .setBody(exchangeProperty("loan1_WorldWarZ"))
            .bean(libraryClient, "returnLoan")
            .log("1st loan return acknowledged? ${body}")
            .log("try 2nd loan again; this time it should work")
            .log("Re-attempting 2nd loan for isbn: ${property.book1_WorldWarZ.isbn}")
            .setBody(simple("${property.book1_WorldWarZ.isbn}"))
            .bean(libraryClient, "attemptLoan")
                .setProperty("loan2_WorldWarZ", simple("${body}"))
                .log("Re-attempt of 2nd loan approved? ${body.approved}")
                .log("get 2nd suggestion, and since 1st book not available (again), 2nd match will return")
                .setBody(constant("Zombie"))
            .bean(libraryClient, "getSuggestion")
                .log("Received suggestion for book: ${body.book.title}  ${body.book.isbn}")
                .setProperty("book2_TheZombieSurvivalGuide", simple("${body.book}"))
                .log("take out 3rd loan")
                .log("Attempting 3rd loan for isbn: ${body.book.isbn}")
                .setBody(simple("${property.book2_TheZombieSurvivalGuide.isbn}"))
                .bean(libraryClient, "attemptLoan")
                .setProperty("loan3_TheZombieSurvivalGuide", simple("${body}"))
                .log("3rd loan approved? ${body.approved}")
                .log("return 2nd loan")
                .log("Returning 2nd loan for isbn: ${property.loan2_WorldWarZ.book.isbn}")
                .setBody(simple("${property.loan2_WorldWarZ}"))
                .bean(libraryClient, "returnLoan")
                .log("2nd loan return acknowledged? ${body}")
                .log("return 3rd loan")
                .log("Returning 3rd loan for isbn: ${property.loan3_TheZombieSurvivalGuide.book.isbn}")
                .setBody(simple("${property.loan3_TheZombieSurvivalGuide}"))
                .bean(libraryClient, "returnLoan")
                .log("3rd loan return acknowledged? ${body}");
    }
}
