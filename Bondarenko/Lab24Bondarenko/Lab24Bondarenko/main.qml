import QtQuick
import QtQuick.Window
import QtQuick.Controls
import QtQuick.Controls.Material
import QtQuick.Layouts
import QtSensors

Window {
    width: 360
    height: 520
    visible: true
    title: qsTr("Lab24 Bondarenko")

    property string toolBarBackgroundColor: "undefined"
    property string orientationTextString: "Ориентация: "
    property string devicePositionTextString: "Положение устройства: "

    ToolBar {
        id: toolBar
        width: parent.width
        background: Rectangle {
            color: toolBarBackgroundColor
            implicitWidth: toolBar.width
            implicitHeight: toolBar.height
        }

        RowLayout {
            ToolButton {
                text: ":"
                onClicked: menu.open()
            }

            ToolButton {
                text: "Положение"
                onClicked: orientationInfoDialog.open()
            }
            ToolButton {
                text: "О программе"
                onClicked: programInfoDialog.open()
            }
            ToolButton {
                text: "Выход"
                onClicked: Qt.quit()
            }
        }
    }

    Item {
        id: rootItem
        anchors.fill: parent
        width: parent.width

        Text {
            id: orientationText
            width: rootItem.width
            anchors.horizontalCenter: parent.horizontalCenter
            anchors.verticalCenter: parent.verticalCenter
            font.pixelSize: 20
            text: orientationTextString
            wrapMode: Text.WordWrap
            horizontalAlignment: Text.AlignHCenter
            color: toolBarBackgroundColor
        }

        Text {
            id: devicePositionText
            width: rootItem.width
            anchors.top: orientationText.bottom
            anchors.horizontalCenter: parent.horizontalCenter
            font.pixelSize: 20
            text: devicePositionTextString
            wrapMode: Text.WordWrap
            horizontalAlignment: Text.AlignHCenter
            color: toolBarBackgroundColor
        }

        Text {
            id: surnameText
            width: rootItem.width
            anchors.top: devicePositionText.bottom
            anchors.horizontalCenter: parent.horizontalCenter
            font.pixelSize: 20
            text: `Разработал Бондаренко`
            wrapMode: Text.WordWrap
            horizontalAlignment: Text.AlignHCenter
        }
    }

    OrientationSensor  {
        id: orientationSensor
        active: true
        onReadingChanged: {
            switch (reading.orientation) {
            case OrientationReading.RightUp:
                orientationTextString = "Горизонтальная ориентация"
                devicePositionTextString = "Устройство лежит на левом боку"
                toolBarBackgroundColor = "green"
                break
            case OrientationReading.LeftUp:
                orientationTextString = "Горизонтальная ориентация"
                devicePositionTextString = "Устройство лежит на правом боку"
                toolBarBackgroundColor = "green"
                break
            case OrientationReading.TopUp:
                orientationTextString = "Вертикальная ориентация"
                devicePositionTextString = "Верхний край устройства направлен вверх"
                toolBarBackgroundColor = "blue"
                break
            case OrientationReading.TopDown:
                orientationTextString = "Вертикальная ориентация"
                devicePositionTextString = "Верхний край устройства направлен вниз"
                toolBarBackgroundColor = "blue"
                break
            case OrientationReading.FaceUp:
                devicePositionTextString = "Лицевая панель устройства направлена вверх"
                break
            case OrientationReading.FaceDown:
                devicePositionTextString = "Лицевая панель устройства направлена вниз"
                break
            case OrientationReading.Undefined:
                orientationTextString = "Не удалось определить ориентацию"
                devicePositionTextString = "Неизвестное положение устройства"
                toolBarBackgroundColor = "red"
                break
            }
        }
    }

    Menu {
        id: menu

        MenuItem {
            text: "Красный цвет"
            onClicked: toolBarBackgroundColor = "red"
        }
        MenuItem {
            text: "Зеленый цвет"
            onClicked: toolBarBackgroundColor = "green"
        }
        MenuItem {
            text: "Синий цвет"
            onClicked: toolBarBackgroundColor = "blue"
        }
    }

    Dialog {
        id: orientationInfoDialog
        title: "Текущее положение"
        standardButtons: Dialog.Ok
        anchors.centerIn: parent

        Text {
            width: programInfoDialog.width
            anchors.top: orientationText.bottom
            anchors.horizontalCenter: parent.horizontalCenter
            anchors.centerIn: parent
            text: orientationTextString + `\n` + devicePositionTextString + `\nРазработал Бондаренко`
            wrapMode: Text.WordWrap
            horizontalAlignment: Text.AlignHCenter
        }
    }

    Dialog {
        id: programInfoDialog
        title: "О программе";
        standardButtons: Dialog.Ok
        anchors.centerIn: parent

        Text {
            width: programInfoDialog.width
            anchors.top: orientationText.bottom
            anchors.horizontalCenter: parent.horizontalCenter
            anchors.centerIn: parent
            text: "Данная программа была создана для предмета \"Разработка программного обеспечения для мобильных платформ\"\nНомер лабораторной работы: 24\nРазработал Бондаренко"
            wrapMode: Text.WordWrap
            horizontalAlignment: Text.AlignHCenter
        }
    }
}
