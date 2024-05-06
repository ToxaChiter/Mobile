#include "mainwindow.h"

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
    orient->start();
    fileMenu->addAction("Open", this, &MainWindow::openImage);
    fileMenu->addAction("clear", this, &MainWindow::clearImage);
    toolBar->addAction(QPixmap(":/open.png"), "1", this, &MainWindow::openImage);
    toolBar->addAction(QPixmap(":/clean.png"), "2", this, &MainWindow::clearImage);
    addToolBar(Qt::TopToolBarArea, toolBar);
    mainMenu->addMenu(fileMenu);

    image = new QPixmap(200, 200);
    image->fill("#ffffff");
    imageLabel->setPixmap(*image);

    layout->setMenuBar(mainMenu);
    layout->addWidget(checkBox, 0, 0);
    layout->addWidget(checkBoxLabel, 0, 1);
    layout->addWidget(button, 1, 0);
    layout->addWidget(buttonLabel, 1, 1);
    layout->addWidget(radioButton, 2, 0);
    layout->addWidget(radioButtonLabel, 2, 1);
    layout->addWidget(imageLabel, 3, 0);

    MainWidget->setLayout(layout);
    setCentralWidget(MainWidget);

    connect(orient, &QOrientationSensor::readingChanged, this, &MainWindow::changeOrientation);

    connect(checkBox, &QCheckBox::stateChanged, this, &MainWindow::changeCBLabel);
    connect(button, &QPushButton::clicked, this, &MainWindow::showText);
    connect(radioButton, &QRadioButton::clicked, this, &MainWindow::changeRBLabel);
}

MainWindow::~MainWindow()
{
}

void MainWindow::changeCBLabel(){
    if(checkBox->checkState()==Qt::Checked){
        checkBoxLabel->setText("checked by Lipovik");
    }
    else{
        checkBoxLabel->setText("not checked");
    }
}

void MainWindow::showText(){
    buttonLabel->setText("created by Lipovik");
}

void MainWindow::changeRBLabel(){
    if(radioButton->isChecked()){
        radioButtonLabel->setText("choosed by Lipovik");
    }
    else{
        radioButtonLabel->setText("not choosed");
    }
}

void MainWindow::openImage(){
    QString fileName = QFileDialog::getOpenFileName(this, "Open document", QDir::currentPath());
    if(fileName.isNull()){
        return;
    }
    QPixmap bgPixmap(fileName);
    QPixmap scaled = bgPixmap.scaled(QSize(200, 200));
    image->swap(scaled);
    imageLabel->setPixmap(*image);
    imageLabel->repaint();
}

void MainWindow::clearImage(){
    image->fill("#ffffff");
    imageLabel->setPixmap(*image);
    imageLabel->repaint();
}

void MainWindow::changeOrientation(){
    QOrientationReading *reading = static_cast<QOrientationReading*>(orient->reading());
    qDebug()<<reading->orientation();
    if(reading->orientation() == QOrientationReading::TopUp || reading->orientation() == QOrientationReading::TopDown){
        addToolBar(Qt::TopToolBarArea, toolBar);

        layout->removeWidget(checkBox);
        layout->removeWidget(checkBoxLabel);
        layout->removeWidget(button);
        layout->removeWidget(buttonLabel);
        layout->removeWidget(radioButton);
        layout->removeWidget(radioButtonLabel);
        layout->removeWidget(imageLabel);

        layout->addWidget(checkBox, 0, 0);
        layout->addWidget(checkBoxLabel, 0, 1);
        layout->addWidget(button, 1, 0);
        layout->addWidget(buttonLabel, 1, 1);
        layout->addWidget(radioButton, 2, 0);
        layout->addWidget(radioButtonLabel, 2, 1);
        layout->addWidget(imageLabel, 3, 0);

        repaint();
    }
    else if(reading->orientation() == QOrientationReading::LeftUp || reading->orientation() == QOrientationReading::RightUp){
        addToolBar(Qt::LeftToolBarArea, toolBar);
        layout->removeWidget(checkBox);
        layout->removeWidget(checkBoxLabel);
        layout->removeWidget(button);
        layout->removeWidget(buttonLabel);
        layout->removeWidget(radioButton);
        layout->removeWidget(radioButtonLabel);
        layout->removeWidget(imageLabel);

        layout->addWidget(checkBox, 0, 0);
        layout->addWidget(checkBoxLabel, 1, 0);
        layout->addWidget(button, 2, 0);
        layout->addWidget(buttonLabel, 3, 0);
        layout->addWidget(radioButton, 4, 0);
        layout->addWidget(radioButtonLabel, 5, 0);
        layout->addWidget(imageLabel, 0, 1, 6, 1);
        repaint();
    }
}
