Cloudstate Gatling Loadtest example
===================================

Running the shopping cart load test

In sbt

`gatling-it:testOnly shoppingcart.ShoppingCartSimulation1`

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

Run the shopping cart simulation at the command line

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
