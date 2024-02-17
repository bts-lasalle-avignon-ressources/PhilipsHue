/**
 * @file MyApplicationHTTP.cpp
 * @brief Définition de la classe MyApplicationHTTP
 */

#include "MyApplicationHTTP.h"
#include <QNetworkReply>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>

/**
 * @brief Constructeur de la classe MyApplicationHTTP
 * @fn MyApplicationHTTP::MyApplicationHTTP
 * @param parent nullptr pour la fenêtre comme fenêtre principale de l'application
 */
MyApplicationHTTP::MyApplicationHTTP(QWidget* parent) :
    QWidget(parent), accesReseau(new QNetworkAccessManager(this))
{
    qDebug() << Q_FUNC_INFO;
    chargerParametres();
    initialiserGUI();
    initialiserSignauxSlots();
}

/**
 * @brief Destructeur de la classe MyApplicationHTTP
 *
 * @fn MyApplicationHTTP::~MyApplicationHTTP
 * @details Libère les ressources de l'application
 */
MyApplicationHTTP::~MyApplicationHTTP()
{
    enregistrerParametres();
    qDebug() << Q_FUNC_INFO;
}

/**
 * @brief Charge les paramètres de l'application
 *
 * @fn MyApplicationHTTP::chargerParametres
 * @details Charge les paramètres à partir du fichier config.ini
 */
void MyApplicationHTTP::chargerParametres()
{
    QSettings parametres(CONFIGURATION_APPLICATION, QSettings::IniFormat);
    adresseIPPontHue = parametres.value("Hue/adresseIPPontHue").toString();
    qDebug() << Q_FUNC_INFO << "adresseIPPontHue" << adresseIPPontHue;
    hueApplicationKey = parametres.value("Hue/hueApplicationKey").toString();
    qDebug() << Q_FUNC_INFO << "hueApplicationKey" << hueApplicationKey;
}

/**
 * @brief Enregistre les paramètres de l'application
 *
 * @fn MyApplicationHTTP::enregistrerParametres
 * @details Enregistre les paramètres à partir du fichier config.ini
 */
void MyApplicationHTTP::enregistrerParametres()
{
    QSettings parametres(CONFIGURATION_APPLICATION, QSettings::IniFormat);
    parametres.setValue(QString("Hue") + "/adresseIPPontHue", adresseIPPontHue);
    qDebug() << Q_FUNC_INFO << "adresseIPPontHue" << adresseIPPontHue;
    parametres.setValue(QString("Hue") + "/hueApplicationKey", hueApplicationKey);
    qDebug() << Q_FUNC_INFO << "hueApplicationKey" << hueApplicationKey;
}

/**
 * @brief Initialise l'interface graphique
 *
 * @fn MyApplicationHTTP::initialiserGUI
 * @details Génère la page graphique d'interface
 */
void MyApplicationHTTP::initialiserGUI()
{
    initialiserWidgets();
    initialiserFenetrePrincipale();
}

/**
 * @brief Initialise les widgets
 *
 * @fn MyApplicationHTTP::initialiserWidgets
 * @details Initialise les widgets de l'IHM.
 */
void MyApplicationHTTP::initialiserWidgets()
{
    layoutPrincipal = new QVBoxLayout;
    layoutLabels    = new QVBoxLayout;
    layoutBoutons   = new QHBoxLayout;

    boutonDecouvrir    = new QPushButton("Découvrir", this);
    boutonAuthentifier = new QPushButton("Authentifier", this);
    boutonLister       = new QPushButton("Lister", this);
    boutonEteindre     = new QPushButton("Éteindre", this);
    boutonAllumer      = new QPushButton("Allumer", this);
    if(!adresseIPPontHue.isEmpty())
    {
        boutonAuthentifier->setEnabled(true);
    }
    else
    {
        boutonAuthentifier->setEnabled(false);
    }
    if(!hueApplicationKey.isEmpty())
    {
        boutonLister->setEnabled(true);
    }
    else
    {
        boutonLister->setEnabled(false);
    }
    boutonEteindre->setEnabled(false);
    boutonAllumer->setEnabled(false);
    urlRequete = new QLabel(this);
    urlRequete->setText("Philips Hue");
    reponseEtat = new QLabel(this);
    // reponseJson = new QLabel(this);
    reponseJson = new QTextEdit(this);
    reponseJson->setReadOnly(true);
}

/**
 * @brief Initialise les signaux et les slots
 *
 * @fn MyApplicationHTTP::initialiserSignauxSlots
 * @details Initialise les connects des signaux et des slots
 */
void MyApplicationHTTP::initialiserSignauxSlots()
{
    connect(boutonDecouvrir, SIGNAL(clicked(bool)), this, SLOT(decouvrirPontHue()));
    connect(boutonAuthentifier, SIGNAL(clicked(bool)), this, SLOT(authentifierHue()));
    connect(boutonLister, SIGNAL(clicked(bool)), this, SLOT(listerEclairages()));
    connect(boutonEteindre, SIGNAL(clicked(bool)), this, SLOT(eteindreEclairages()));
    connect(boutonAllumer, SIGNAL(clicked(bool)), this, SLOT(allumerEclairages()));
    connect(accesReseau,
            SIGNAL(finished(QNetworkReply*)),
            this,
            SLOT(traiterReponseHue(QNetworkReply*)));
}

/**
 * @brief Initialise la fenêtre principale
 * @fn MyApplicationHTTP::initialiserFenetrePrincipale
 */
void MyApplicationHTTP::initialiserFenetrePrincipale()
{
    layoutLabels->addWidget(urlRequete);
    layoutLabels->addWidget(reponseEtat);
    layoutLabels->addWidget(reponseJson);
    layoutLabels->addStretch();
    layoutBoutons->addWidget(boutonDecouvrir);
    layoutBoutons->addWidget(boutonAuthentifier);
    layoutBoutons->addWidget(boutonLister);
    layoutBoutons->addWidget(boutonEteindre);
    layoutBoutons->addWidget(boutonAllumer);
    layoutPrincipal->addLayout(layoutLabels);
    layoutPrincipal->addLayout(layoutBoutons);

    setLayout(layoutPrincipal);
    // QRect screenGeometry = QGuiApplication::primaryScreen()->availableGeometry();
    // resize(screenGeometry.width(), screenGeometry.height());
    resize(820, 820);
}

void MyApplicationHTTP::decouvrirPontHue()
{
    qDebug() << Q_FUNC_INFO;
    QString         api = "https://discovery.meethue.com/";
    QUrl            url = QUrl(api);
    QNetworkRequest requeteGet;
    requeteGet.setUrl(url);
    requeteGet.setRawHeader("Content-Type", "application/json");
    qDebug() << Q_FUNC_INFO << "url" << url;
    requeteApi = api;
    urlRequete->setText(api);
    reponseReseau = accesReseau->get(requeteGet);
}

void MyApplicationHTTP::authentifierHue()
{
    qDebug() << Q_FUNC_INFO;
    QString         api = "https://" + adresseIPPontHue + "/api";
    QNetworkRequest requetePost;
    QUrl            url  = QUrl(api);
    QByteArray      json = "{\"devicetype\": \"ClientHTTPHue\",\"generateclientkey\": true}";
    requetePost.setUrl(url);
    requetePost.setHeader(QNetworkRequest::ContentTypeHeader, "application/x-www-form-urlencoded");
    requetePost.setRawHeader("Content-Type", "application/json");
    requetePost.setRawHeader("Content-Length", QByteArray::number(json.size()));
    requetePost.setRawHeader("Accept", "application/json");
    qDebug() << Q_FUNC_INFO << "url" << url << "json" << json;
    requeteApi = api;
    urlRequete->setText(api);
    QSslConfiguration conf = requetePost.sslConfiguration();
    conf.setPeerVerifyMode(QSslSocket::VerifyNone);
    requetePost.setSslConfiguration(conf);
    accesReseau->post(requetePost, json);
}

void MyApplicationHTTP::listerEclairages()
{
    qDebug() << Q_FUNC_INFO;
    QString         api = "https://" + adresseIPPontHue + "/clip/v2/resource/light";
    QUrl            url = QUrl(api);
    QNetworkRequest requeteGet;
    requeteGet.setUrl(url);
    requeteGet.setRawHeader("Content-Type", "application/json");
    requeteGet.setRawHeader("hue-application-key", hueApplicationKey.toLocal8Bit());
    qDebug() << Q_FUNC_INFO << "url" << url;
    requeteApi = api;
    urlRequete->setText(api);
    QSslConfiguration conf = requeteGet.sslConfiguration();
    conf.setPeerVerifyMode(QSslSocket::VerifyNone);
    requeteGet.setSslConfiguration(conf);
    accesReseau->get(requeteGet);
}

void MyApplicationHTTP::eteindreEclairages()
{
    qDebug() << Q_FUNC_INFO;
    for(int i = 0; i < idEclairages.size(); i++)
    {
        QString         id  = idEclairages.at(i);
        QString         api = "https://" + adresseIPPontHue + "/clip/v2/resource/light/" + id;
        QNetworkRequest requetePut;
        QUrl            url  = QUrl(api);
        QByteArray      json = "{\"on\":{\"on\": false}}";
        requetePut.setUrl(url);
        requetePut.setRawHeader(QByteArray("Content-Type"), QByteArray("application/json"));
        requetePut.setRawHeader(QByteArray("Accept"), QByteArray("application/json"));
        requetePut.setRawHeader(QByteArray("hue-application-key"), hueApplicationKey.toLocal8Bit());
        qDebug() << Q_FUNC_INFO << "url" << url << "json" << json;
        requeteApi = api;
        urlRequete->setText(api);
        QSslConfiguration conf = requetePut.sslConfiguration();
        conf.setPeerVerifyMode(QSslSocket::VerifyNone);
        requetePut.setSslConfiguration(conf);
        accesReseau->put(requetePut, json);
    }
}

void MyApplicationHTTP::allumerEclairages()
{
    qDebug() << Q_FUNC_INFO;
    for(int i = 0; i < idEclairages.size(); i++)
    {
        QString         id  = idEclairages.at(i);
        QString         api = "https://" + adresseIPPontHue + "/clip/v2/resource/light/" + id;
        QNetworkRequest requetePut;
        QUrl            url  = QUrl(api);
        QByteArray      json = "{\"on\":{\"on\": true}}";
        requetePut.setUrl(url);
        requetePut.setRawHeader(QByteArray("Content-Type"), QByteArray("application/json"));
        requetePut.setRawHeader(QByteArray("Accept"), QByteArray("application/json"));
        requetePut.setRawHeader(QByteArray("hue-application-key"), hueApplicationKey.toLocal8Bit());
        qDebug() << Q_FUNC_INFO << "url" << url << "json" << json;
        requeteApi = api;
        urlRequete->setText(api);
        QSslConfiguration conf = requetePut.sslConfiguration();
        conf.setPeerVerifyMode(QSslSocket::VerifyNone);
        requetePut.setSslConfiguration(conf);
        accesReseau->put(requetePut, json);
    }
}

/**
 * @brief Slot qui traite les réponses renvoyées par le pont Hue
 * @fn MyApplicationHTTP::traiterReponseHue
 */
void MyApplicationHTTP::traiterReponseHue(QNetworkReply* reponse)
{
    if(reponse->error() != QNetworkReply::NoError)
    {
        qDebug() << Q_FUNC_INFO << "erreur" << reponse->error();
        qDebug() << Q_FUNC_INFO << "erreur" << reponse->errorString();
        reponseEtat->setText(reponse->errorString());
        return;
    }
    QByteArray donneesReponse = reponse->readAll();
    qDebug() << Q_FUNC_INFO << "donneesReponse" << donneesReponse;
    qDebug() << Q_FUNC_INFO << "donneesReponse" << donneesReponse.size();
    reponseJson->append(donneesReponse);

    QJsonDocument documentJson = QJsonDocument::fromJson(donneesReponse);

    if(requeteApi.contains("discovery"))
    {
        /*
            [
                {
                    "id":"ecb5fafffe01c1b5",
                    "internalipaddress":"192.168.52.16",
                    "port":443
                }
            ]
        */
        QJsonArray  json          = documentJson.array();
        QJsonObject payloadFields = json.at(0).toObject();
        adresseIPPontHue          = payloadFields["internalipaddress"].toString();
        qDebug() << Q_FUNC_INFO << "adresseIPPontHue" << adresseIPPontHue;

        if(!adresseIPPontHue.isEmpty())
        {
            boutonAuthentifier->setEnabled(true);
        }
        else
        {
            boutonAuthentifier->setEnabled(false);
            boutonLister->setEnabled(false);
            boutonEteindre->setEnabled(false);
            boutonAllumer->setEnabled(false);
        }
        requeteApi.clear();
    }
    else if(requeteApi.contains("api"))
    {
        /*
            [{"error":{"type":101,"address":"","description":"link button not pressed"}}]
            [{"success":{"username":"XXXXXXXX","clientkey":"YYYYYYYY"}}]
        */
        QJsonArray  json          = documentJson.array();
        QJsonObject payloadFields = json.at(0).toObject();
        if(payloadFields.contains("error"))
        {
            QJsonObject erreur = payloadFields["error"].toObject();
            qDebug() << Q_FUNC_INFO << "type" << erreur["type"].toInt();
            qDebug() << Q_FUNC_INFO << "type" << erreur["description"].toString();
            reponseEtat->setText(erreur["description"].toString());
        }

        if(payloadFields.contains("success"))
        {
            QJsonObject succes = payloadFields["success"].toObject();
            qDebug() << Q_FUNC_INFO << "username" << succes["username"].toString();
            qDebug() << Q_FUNC_INFO << "clientkey" << succes["clientkey"].toString();
            hueApplicationKey = succes["username"].toString();
        }
        requeteApi.clear();
    }
    else if(requeteApi.endsWith("light"))
    {
        /*
            {
                "errors": [],
                "data": [
                    {
                        "id": "5d4ba384-f49c-45a1-a68f-874a5008b967",
                        "id_v1": "/lights/3",
                        ...
                        "type": "light"
                    },
                    {
                        "id": "a40100ed-eedf-4f8b-b5f9-271b790edb2f",
                        "id_v1": "/lights/2",
                        ...
                        "type": "light"
                    },
                    {
                        "id": "f4d936da-441d-46d5-bf58-0089ad4a9aa9",
                        "id_v1": "/lights/1",
                        ...
                        "type": "light"
                    }
                ]
            }
        */
        QJsonObject json          = documentJson.object();
        QJsonArray  payloadFields = json["data"].toArray();

        idEclairages.clear();
        for(int i = 0; i < payloadFields.count(); i++)
        {
            QJsonObject eclairage = payloadFields.at(i).toObject();
            QString     id        = eclairage["id"].toString();
            qDebug() << Q_FUNC_INFO << "id" << id;
            qDebug() << Q_FUNC_INFO << "type" << eclairage["type"].toString();
            if(eclairage["type"].toString() == "light")
                idEclairages.push_back(id);
        }
        if(idEclairages.size() > 0)
        {
            boutonEteindre->setEnabled(true);
            boutonAllumer->setEnabled(true);
        }
        else
        {
            boutonEteindre->setEnabled(false);
            boutonAllumer->setEnabled(false);
        }
        requeteApi.clear();
    }
    else if(requeteApi.contains("light/"))
    {
        /*
            {"data":[{"rid":"f4d936da-441d-46d5-bf58-0089ad4a9aa9","rtype":"light"}],"errors":[]}
        */
        requeteApi.clear();
    }
}
