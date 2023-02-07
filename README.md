# DID4DCAT App

## Prerequisites

- Java >= 11
- Maven >= 3

## Installation

Create a configuration file and fill it.

```bash
   $ cp config/config.sample.json config/config.json 
```


Build the project by using Maven and run the application. The generated _jar_ can be found in the `target` directory.

```bash
   $ mvn clean package
   $ java -jar target/did4dcat-app-fat.jar
```

## Configuration

| Name        | Description                                        | Type   |
|-------------|----------------------------------------------------|--------|
| PORT        | The port for the service                           | number |
| CLI_PORT    | The port for the CLI                               | number |
| CA_PEM_FILE | Absolute or relative path to the CA PEM file       | string |
| CA_URL      | URL to the CA service                              | string |
| CHANNEL     | Name of the channel                                | string |
| PEER_CONFIG | Absolute or relative path to the connection config | string |
| WALLET_DIR  | Directory of the wallet                            | string |
| ORG_MSP  | MSP for the users                                  | string |
| ORG_AFFILIATION  | Affiliation for the users                          | string |


## Tips

For local development you might need to set the following ENV variable to true, so the peers can discovery each other. 

```bash
   $ export ORG_HYPERLEDGER_FABRIC_SDK_SERVICE_DISCOVERY_AS_LOCALHOST=true
```
