# PhilipsHue

- [PhilipsHue](#philipshue)
  - [Présentation](#présentation)
  - [La maquette](#la-maquette)
  - [Application](#application)
  - [OpenHue API](#openhue-api)
  - [Postman](#postman)
  - [Boutique](#boutique)
  - [Auteurs](#auteurs)

---

## Présentation

Philips fut fondé par Gerhard et Frederik Philips à Eindhoven en 1891 avec la production d'ampoules à filament de charbon.

Philips Hue est une sous-marque qui fait partie de la famille Philips et elle est dédiée à l’éclairage connecté.

> La sous-marque Philips Lighting est devenu [Signify](https://www.signify.com/fr-fr). Les produits Philips Hue existent depuis 2013.

Pionnier dans le secteur de l'éclairage connecté, Philips Hue est devenu un écosystème propriétaire de gestion de produits connectés ([Domotique](https://fr.wikipedia.org/wiki/Domotique)) communiquant en [ZigBee](https://fr.wikipedia.org/wiki/ZigBee).

Pionnier dans le secteur de l'éclairage connecté, Philips Hue dispose d'une gamme importante d'ampoules et de luminaires qui communiquent en [ZigBee](https://fr.wikipedia.org/wiki/ZigBee).

> Les lumières Philips Hue peuvent aussi être contrôlées via [Bluetooth](https://fr.wikipedia.org/wiki/Bluetooth) pour obtenir un ensemble limité de fonctionnalités.

L'utilisation du [ZigBee](https://fr.wikipedia.org/wiki/ZigBee) nécessite la présence d'une passerelle nommée « pont de connexion » (_Hue Bridge_). Un pont (ou _hub_) Hue Bridge peut supporter jusqu’à 50 ampoules.

> Le pont Philips Hue Bridge prend en charge [Matter](https://fr.wikipedia.org/wiki/Matter_(standard)).

## La maquette

La maquette de test est composé :

- d'une passerelle _Hue Bridge_

![](./images/hue-bridge.jpeg)

- de trois ampoules Hue White and Color Ambiance A60 E27 9W

![](./images/ampoule.jpeg)

- d'un Hue Motion Sensor détecteur de mouvement

![](./images/motion-sensor.jpeg)

- d'un Hue Dimmer switch télécommande nomade et variateur de Lumière

![](./images/dimmer-switch.jpeg)

## Application

L'application principale du système Philips Hue vous permet d'allumer et d'éteindre vos lumières, de créer des automatisations et des minuteries, de synchroniser vos éclairages avec votre TV et votre musique, de contrôler votre système de sécurité connectée pour la maison.

Liens :

- [Android](https://play.google.com/store/apps/details?id=com.philips.lighting.hue2)
- [iOS](https://apps.apple.com/us/app/philips-hue-gen-2/id1055281310?ls=1)

Philips Hue fonctionne avec de nombreux appareils, plates-formes et assistants pour une maison connectée :

- [Amazon Alexa](https://www.philips-hue.com/fr-fr/explore-hue/works-with/amazon-alexa)
- [Assistant Google](https://www.philips-hue.com/fr-fr/explore-hue/works-with/the-google-assistant)
- [Apple Home et Siri](https://www.philips-hue.com/fr-fr/explore-hue/works-with/apple-homekit)

> Les applications compatibles avec Philips Hue : https://www.philips-hue.com/fr-fr/explore-hue/works-with

## OpenHue API

Liens :

- [OpenHue](https://www.openhue.io/api/openhue-api) et [API OpenHue](https://github.com/openhue/openhue-api)
- [OpenHue CLI](https://www.openhue.io/cli/openhue-cli)

OpenHue API est un projet open source qui fournit une spécification complète pour l'API Philips Hue REST.

Télécharger le fichier de spécification `openapi.yaml` :

```bash
$ wget -c --output-document openapi.yaml 'https://api.redocly.com/registry/bundle/openhue/openhue/v2/openapi.yaml?branch=main&download'

$ ls -l openapi.yaml
-rw-rw-r-- 1 tv tv 120480 févr.  6 07:32 openapi.yaml
```

Il existe un outil en ligne de commande [OpenHue CLI](https://www.openhue.io/cli/openhue-cli) (https://www.openhue.io/cli/openhue-cli) disponible via [brew](https://brew.sh/fr/) ou [docker](https://hub.docker.com/r/openhue/cli).

- [brew](https://brew.sh/fr/)

```bash
$ /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

$ brew tap openhue/cli
$ brew install openhue-cli
```

```bash
$ openhue help

openhue controls your Philips Hue lighting system

    Find more information at: https://www.openhue.io/cli

Usage:
  openhue [command]

Configuration
  config      Manual openhue CLI setup
  discover    Hue Bridge discovery
  setup       Automatic openhue CLI setup

Philips Hue
  get         Display one or many resources
  set         Set specific features on resources

Additional Commands:
  completion  Generate the autocompletion script for the specified shell
  help        Help about any command
  version     Print the version information

Flags:
  -h, --help   help for openhue

Use "openhue [command] --help" for more information about a command.
```


```bash
$ openhue setup
[OK] Found Hue Bridge with IP '192.168.52.182'
[..] Please push the button on your Hue Bridge
......
[OK] Successfully paired openhue with your Hue Bridge!
[OK] Configuration saved in file $HOME/.openhue/config.yaml
```

La clé (_key_) de l'API se trouve dans le fichier `$HOME/.openhue/config.yaml` :

```bash
$ cat $HOME/.openhue/config.yaml
bridge: 192.168.52.182
key: LiG7XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXvZ8f
```

```bash
$ openhue get lights
```

- [docker](https://hub.docker.com/r/openhue/cli)

```bash
$ docker pull openhue/cli

$ docker run -v "${HOME}/.openhue:/.openhue" --rm -it --name=openhue openhue/cli help
...

$ docker run -v "${HOME}/.openhue:/.openhue" --rm -it --name=openhue openhue/cli setup --bridge 192.168.52.182
...

$ docker run -v "${HOME}/.openhue:/.openhue" --rm -it --name=openhue openhue/cli get lights
```


## Postman

[Postman](https://fr.wikipedia.org/wiki/Postman_(logiciel)) est une plateforme pour la construction, l'utilisation et les tests d'API.

Lien : https://www.postman.com/

Télécharger et installer la version de [Postman](https://dl.pstmn.io/download/latest/linux_64) pour Linux : https://dl.pstmn.io/download/latest/linux_64

Ou à partir du gestionnaire de paquets _snap_ :

```bash
$ sudo snap install postman
```

![](./images/demarrer-postman-ubuntu.png)

![](./images/postman-ubuntu.png)

Créer un compte si nécessaire (campus-btssn-avignon).

Il existe aussi un outil en ligne de commande Postman CLI :

```bash
$ curl -o- "https://dl-cli.pstmn.io/install/linux64.sh" | sh
```

> Il existe une extension pour Visual Studio Code : https://marketplace.visualstudio.com/items?itemName=Postman.postman-for-vscode

Créer un nouvel environnement avec ces deux variables :

- `baseUrl` avec comme valeur initiale et courante l'adresse du pont Hue, par exemple : `https://192.168.52.182`
- `apiKey`

![](./images/postman-new-environment.png)



## Boutique

- [Philips Hue](https://www.philips-hue.com/fr-fr/products/)
- [Amazon](https://www.amazon.fr/stores/PhilipsHue/page/1D8D599B-E9F3-4C60-971C-276FC75625AB)

## Auteurs

- Jérôme BEAUMONT <<beaumontlasalle84@gmail.com>>
- Thierry VAIRA <<thierry.vaira@gmail.com>>

---
&copy; 2024 LaSalle Avignon
