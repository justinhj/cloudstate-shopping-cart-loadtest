Cloudstate Gatling Loadtest example
===================================

# What is this?

This is a sample of using [Gatling](https://gatling.io/) to run a load test against Cloudstate services. [Cloudstate](https://cloudstate.io/) is a specification, protocol, and reference implementation for providing distributed state management patterns suitable for Serverless computing.

# Getting started

If you deploy the sample shopping cart [js-shopping-cart](https://github.com/cloudstateio/cloudstate/tree/master/samples/js-shopping-cart) you can use this repository to run a load test against it, that tests various functions. 

# Running the shopping cart load test

In sbt

`gatling-it:testOnly shoppingcart.ShoppingCartSimulation1`

# Running a load test against your own service

1. Add the protobuf files for your service API in `src/main/protobuf`
2. Copy the folder `src/it/scala/shoppingcart` and rename it to match your service
3. Make a gatling test copying the example from `ShoppingCartSimulation1.scala` that calls your endpoints

You may need to consult the Gatling documumentation to explore its full range of functionality.

# Technical notes

This project uses Gatling as a plugin along with grpc to handle the protobuf compilation and gatling-grpc so that we can write Gatling load tests over gRPC instead of HTTP.

Run all simulations
-------------------

```bash
> gatling:test
```

Run the shopping cart simulation from sbt
-----------------------------------------

```bash
> gatling-it:testOnly shoppingcart.ShoppingCartSimulation1
```

Run the shopping cart simulation at the command line and direct to a particular server...

```
GATLING_PORT=[PORT] GATLING_USERS=[NUM USERS] GATLING_HOST=[YOUR HOST] sbt gatling-it:testOnly shoppingcart.ShoppingCartSimulation1
```

List all tasks
--------------------

```bash
> tasks gatling -v
```

## References

https://github.com/gatling/gatling-sbt-plugin-demo
https://github.com/phiSgr/gatling-grpc
https://medium.com/@georgeleung_7777/a-demo-of-gatling-grpc-bc92158ca808
