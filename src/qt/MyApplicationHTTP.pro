QT       += core gui network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++11

DEFINES += QT_DEPRECATED_WARNINGS

SOURCES += MyApplicationHTTP.cpp main.cpp


HEADERS += MyApplicationHTTP.h

COPIES += configuration
configuration.files = config.ini
configuration.path = $$OUT_PWD/
configuration.base = $$PWD/

CONFIG(release, debug|release):DEFINES+=QT_NO_DEBUG_OUTPUT
