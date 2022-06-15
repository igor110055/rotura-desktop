# Rotura - decentralized exchange, wallet, plotting, and mining tool

![](https://github.com//btdex/btdex/workflows/BTDEX%20Build/badge.svg)
[![GPLv3](https://img.shields.io/badge/license-GPLv3-blue.svg)](LICENSE)

Rotura is a decentralized exchange system running on the [Rotura] blockchain.
It implements a unique non-custodial exchange method for cryptocurrencies and conventional fiat currencies based on [BlockTalk](https://github.com/jjos2372/blocktalk) Smart Contracts and Rotura on-chain encrypted messages.
The exchange method is serverless and fees are distributed among [Trade Token (TRT)](https://explore.burstcoin.ro/asset/12402415494995249540) holders.

Currently the following pairs are available with Rotura:
 - BTC
 - ARRR
 - BCH
 - BNB
 - BSV
 - DOGE
 - ETH
 - LTC
 - XMR
 - XLA

 
Additionally, any Rotura-based token can be listed instantly and traded.

## Download

Check the [releases](https://github.com/RoturaProtocol/rotura-desktop/releases) and get the latest one.

### Running on Windows
Just download the `rotura-version-win_x64.zip` [latest release](https://github.com/RoturaProtocol/rotura-desktop/releases) and extract it
to a folder you have write rights.
Double click on `rotura.exe` to start the application (it will create a file named `config.properties` inside the user [AppData](https://www.howtogeek.com/318177/what-is-the-appdata-folder-in-windows/)`/rotura` folder or use a file with that name if available on the same folder).

### Running on Linux

#### Ubuntu and other Debian-based distributions
Just download the `rotura_version_amd64.deb` [latest release](https://github.com/RoturaProtocol/rotura-desktop/releases) and install it.
The application `Rotura` will be available on the system (config file will go to `.config/rotura/` inside your home folder).

### Running on MacOS
Just download the `rotura-mac-version.zip` [latest release](https://github.com/RoturaProtocol/rotura-desktop/releases) and uncompress the app.
You can now run the app as usual (config file will go to `.config/rotura/` inside your home folder).

### General method without installing
Just download the `rotura-version-all.jar` [latest release](https://github.com/RoturaProtocol/rotura-desktop/releases) and copy it
to a folder you have write rights (it will create a file named `config.properties` with your account details as described for different operating systems above).
Run this jar file with Java 8 or 11 (the `xdg-utils` package is required to open your browser when necessary):

`java -jar rotura-version-all.jar`

## Translations
If you want to see Rotura on your own language or have suggestions on how to improve a translation, please join us at https://www.transifex.com/rotura/.

## Compile from source

Clone this repository code and run the gradle build (requires Java 8 to build):

```
$ git clone https://github.com/RoturaProtocol/rotura-desktop.git
$ cd rotura-desktop
$ ./gradlew release
```

The many resulting files will be found inside the `build` folder, like:

`build/libs/rotura-all.jar`

The Windows package will be found at:

`build/distributions/rotura-win_x64.zip`

## Running on testnet

Edit your `config.properties` file and add the following lines:

```
testnet=True
node=http\://nivbox.co.uk\:6876
```

## Minimum offer sizes

If you want to change the default minimum offer sizes, edit your `config.properties` for specific markets:

```
minOfferBTC=10000
minOfferETH=80000
```

## API

You can enable the API by adding the following line on your `config.properties` file:

```
apiPort=9000
```

You can choose a port number other than 9000. The config file is usually found on the same folder where the Rotura application is executed. If you installed a DEB (Linux) the config file is found at `~/.config/rotura`. The API will be available at `http://localhost:9000/api/v1/`, with the following endpoints:
 - `http://localhost:9000/api/v1/summary`
 - `http://localhost:9000/api/v1/ticker`
 - `http://localhost:9000/api/v1/orderbook/ROTURA_TRT` (or any other pair listed on the *summary*)
 - `http://localhost:9000/api/v1/trades/ROTURA_TRT` (or any other pair listed on the *summary*)

## Logging

By default, logging is disabled. Add/edit the following line on your `config.properties` to change the logging level:

```
logger=off
```

### Logging level

The same logging level is used to print log messages to the console and as well as to log file, possible levels are:

- `OFF` The highest possible log level. This is intended for disabling logging.
- `FATAL` Indicates server errors that cause premature termination. These logs are expected to be immediately visible on the command line that you used for starting the server.
- `ERROR` Indicates other runtime errors or unexpected conditions. These logs are expected to be immediately visible on the command line that you used for starting the server.
- `WARN` Indicates the use of deprecated APIs, poor use of API, possible errors, and other runtime situations that are undesirable or unexpected but not necessarily wrong. These logs are expected to be immediately visible on the command line that you used for starting the server.
- `INFO` Indicates important runtime events, such as server startup/shutdown. These logs are expected to be immediately visible on the command line that you used for starting the server . It is recommended to keep these logs to a minimum.
- `DEBUG` Provides detailed information on the flow through the system. This information is expected to be written to logs only. Generally, most lines logged by your application should be written as DEBUG logs.
- `TRACE` Provides additional details on the behavior of events and services. This information is expected to be written to logs only.<br>

## License
[GPL license](LICENSE)

## Author
john33899

Donation address: S-JJQS-MMA4-GHB4-4ZNZU
