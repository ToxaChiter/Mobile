#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QGraphicsItem>
#include <QGraphicsView>
#include <QGridLayout>
#include <QLabel>
#include <QMainWindow>
#include <QPaintEvent>
#include <QPixmap>
#include <QPlainTextEdit>
#include <QPushButton>
#include <QQuickItem>
#include <QTouchEvent>

class MyLabel : public QLabel {
protected:
    virtual void paintEvent(QPaintEvent* e) {
        QLabel::paintEvent(e);
    }
};

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();
    QPixmap *image;
    QGridLayout *layout = new QGridLayout;
    QWidget *mainWidget = new QWidget;
    QPointF lastPoint, currentPoint;
    qreal distance(QPointF a, QPointF b);
    QLabel *label = new MyLabel();
    QLabel *label1 = new MyLabel();
    QQuickItem item;
    QPen *pen = new QPen(QBrush(Qt::red),7);
    int penSize=7;
    QColor penColor = Qt::red;
protected:
    bool event(QEvent *event);

private slots:
    void timerOut();
public slots:
    void changePenSize(int size);
    void changeColor(QString color);
    void saveImage();
    void openImage();
    void clearImage();
};
#endif // MAINWINDOW_H
