TEMPLATE=app
TARGET=Lab24Bondarenko

# Установка идентификатора пакета (PACKAGE_ID)
PACKAGE_ID = com.example.Lab24Bondarenko

QT += core quick qml sensors quickcontrols2

SOURCES += \
        main.cpp

resources.files = main.qml 
resources.prefix = /$${TARGET}
RESOURCES += resources

# Additional import path used to resolve QML modules in Qt Creator's code model
QML_IMPORT_PATH =

# Additional import path used to resolve QML modules just for Qt Quick Designer
QML_DESIGNER_IMPORT_PATH =

# Default rules for deployment.
# qnx: target.path = /tmp/$${TARGET}/bin
# else: unix:!android: target.path = /opt/$${TARGET}/bin
# !isEmpty(target.path): INSTALLS += target
target.path = $$[QT_INSTALL_EXAMPLES]/sensors/Lab24Bondarenko
INSTALLS += target

DISTFILES +=

contains(ANDROID_TARGET_ARCH,arm64-v8a) {
    ANDROID_PACKAGE_SOURCE_DIR = \
        $$PWD/android
}
