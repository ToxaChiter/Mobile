#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QCheckBox>
#include <QGridLayout>
#include <QLabel>
#include <QMainWindow>
#include <QMenu>
#include <QPushButton>
#include <QRadioButton>
#include <QFileDialog>
#include <QMenuBar>
#include <QToolBar>
#include <QtSensors/QtSensors>
#include <QScreen>
#include <QGuiApplication>

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();
    QCheckBox *checkBox=new QCheckBox("checkBox");
    QLabel *checkBoxLabel = new QLabel("not checked");
    QPushButton *button = new QPushButton("info");
    QLabel *buttonLabel = new QLabel;
    QRadioButton *radioButton = new QRadioButton("choose");
    QLabel *radioButtonLabel = new QLabel("not choosed");
    QGridLayout *layout = new QGridLayout;
    QWidget *MainWidget= new QWidget;
    QWidget *horizontalWidget = new QWidget;
    QMenu * fileMenu = new QMenu("file");
    QPixmap *image;
    QLabel *imageLabel = new QLabel("");
    QMenuBar * mainMenu = new QMenuBar;
    QToolBar *toolBar = new QToolBar;
    QSensor *orient = new QSensor("QOrientationSensor");

private slots:
    void changeCBLabel();
    void showText();
    void changeRBLabel();
    void openImage();
    void clearImage();
    void changeOrientation();
};
#endif // MAINWINDOW_H
