import QtQuick
import QtQuick.Window
import QtMultimedia


Rectangle {
    id : cameraUI

    width: 800
    height: 480
    color: "black"
    state: "PhotoCapture"

    property string platformScreen: ""
    property int buttonsPanelLandscapeWidth: 328
    property int buttonsPanelPortraitHeight: 180

    onWidthChanged: {
        setState()
    }
    function setState() {
        if (Qt.platform.os === "android" || Qt.platform.os === "ios") {
            if (Screen.desktopAvailableWidth < Screen.desktopAvailableHeight) {
                stillControls.state = "MobilePortrait";
            } else {
                stillControls.state  = "MobileLandscape";
            }
        } else {
            stillControls.state = "Other";
        }
        console.log("State: " + stillControls.state);
        stillControls.buttonsWidth = (stillControls.state === "MobilePortrait")
                ? Screen.desktopAvailableWidth/3.4 : 144
    }

    states: [
        State {
            name: "PhotoCapture"
            StateChangeScript {
                script: {
                    camera.start()
                }
            }
        },
        State {
            name: "PhotoPreview"
        }
    ]

    CaptureSession {
        id: captureSession
        camera: Camera {
            id: camera
        }
        imageCapture: ImageCapture {
            id: imageCapture
        }

        recorder: MediaRecorder {
            id: recorder
//             resolution: "640x480"
//             frameRate: 30
        }
        videoOutput: viewfinder
    }

    PhotoPreview {
        id : photoPreview
        anchors.fill : parent
        onClosed: cameraUI.state = "PhotoCapture"
        visible: (cameraUI.state === "PhotoPreview")
        focus: visible
        source: imageCapture.preview
    }

    VideoOutput {
        id: viewfinder
        visible: ((cameraUI.state === "PhotoCapture") || (cameraUI.state === "VideoCapture"))

        x: 0
        y: 0
        width: ((stillControls.state === "MobilePortrait") ? parent.width : (parent.width-buttonsPanelLandscapeWidth))
        height: ((stillControls.state === "MobilePortrait") ? parent.height - buttonsPanelPortraitHeight : parent.height)
    }

    PhotoCaptureControls {
        id: stillControls
        state: setState()
        anchors.fill: parent
        buttonsPanelPortraitHeight: cameraUI.buttonsPanelPortraitHeight
        buttonsPanelWidth: cameraUI.buttonsPanelLandscapeWidth
        captureSession: captureSession
        visible: (cameraUI.state === "PhotoCapture")
        onPreviewSelected: cameraUI.state = "PhotoPreview"
        onVideoModeSelected: cameraUI.state = "VideoCapture"
        previewAvailable: imageCapture.preview.length !== 0
    }
}
