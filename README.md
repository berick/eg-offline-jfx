# eg-offline-jfx

Evergreen Offline UI in Java / JavaFX

## Running From Source (Ubuntu Version)

```sh
sudo apt install openjdk-11-jdk libopenjfx-java maven

git clone github.com:berick/eg-offline-jfx

cd eg-offline-jfx

mvn javafx:run

```

## Screens

### Checkout Tab

![Checkout Tab](./src/main/assets/images/eg-offline-jfx-checkout.png)

### Pending Transactions Tab

![Transactions Tab](./src/main/assets/images/eg-offline-jfx-transactions.png)


## Java with Local Certificate

https://medium.com/expedia-group-tech/how-to-import-public-certificates-into-javas-truststore-from-a-browser-a35e49a806dc


```sh
# Roughly...
# password defaults to 'changeit'
keytool -keystore /etc/ssl/certs/java/cacerts -import -alias friendly-cert-name -file /path/to/my/cert.cer
```

## Development Notes

### TODO

* string bundles
* shared base page
* all async net
* Context class for shared data
* baked in progress indicator along bottom 
* show progress during network connectivity check.
* add skip option to network check
* common exception type

### Startup Sequence

* load default config from sqlite (host / workstation / org unit)
* Show host select option / allow free text
* Check network connectivity / display in status
* Fetch org unit tree
* Show login form:
  * show workstation selector if any are stored (based on selected host)
  * IF NETWORK:
    * show username, password inputs
* User submits login
  * IF NETWORK
    * Login + refresh data
    * If no workstation was selected, go to workstation register form
  * IF NOT NETWORK
    * Load cached data
* Proceed to checkout UI

