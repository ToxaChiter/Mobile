import QtQuick
import QtMultimedia
import QtQuick.Window
import QtQuick.Controls
import QtQuick.Layouts
import QtQuick.Dialogs

Item {
    id: photoPreview
    property alias source : preview.source
    signal closed

    Image {
        id: preview
        anchors.fill: parent
        fillMode: Image.PreserveAspectFit
        smooth: true

        Canvas {
            id: drawingCanvas
            anchors.fill: parent

            // Переменные для хранения предыдущей позиции и цвета/толщины пера
            property point lastPos: Qt.point(-1, -1)
            property color penColor: "red"
            property int penWidth: 5

            onPaint: {
                var ctx = getContext("2d");
                ctx.lineWidth = drawingCanvas.penWidth;
                ctx.strokeStyle = drawingCanvas.penColor;
            }

            MouseArea {
                id: mouseArea
                anchors.fill: parent
                hoverEnabled: true
                property point lastMousePos: Qt.point(0, 0)

                onPressed: mouse => {
                   drawingCanvas.lastPos = Qt.point(mouse.x, mouse.y);
                   mouseArea.lastMousePos = Qt.point(mouse.x, mouse.y);

                   /*console.log("OnPressed", mouse.x, mouse.y);
                   console.log("lastPos", drawingCanvas.lastPos.x, drawingCanvas.lastPos.y);
                   console.log("lastMousePos", mouseArea.lastMousePos.x, mouseArea.lastMousePos.y);*/
                }
                onReleased: mouse => {
                    drawingCanvas.lastPos = Qt.point(-1, -1);
                    mouseArea.lastMousePos = Qt.point(0, 0);

                    /*console.log(`onReleased ` + mouse.x + ` ` + mouse.y);
                    console.log(`lastPos ` + drawingCanvas.lastPos.x + ` ` + drawingCanvas.lastPos.y);
                    console.log(`lastMousePos ` + mouseArea.lastMousePos.x + ` ` + mouseArea.lastMousePos.y);*/
                }
                onPositionChanged: mouse => {
                    if (mouse.buttons & Qt.LeftButton /*&& drawingCanvas.lastPos.x !== -1 && drawingCanvas.lastPos.y !== -1*/) {

                        var ctx = drawingCanvas.getContext("2d");
                        ctx.lineWidth = drawingCanvas.penWidth;
                        ctx.strokeStyle = drawingCanvas.penColor;
                        ctx.beginPath();
                        ctx.moveTo(mouseArea.lastMousePos.x, mouseArea.lastMousePos.y);
                        ctx.lineTo(mouse.x, mouse.y);
                        ctx.stroke();
                        mouseArea.lastMousePos.x = mouse.x;
                        mouseArea.lastMousePos.y = mouse.y;
                        // Перерисовываем Canvas
                        drawingCanvas.requestPaint();

                        /*console.log(`onPositionChanged ` + mouse.x + ` ` + mouse.y);
                        console.log(`lastPos ` + drawingCanvas.lastPos.x + ` ` + drawingCanvas.lastPos.y);
                        console.log(`lastMousePos ` + mouseArea.lastMousePos.x + ` ` + mouseArea.lastMousePos.y);*/
                    }
                }
            }
        }
    }

    FocusScope {
        id : menuControls
        anchors.fill: parent

        property CaptureSession captureSession
        property bool previewAvailable : false

        property int buttonsmargin: 8
        property int buttonsPanelWidth: 328
        property int buttonsPanelPortraitHeight: 180
        //property int buttonsWidth

        signal previewSelected
        signal videoModeSelected

        state: "MobilePortrait"

        Rectangle {
            id: buttonPaneShadow
            color: Qt.rgba(0.08, 0.08, 0.08, 1)

            GridLayout {
                id: buttonsColumn
                anchors.margins: buttonsmargin
                flow: menuControls.state === "MobilePortrait"
                      ? GridLayout.LeftToRight : GridLayout.TopToBottom
                columns: 2
                rows: 2

                CameraButton {
                    id: changePenColorButton
                    text: "Изменить цвет"
                    width: 150
                    onClicked: {
                        drawingCanvas.penColor = Qt.rgba(Math.random(), Math.random(), Math.random(), 1);
                    }

                    Layout.alignment: Qt.AlignHCenter | Qt.AlignVCenter
                    Layout.row: 0
                    Layout.column: 0
                }

                CameraButton {
                    id: changePenWidthButton
                    text: "Изменить толщину"
                    width: 150
                    onClicked: {
                        drawingCanvas.penWidth = Math.floor(Math.random() * 8) + 1; // Генерируем случайное значение от 1 до 8
                    }

                    Layout.alignment: Qt.AlignHCenter | Qt.AlignVCenter
                    Layout.row: 0
                    Layout.column: 1
                }

                CameraButton {
                    id: savePhotoButton
                    text: "Сохранить"
                    width: 150
                    onClicked: {
                        saveImage();
                    }

                    Layout.alignment: Qt.AlignHCenter | Qt.AlignVCenter
                    Layout.row: 1
                    Layout.column: 0
                }

                CameraButton {
                    id: returnButton
                    text: "Вернуться\nБондаренко"
                    width: 150
                    onClicked: {
                        photoPreview.closed();
                    }

                    Layout.alignment: Qt.AlignHCenter | Qt.AlignVCenter
                    Layout.row: 1
                    Layout.column: 1
                }
            }

            GridLayout {
                id: buttomColumn
                anchors.margins: buttonsmargin
                flow: menuControls.state === "MobilePortrait"
                      ? GridLayout.LeftToRight : GridLayout.TopToBottom


            }
        }

        ZoomControl {
            x : 0
            y : menuControls.state === "MobilePortrait" ? -buttonPaneShadow.height : 0
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
                    height: menuControls.buttonsPanelPortraitHeight
                }
                PropertyChanges {
                    target: buttonsColumn
                    height: menuControls.buttonsPanelPortraitHeight / 2 - buttonsmargin
                }
                PropertyChanges {
                    target: bottomColumn
                    height: menuControls.buttonsPanelPortraitHeight / 2 - buttonsmargin
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


    function saveImage() {
        var previewImage = preview.grabToImage(preview.width, preview.height);
        var canvasImage = drawingCanvas.grabToImage(drawingCanvas.width, drawingCanvas.height);

        if (!previewImage || !canvasImage) {
            console.error("Unable to grab one or both images.");
            return;
        }

        // Получаем пиксели изображений
        var previewPixels = previewImage.toVariant().toByteArray();
        var canvasPixels = canvasImage.toVariant().toByteArray();

        // Количество байтов на пиксель RGBA
        var bytesPerPixel = 4;

        // Создаем новый буфер для объединенных пикселей
        var mergedPixels = new Uint8Array(previewPixels.length);

        // Копируем пиксели из изображения предварительного просмотра в объединенные пиксели
        for (var i = 0; i < previewPixels.length; i += bytesPerPixel) {
            mergedPixels[i] = previewPixels[i];         // Красный
            mergedPixels[i + 1] = previewPixels[i + 1]; // Зеленый
            mergedPixels[i + 2] = previewPixels[i + 2]; // Синий
            mergedPixels[i + 3] = previewPixels[i + 3]; // Альфа
        }

        // Копируем пиксели изображения холста в объединенные пиксели
        for (var j = 0; j < canvasPixels.length; j += bytesPerPixel) {
            var canvasAlpha = canvasPixels[j + 3]; // Альфа канала холста
            if (canvasAlpha !== 0) {
                // Если холст непрозрачный, копируем его пиксели в объединенные пиксели
                mergedPixels[j] = canvasPixels[j];         // Красный
                mergedPixels[j + 1] = canvasPixels[j + 1]; // Зеленый
                mergedPixels[j + 2] = canvasPixels[j + 2]; // Синий
                mergedPixels[j + 3] = canvasPixels[j + 3]; // Альфа
            }
        }

        // Создаем новое изображение из объединенных пикселей
        var mergedImage = QtQuick.Image.fromBytes(mergedPixels, previewImage.width, previewImage.height, QtQuick.Image.Format_RGBA8888);

        // Путь для сохранения на Android
        var filePath = "file://" + QtAndroid.currentActivity.getExternalFilesDir(null).toString();
        var fileName = filePath + "/merged_image.png";

        mergedImage.saveToFile(fileName);

        console.log("Файл сохранен:", fileName);
    }
}


