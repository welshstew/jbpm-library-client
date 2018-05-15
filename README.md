# jbpm library client

A FIS implementation of the example used for the processserver-library found at [openshift-quickstarts](https://github.com/jboss-openshift/openshift-quickstarts.git).


## How to

First, we need to build and deploy the processserver image running the library jbpm flow

```text
git clone https://github.com/jboss-openshift/openshift-quickstarts.git
cd openshift-quickstarts/processserver/
mkdir -p ps-bin-demo/deployments
mvn clean install
cp library/target/processserver-library-1.4.0.Final.jar ps-bin-demo/deployments/
cp library-client/target/processserver-library-client-1.4.0.Final.war ps-bin-demo/deployments/
oc new-project ps-bin-demo
oc new-build --binary=true --name=ps-l-app --image-stream=jboss-processserver63-openshift:1.4 -e KIE_SERVER_USER=kieserveruser -e KIE_SERVER_PASSWORD=kieserverPwd1!
oc start-build ps-l-app --from-dir=./ps-bin-demo/ --follow
oc new-app ps-l-app
oc expose svc/ps-l-app
```

We should now have a route available where we can use the REST endpoint on the kie-server to run tasks/processes.

## Run this project locally

Ensure the application.properties file is updated with the appropriate url for the route.

The `mvn clean install` from earlier should have put the `processserver-library` artifact in your local maven repo in order
for you to use this component (You'll need the model classes to build this project).

```text
kie.resturl=http://ps-l-app-ps-bin-demo.192.168.42.77.nip.io/kie-server/services/rest/server
kie.username=kieserveruser
kie.password=kieserverPwd1!
```

Build and run the app:

```text
mvn clean package
mvn spring-boot:run

```

## Log Output

Should see the following output from the camel log

```text
16:38:53.630 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Received suggestion for book: World War Z  978-0-307-35193-7
16:38:53.631 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Attempting 1st loan for isbn: 978-0-307-35193-7
16:38:53.746 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - 1st loan approved? true
16:38:53.749 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - 2nd loan should not be approved since 1st loan hasn't been returned
16:38:53.813 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - 2nd loan approved? false
16:38:53.813 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - return 1st loan
16:38:53.814 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Returning 1st loan for isbn: 978-0-307-35193-7
16:38:53.903 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - 1st loan return acknowledged? true
16:38:53.904 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - try 2nd loan again; this time it should work
16:38:53.905 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Re-attempting 2nd loan for isbn: 978-0-307-35193-7
16:38:53.982 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Re-attempt of 2nd loan approved? true
16:38:53.982 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - get 2nd suggestion, and since 1st book not available (again), 2nd match will return
16:38:54.005 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Received suggestion for book: The Zombie Survival Guide  978-1-4000-5-80-2
16:38:54.006 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - take out 3rd loan
16:38:54.007 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Attempting 3rd loan for isbn: 978-1-4000-5-80-2
16:38:54.089 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - 3rd loan approved? true
16:38:54.090 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - return 2nd loan
16:38:54.091 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Returning 2nd loan for isbn: 978-0-307-35193-7
16:38:54.147 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - 2nd loan return acknowledged? true
16:38:54.147 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - return 3rd loan
16:38:54.148 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - Returning 3rd loan for isbn: 978-1-4000-5-80-2
16:38:54.201 [Camel (MyCamel) thread #0 - timer://foo] INFO  mainRoute - 3rd loan return acknowledged? true
```