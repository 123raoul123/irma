# IRMA
The IRMA application is a java implementation of [An efficient self-blindable attribute-based
credential scheme](https://eprint.iacr.org/2017/115.pdf). The application uses the Relic toolkit for eliptic curve computation. The toolkit is compiled for the 638 barreto naehrig elliptic curve.

## Prerequisites
This application has the following dependencies. All these dependencies are included in the project and are compiled by and loaded when building the code. The dependencies are located in /app/libs.

* [Relic](https://github.com/relic-toolkit/relic)
  * A forked version of Relic is used to support [Relic edit](https://github.com/sietseringers/relic)
  * The 638 barreto naehrig elliptic curve is used (flag -DFP_PRIME=638)
  
* [Java Native Access](https://github.com/java-native-access/jna)
  * Java Native Access is used to enable java to use the relic dynamic c library.
  
## Project structure
* Relic
* IRMA

  
## Building


## Testing
