TEMPLATE=app
TARGET=Lab23Bondarenko

# Установка идентификатора пакета (PACKAGE_ID)
PACKAGE_ID = com.example.Lab23Bondarenko

QT += core gui quick qml multimedia multimediawidgets quickcontrols2

# You can define your target Android SDK version here
ANDROID_TARGET_SDK_VERSION = 29

# You can define your minimum Android SDK version here
ANDROID_MIN_SDK_VERSION = 21

# You can define your Android permissions here
ANDROID_PERMISSIONS += android.permission.CAMERA \
                       android.permission.WRITE_EXTERNAL_STORAGE

SOURCES += \
        main.cpp

RESOURCES += qml.qrc

# Additional import path used to resolve QML modules in Qt Creator's code model
QML_IMPORT_PATH =

# Additional import path used to resolve QML modules just for Qt Quick Designer
QML_DESIGNER_IMPORT_PATH =

# Default rules for deployment.
# qnx: target.path = /tmp/$${TARGET}/bin
# else: unix:!android: target.path = /opt/$${TARGET}/bin
# !isEmpty(target.path): INSTALLS += target*/
target.path = $$[QT_INSTALL_EXAMPLES]/multimedia/Lab23Bondarenko
INSTALLS += target

contains(ANDROID_TARGET_ARCH,arm64-v8a) {
    ANDROID_PACKAGE_SOURCE_DIR = \
        $$PWD/android
}
