#ifndef MyApplicationHTTP_H
#define MyApplicationHTTP_H

#include <QtWidgets>
#include <QObject>
#include <QtNetwork/QNetworkAccessManager>
#include <QList>

/**
 * @def CONFIGURATION_APPLICATION
 * @brief Le nom du fichier de configuration
 */
#define CONFIGURATION_APPLICATION "config.ini"

/**
 * @class MyApplicationHTTP
 * @brief La fenêtyre principale de l'application
 */
class MyApplicationHTTP : public QWidget
{
    Q_OBJECT

  private:
    // Widgets
    // Les layouts
    QVBoxLayout* layoutPrincipal;
    QVBoxLayout* layoutLabels;
    QHBoxLayout* layoutBoutons;
    // Les boutons
    QPushButton* boutonDecouvrir;
    QPushButton* boutonAuthentifier;
    QPushButton* boutonLister;
    QPushButton* boutonEteindre;
    QPushButton* boutonAllumer;
    // Les labels
    QLabel* urlRequete;
    QLabel* reponseEtat;
    // QLabel* reponseJson;
    QTextEdit* reponseJson;
    // Communication HTTP
    QNetworkAccessManager* accesReseau;
    QNetworkReply*         reponseReseau;
    QString                requeteApi;
    QString                adresseIPPontHue;
    QString                hueApplicationKey;
    QList<QString>         idEclairages;

    void chargerParametres();
    void enregistrerParametres();

  public:
    MyApplicationHTTP(QWidget* parent = nullptr);
    ~MyApplicationHTTP();

    void initialiserGUI();
    void initialiserWidgets();
    void initialiserSignauxSlots();
    void initialiserFenetrePrincipale();

  public slots:
    void decouvrirPontHue();
    void authentifierHue();
    void listerEclairages();
    void eteindreEclairages();
    void allumerEclairages();
    void traiterReponseHue(QNetworkReply* reponseStation);
};

#endif // MyApplicationHTTP_H
