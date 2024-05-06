#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include "qgeopositioninfo.h"
#include <QGridLayout>
#include <QLineEdit>
#include <QMainWindow>
#include <QPushButton>
#include <QAccelerometer>
#include <QAccelerometerReading>
#include <QTextEdit>
#include <QGeoSatelliteInfo>

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();
    QWidget *MainWidget = new QWidget;
    QGridLayout *MainLayout = new QGridLayout;
    QPushButton *But_1 = new QPushButton;
    QLineEdit *LineX = new QLineEdit;
    QLineEdit *LineY = new QLineEdit;
    QLineEdit *LineZ = new QLineEdit;
    QLineEdit *Position = new QLineEdit;
    QSensor *accel = new QSensor("QAccelerometer");
    QPushButton *but = new QPushButton("read Lipovik's file");
    QTextEdit *GPSFile = new QTextEdit;
private:
    QTextEdit *textEdit;
    QLineEdit *satelliteText;
    void recognizeMove(QSensorReading *reading);
    qreal x;
    qreal y;
    qreal z;
private slots:
    void changeValues();
    void positionUpdated(const QGeoPositionInfo &info);
    void satelliteUpdated(const QList<QGeoSatelliteInfo> &info);
    void readFromFille();
};
#endif // MAINWINDOW_H
