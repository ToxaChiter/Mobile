QT += core gui quick qml positioning sensors

CONFIG += c++11

# You can define your target Android SDK version here
ANDROID_TARGET_SDK_VERSION = 29

# You can define your minimum Android SDK version here
ANDROID_MIN_SDK_VERSION = 21

# You can define your Android permissions here
ANDROID_PERMISSIONS += android.permission.ACCESS_FINE_LOCATION \
                       android.permission.ACCESS_COARSE_LOCATION

SOURCES += \
        main.cpp

RESOURCES += qml.qrc

# Additional import path used to resolve QML modules in Qt Creator's code model
QML_IMPORT_PATH += $$PWD

# Additional import path used to resolve QML modules just for Qt Quick Designer
QML_DESIGNER_IMPORT_PATH =

# Default rules for deployment.
qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target

contains(ANDROID_TARGET_ARCH,armeabi-v7a) {
    ANDROID_PACKAGE_SOURCE_DIR = \
        $$PWD/android
}
