# GATE API

GATE API is an extension of the GATE system that allows for communication with RESTful clients. 

## How to setup GATE API?

Please first read the official [GATE README](https://github.com/csware/si) to get the system up and running,
below are the additional steps involved to set up the GATE API.

### Installation steps of required software

### Authentication / Identity Provider
- GATE authenticates users both with SAML2.0 and LDAP identity providers, decide what SAML2.0 or LDAP identity provider to use
- Create a user with the identity provider chosen 

### Authorization Server
- Decide what OAuth 2.0 Authorization Server to use (e.g, [Keycloak](https://www.keycloak.org))
- Create and configure a public GATE client with the authorization server
- The GATE client must be configured to support the OAuth 2.0 device-code-flow
- Configure identity brokering for the authorization server with the identity provider of choice 
  - SAML2.0 involves exchanging the metadata config with the authorization server
  - LDAP involves providing the connection url of the LDAP server
  
### Configuration files

- `src/main/webapp/WEB-INF/web.xml` for the authorization server configurations
  - Adjust the `oauth2RealmUrl` here, to the url domain of your authorization server. (This is the issuer field in the /.well-known/openid-configuration file of your authorization server)
  - Get the publicKey of your authorization server. (The publickey is used by the authorization server to create and verify access tokens)
    - Adjust the `publicKeyFilePath` here
    
  
### Installation steps for GATE API

- Start the authorization server 
- Start Tomcat and deploy the built `.war` file


Now GATE API can be access using e.g. <http://localhost:8080/gate/api/{endpoint}> depending on your concrete local setup and the endpoint you want to access.
