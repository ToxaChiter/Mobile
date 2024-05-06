#include "mainwindow.h"
#include <QGeoSatelliteInfoSource>
#include <QGridLayout>
#include <QLabel>
#include <ostream>
#include <QFile>
#include <QGeoPositionInfoSource>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    accel->start();
    LineX->setReadOnly(true);
    LineY->setReadOnly(true);
    LineZ->setReadOnly(true);
    Position->setReadOnly(true);
    connect(accel, &QAccelerometer::readingChanged, this, &MainWindow::changeValues);
    textEdit = new QTextEdit;
    satelliteText = new QLineEdit;
    satelliteText->setReadOnly(true);
    textEdit->setReadOnly(true);
    textEdit->setMaximumHeight(60);
    GPSFile->setReadOnly(true);
    QGeoPositionInfoSource *source = QGeoPositionInfoSource::createDefaultSource(this);
    if (source) {
        connect(source, SIGNAL(positionUpdated(QGeoPositionInfo)),
                this, SLOT(positionUpdated(QGeoPositionInfo)));
        source->startUpdates();
        source->setUpdateInterval(20000);
    }
    QGeoSatelliteInfoSource *satelliteSource = QGeoSatelliteInfoSource::createDefaultSource(this);
    if (satelliteSource) {
        connect(satelliteSource, SIGNAL(satellitesInViewUpdated(QList<QGeoSatelliteInfo>)),
                this, SLOT(satelliteUpdated(QList<QGeoSatelliteInfo>)));
        satelliteSource->startUpdates();
    }
    connect(but, &QPushButton::clicked, this, &MainWindow::readFromFille);
    MainLayout->addWidget(LineX, 0, 0);
    MainLayout->addWidget(LineY, 1, 0);
    MainLayout->addWidget(LineZ, 2, 0);
    MainLayout->addWidget(Position, 3, 0);
    MainLayout->addWidget(textEdit, 5, 0);
    MainLayout->addWidget(satelliteText, 6, 0);
    MainLayout->addWidget(but, 7, 0);
    MainLayout->addWidget(GPSFile, 8, 0);
    MainWidget->setLayout(MainLayout);
    setCentralWidget(MainWidget);
}

MainWindow::~MainWindow()
{
}

void MainWindow::changeValues(){
    QSensorReading *reading = accel->reading();
    LineX->setText("X: " + QString::number(reading->property("x").value<qreal>()));
    LineY->setText("Y: " + QString::number(reading->property("y").value<qreal>()));
    LineZ->setText("Z: " + QString::number(reading->property("z").value<qreal>()));
    recognizeMove(reading);
    x=reading->property("x").value<qreal>();
    y=reading->property("y").value<qreal>();
    z=reading->property("z").value<qreal>();
}

void MainWindow::recognizeMove(QSensorReading *reading){
    if(abs(x-reading->property("x").value<qreal>())>abs(y-reading->property("y").value<qreal>()) && abs(x-reading->property("x").value<qreal>())>abs(z-reading->property("z").value<qreal>()) && (abs(reading->property("x").value<qreal>())-x)>5){
        if (reading->property("x").value<qreal>()-x > 0) Position->setText("left");
        else Position->setText("right");
    }
    else if(abs(reading->property("y").value<qreal>()-y)>3 || abs(reading->property("z").value<qreal>()-z)>5){
        if (reading->property("z").value<qreal>()-z > 0) Position->setText("forward");
        else Position->setText("backward");
    }
    else if(abs(x-reading->property("x").value<qreal>())<0.5 && abs(y-reading->property("y").value<qreal>())<0.5 && abs(z-reading->property("z").value<qreal>())<0.5){
        Position->setText("fixed");
    }
}

void MainWindow::positionUpdated(const QGeoPositionInfo &info)
{
    textEdit->setText("Lipovik coords: " + info.coordinate().toString()+" " + info.timestamp().toString());
    QString fileName;
    fileName = "GPS.txt";
    QFile file(fileName);
    QByteArray text = (info.coordinate().toString()+" " + info.timestamp().toString()+"\n").toUtf8();
    if (!file.open(QIODevice::WriteOnly | QIODevice::Text | QIODevice::Append))return;
    file.write(text);
    file.close();
}

void MainWindow::satelliteUpdated(const QList<QGeoSatelliteInfo> &info)
{
    satelliteText->setText("count of avaible satellites: "+QString::number(info.size()));
}

void MainWindow::readFromFille(){
    QString fileName;
    fileName = "GPS.txt";
    QFile file(fileName);
    QByteArray text;
    if (!file.open(QIODevice::ReadOnly))
        return;
    text = file.readAll();
    GPSFile->setText("Lipovik's file:\n"+QString(text));
    file.close();
}
