# PhilipsHue

- [PhilipsHue](#philipshue)
  - [Présentation](#présentation)
  - [La maquette](#la-maquette)
  - [Application](#application)
  - [OpenHue API](#openhue-api)
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

## Boutique

- [Philips Hue](https://www.philips-hue.com/fr-fr/products/)
- [Amazon](https://www.amazon.fr/stores/PhilipsHue/page/1D8D599B-E9F3-4C60-971C-276FC75625AB)

## Auteurs

- Jérôme BEAUMONT <<beaumontlasalle84@gmail.com>>
- Thierry VAIRA <<thierry.vaira@gmail.com>>

---
&copy; 2024 LaSalle Avignon
