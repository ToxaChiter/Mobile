#include "mainwindow.h"
#include "qcoreevent.h"

#include <QDebug>
#include <QGestureEvent>
#include <QLabel>
#include <QPainter>
#include <QPen>
#include <QTouchEvent>
#include <QQuickItem>
#include <type_traits>
#include <QPixmap>
#include <QApplication>
#include <QTimer>
#include <QPainter>
#include <QImageWriter>
#include <QFileDialog>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    setAttribute(Qt::WA_AcceptTouchEvents, true);
    image = new QPixmap(400, 700);
    image->fill("#ffffff");
    label->setPixmap(*image);
    layout->addWidget(label);
    mainWidget->setLayout(layout);
    setCentralWidget(mainWidget);
    QTimer *timer = new QTimer(this);
    timer->setInterval(10000);
    connect(timer, &QTimer::timeout, this, &MainWindow::timerOut);

    item.setAcceptTouchEvents(false);
}

MainWindow::~MainWindow()
{
}

bool MainWindow::event(QEvent *event){
    qDebug()<<event->type();
    if (event->type() == QEvent::TouchBegin ||
        event->type() == QEvent::TouchUpdate ||
        event->type() == QEvent::TouchEnd) {
        QTouchEvent *te = static_cast<QTouchEvent*>(event);
        if (te->type() == QEvent::TouchBegin) {
            lastPoint = te->points().first().position();
        } else {
            qDebug()<<event->type();
            currentPoint = te->points().first().position();
            qreal dist = distance(currentPoint,lastPoint);
            if (dist > 10 || te->type() == QEvent::TouchEnd) {
                QPainter* painter = new QPainter(image);
                painter->setPen(*pen);
                painter->drawLine(lastPoint, currentPoint);
                painter->end();
                label->setPixmap(*image);
                label->repaint();
                lastPoint = currentPoint;
            }
        }
        te->accept();
        return true;
    }
    else{
        setAttribute(Qt::WA_AcceptTouchEvents, false);
        QWidget::event(event);
        setAttribute(Qt::WA_AcceptTouchEvents, true);
    }
    return event;
}

inline qreal MainWindow::distance(QPointF a, QPointF b) {
    qreal d1 = abs(a.x()-b.x());
    qreal d2 = abs(a.y()-b.y());
    return sqrt(d1*d1+d2*d2);
}

void MainWindow::timerOut(){
    qDebug()<<"a";
    image->scaled(200,200).save("a.png", "png");
}

void MainWindow::changePenSize(int size){
    penSize=size;
    pen->setColor(penColor);
    pen->setWidth(penSize);
}

void MainWindow::changeColor(QString color){
    if(color == "Lipovik red"){
        penColor.setNamedColor("red");
    }
    else if(color == "Lipovik green"){
        penColor.setNamedColor("green");
    }
    else{
        penColor.setNamedColor(color);
    }

    pen->setColor(penColor);
    pen->setWidth(penSize);
}

void MainWindow::saveImage(){
    QString filters;
    foreach(QByteArray byte, QImageWriter::supportedImageFormats())
    {
        QString format = byte;
        filters += QString("%1 (%2)\n")
                       .arg(format.toUpper())
                       .arg("*." + format);
    }

    QString fileName = QFileDialog::getSaveFileName(this, tr("Save as..."), "img.png", filters);
    if (fileName.isEmpty() || fileName.isNull())
        return;

    label->pixmap().save(fileName);
}

void MainWindow::openImage(){
    QString fileName = QFileDialog::getOpenFileName(this, "Open document", QDir::currentPath());
    if(fileName.isNull()){
        return;
    }
    QPixmap bgPixmap(fileName);
    QPixmap scaled = bgPixmap.scaled(QSize(400, 700));
    image->swap(scaled);
    label->setPixmap(*image);
    label->repaint();
}

void MainWindow::clearImage(){
    image->fill("#ffffff");
    label->setPixmap(*image);
    label->repaint();
}
