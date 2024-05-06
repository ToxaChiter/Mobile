#include "mainwindow.h"

#include <QApplication>
#include <QComboBox>
#include <QSpinBox>
#include <QTableWidgetItem>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    QWidget window;
    MainWindow w;
    QPushButton saveButton("Lipovik save");
    QPushButton openButton("open");
    QPushButton clearButton("clear");
    QSpinBox penSize;
    penSize.setValue(7);
    QComboBox penColor;
    QString str;
    penColor.addItem("Lipovik red");
    penColor.addItem("Lipovik green");
    penColor.addItem("blue");
    QGridLayout layout;
    layout.addWidget(&saveButton, 0, 0);
    layout.addWidget(&openButton, 0, 1);
    layout.addWidget(&clearButton, 0, 2);
    layout.addWidget(&penSize, 0, 3);
    layout.addWidget(&penColor, 0, 4);
    layout.addWidget(&w, 1, 0, 1, 5);
    window.setLayout(&layout);

    QObject::connect(&penSize, &QSpinBox::valueChanged, &w, &MainWindow::changePenSize);
    QObject::connect(&penColor, &QComboBox::currentTextChanged, &w, &MainWindow::changeColor);
    QObject::connect(&saveButton, &QPushButton::clicked, &w, &MainWindow::saveImage);
    QObject::connect(&openButton, &QPushButton::clicked, &w, &MainWindow::openImage);
    QObject::connect(&clearButton, &QPushButton::clicked, &w, &MainWindow::clearImage);



    window.show();
    return a.exec();
}
