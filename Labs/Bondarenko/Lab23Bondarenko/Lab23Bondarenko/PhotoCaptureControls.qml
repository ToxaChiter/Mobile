import QtQuick
import QtMultimedia
import QtQuick.Layouts

FocusScope {
    id : captureControls
    property CaptureSession captureSession
    property bool previewAvailable : false

    property int buttonsmargin: 8
    property int buttonsPanelWidth
    property int buttonsPanelPortraitHeight
    property int buttonsWidth

    signal previewSelected
    signal videoModeSelected

    Rectangle {
        id: buttonPaneShadow
        color: Qt.rgba(0.08, 0.08, 0.08, 1)

        GridLayout {
            id: buttonsColumn
            anchors.margins: buttonsmargin
            flow: captureControls.state === "MobilePortrait"
                  ? GridLayout.LeftToRight : GridLayout.TopToBottom
            CameraButton {
                //text: "Capture"
                text: "Получить кадр"
                //implicitWidth: buttonsWidth
                width: 150
                visible: captureSession.imageCapture.readyForCapture
                onClicked: captureSession.imageCapture.captureToFile("")
            }

            Item {
                //implicitWidth: buttonsWidth
                width: 175
                height: 70
                CameraButton {
                    //text: "View"
                    text: "Отобразить\nБондаренко"
                    anchors.fill: parent
                    onClicked:state = captureControls.previewSelected()
                    visible: captureControls.previewAvailable
                }
            }
        }

        GridLayout {
            id: bottomColumn
            anchors.margins: buttonsmargin
            flow: captureControls.state === "MobilePortrait"
                  ? GridLayout.LeftToRight : GridLayout.TopToBottom

            CameraListButton {
                //implicitWidth: buttonsWidth
                width: 150
                state: captureControls.state
                onValueChanged: captureSession.camera.cameraDevice = value
            }

            CameraPropertyButton {
                id : wbModesButton
                //implicitWidth: buttonsWidth
                width: 75
                state: captureControls.state
                value: Camera.WhiteBalanceAuto
                model: ListModel {
                    ListElement {
                        icon: "images/camera_auto_mode.png"
                        value: Camera.WhiteBalanceAuto
                        text: "Авто"
                    }
                    ListElement {
                        icon: "images/camera_white_balance_sunny.png"
                        value: Camera.WhiteBalanceSunlight
                        text: "Солнечный цвет"
                    }
                    ListElement {
                        icon: "images/camera_white_balance_cloudy.png"
                        value: Camera.WhiteBalanceCloudy
                        text: "Облачно"
                    }
                    ListElement {
                        icon: "images/camera_white_balance_incandescent.png"
                        value: Camera.WhiteBalanceTungsten
                        text: "Ламповое освещение"
                    }
                    ListElement {
                        icon: "images/camera_white_balance_flourescent.png"
                        value: Camera.WhiteBalanceFluorescent
                        text: "Флуоресцентное освещение"
                    }
                }
                onValueChanged: captureControls.captureSession.camera.whiteBalanceMode = wbModesButton.value
            }

            CameraButton {
                id: quitButton
                //implicitWidth: buttonsWidth
                width: 100
                text: "Выйти\nБондаренко"
                onClicked: Qt.quit()
            }
        }
    }

    ZoomControl {
        x : 0
        y : captureControls.state === "MobilePortrait" ? -buttonPaneShadow.height : 0
        width : 100
        height: parent.height

        currentZoom: camera.zoomFactor
        maximumZoom: camera.maximumZoomFactor
        onZoomTo: camera.setDigitalZoom(value)
    }

    states: [
        State {
            name: "MobilePortrait"
            PropertyChanges {
                target: buttonPaneShadow
                width: parent.width
                height: captureControls.buttonsPanelPortraitHeight
            }
            PropertyChanges {
                target: buttonsColumn
                height: captureControls.buttonsPanelPortraitHeight / 2 - buttonsmargin
            }
            PropertyChanges {
                target: bottomColumn
                height: captureControls.buttonsPanelPortraitHeight / 2 - buttonsmargin
            }
            AnchorChanges {
                target: buttonPaneShadow
                anchors.bottom: parent.bottom
                anchors.left: parent.left
                anchors.right: parent.right
            }
            AnchorChanges {
                target: buttonsColumn
                anchors.left: parent.left
                anchors.right: parent.right
                anchors.top: parent.top
            }
            AnchorChanges {
                target: bottomColumn
                anchors.bottom: parent.bottom
                anchors.left: parent.left
                anchors.right: parent.right
            }
        },
        State {
            name: "MobileLandscape"
            PropertyChanges {
                target: buttonPaneShadow
                width: buttonsPanelWidth
                height: parent.height
            }
            PropertyChanges {
                target: buttonsColumn
                height: parent.height
                width: buttonPaneShadow.width / 2
            }
            PropertyChanges {
                target: bottomColumn
                height: parent.height
                width: buttonPaneShadow.width / 2
            }
            AnchorChanges {
                target: buttonPaneShadow
                anchors.top: parent.top
                anchors.right: parent.right
            }
            AnchorChanges {
                target: buttonsColumn
                anchors.top: parent.top
                anchors.bottom: parent.bottom
                anchors.left: parent.left
            }
            AnchorChanges {
                target: bottomColumn
                anchors.top: parent.top
                anchors.bottom: parent.bottom
                anchors.right: parent.right
            }
        },
        State {
            name: "Other"
            PropertyChanges {
                target: buttonPaneShadow
                width: bottomColumn.width + 16
                height: parent.height
            }
            AnchorChanges {
                target: buttonPaneShadow
                anchors.top: parent.top
                anchors.right: parent.right
            }
            AnchorChanges {
                target: buttonsColumn
                anchors.top: parent.top
                anchors.right: parent.right
            }
            AnchorChanges {
                target: bottomColumn
                anchors.bottom: parent.bottom
                anchors.right: parent.right
            }
        }
    ]
}
