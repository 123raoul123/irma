# IRMA
The IRMA application is a java implementation of [An efficient self-blindable attribute-based
credential scheme](https://eprint.iacr.org/2017/115.pdf). The application uses the Relic toolkit for eliptic curve computation. The toolkit is compiled for the 638 barreto naehrig elliptic curve.

## Prerequisites
This application has the following dependencies. All these dependencies are included in the project and are compiled by and loaded when building the code.

* [Relic](https://github.com/relic-toolkit/relic)
  * A forked version of Relic is used to support [Relic edit](https://github.com/sietseringers/relic)
  * The 638 barreto naehrig elliptic curve is used (flag -DFP_PRIME=638)
  * dylib version located in /resources.
  
* [Java Native Access](https://github.com/java-native-access/jna)
  * Java Native Access is used to enable java to use the relic dynamic c library.
  * JNA is automatically imported by gradle.
  
## Project structure
The source code in /src contains the following packages:

### Relic
Contains classes that are needed to interface with the relic toolkit.

### IRMA
Contains all the logic needed to perform the issue and show credential protocol. The package contains:

* Issuer.java
  * The issuer class implements the issuer role for the Issue protocol. Upon creation of the object it generates a private and a public key implemented in IssuerPrivateKey.java and IssuerPublicKey.java.
* User.java 
  * The user class implements the user role for both the Issue and ShowCredential protocol. Upon creation of the object it generates a privatekey and attributes, these are defined in UserPravateKey.java and Attributes.java.
* Verifier.java
  * The verifier class implements the verifier role used in the ShowCredential protocol. Upon creation of the object it obtains the public key of the Issuer.

### Issue
The issue package contains all the data classes used in the Issue protocol.

### ShowCredential
The ShowCredential package contains all the data classes used in the ShowCredential protocol.
  
## Building


## Testing
The main class is located at /relic/HelloWorld.java. This class tests a single run of both protocols with n attributes.

