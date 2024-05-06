#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QQmlContext>
#include <QGeoPositionInfo>
#include <QGeoPositionInfoSource>
#include <QGeoSatelliteInfoSource>
#include <QDebug>

int main(int argc, char *argv[])
{
    QGuiApplication app(argc, argv);

    QQmlApplicationEngine engine;
    engine.load(QUrl(QStringLiteral("qrc:/main.qml")));


    // Создаем источник информации о местоположении
    QGeoPositionInfoSource *source = QGeoPositionInfoSource::createDefaultSource(nullptr);
    if (source) {
        // Подключаемся к обновлениям местоположения
        source->setUpdateInterval(1000); // Обновление каждую секунду
        source->startUpdates();

        QMetaObject::invokeMethod(engine.rootObjects().first(), "setIsGPSEnabled", Q_ARG(QVariant, "GPS доступен"));

        // Обработчик обновлений местоположения
        QObject::connect(source, &QGeoPositionInfoSource::positionUpdated, [&](const QGeoPositionInfo &info) {
            if (info.isValid()) {
                // Получаем координаты
                qreal latitude = info.coordinate().latitude();
                qreal longitude = info.coordinate().longitude();
                qreal altitude = info.coordinate().altitude();

                // Отправляем координаты в QML
                QMetaObject::invokeMethod(engine.rootObjects().first(), "updateCoordinates",
                                          Q_ARG(QVariant, latitude),
                                          Q_ARG(QVariant, longitude),
                                          Q_ARG(QVariant, altitude));
            }
        });
    }
    else {
        QMetaObject::invokeMethod(engine.rootObjects().first(), "setIsGPSEnabled", Q_ARG(QVariant, "GPS не доступен"));
    }

    // Создаем источник информации о спутниках
    QGeoSatelliteInfoSource *satelliteSource = QGeoSatelliteInfoSource::createDefaultSource(nullptr);
    if (satelliteSource) {
        // Подключаемся к обновлениям информации о спутниках
        satelliteSource->setUpdateInterval(1000); // Обновление каждую секунду
        satelliteSource->startUpdates();

        // Обработчик обновлений информации о спутниках
        QObject::connect(satelliteSource, &QGeoSatelliteInfoSource::satellitesInViewUpdated, [&](const QList<QGeoSatelliteInfo> &satellites) {
            // Отправляем информацию о спутниках в QML
            QString satelliteInfo = "Спутники:";
            if (!satellites.isEmpty()) {
                for (int i = 0; i < satellites.size(); ++i) {
                    satelliteInfo += "\nСпутник ";
                    satelliteInfo += QString::number(i + 1);
                    satelliteInfo += ", ID=";
                    satelliteInfo += QString::number(satellites[i].satelliteIdentifier());
                    satelliteInfo += ", Сила сигнала=";
                    satelliteInfo += QString::number(satellites[i].signalStrength());
                    satelliteInfo += "dBHz, Азимут=";
                    satelliteInfo += QString::number(satellites[i].attribute(QGeoSatelliteInfo::Azimuth));
                    satelliteInfo += "°, Высота=";
                    satelliteInfo += QString::number(satellites[i].attribute(QGeoSatelliteInfo::Elevation));
                    satelliteInfo += "°";
                }
            }

            QMetaObject::invokeMethod(engine.rootObjects().first(), "updateSatellites", Q_ARG(QVariant, satelliteInfo));
        });
    }

    return app.exec();
}

