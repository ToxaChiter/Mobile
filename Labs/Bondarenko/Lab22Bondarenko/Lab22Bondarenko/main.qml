import QtQuick 2.15
import QtQuick.Window 2.15
import QtQuick.Layouts
import QtQuick.Controls
import QtSensors

Window {
    width: 360
    height: 520
    visible: true
    title: qsTr("Lab2 Bondarenko")

    property real minX: Number.MAX_VALUE
    property real minY: Number.MAX_VALUE
    property real minZ: Number.MAX_VALUE
    property real maxX: Number.MIN_VALUE
    property real maxY: Number.MIN_VALUE
    property real maxZ: Number.MIN_VALUE
    property real prevX: 0
    property real prevY: 0
    property real prevZ: 0
    property real prevTime: 0
    property real accelerationX: 0
    property real accelerationY: 0
    property real accelerationZ: 0
    property real velocityX: 0
    property real velocityY: 0
    property real velocityZ: 0
    property real totalAcceleration: 0
    property real totalVelocity: 0

    property real shakeThreshold: 600 // Пороговое значение для встряхивания

    Accelerometer  {
        id: accelerometer
        dataRate: 100
        active: true

        onReadingChanged: {
            var currentTime = new Date().getTime();
            var deltaTime = (currentTime - prevTime) / 1000; // в секундах
            prevTime = currentTime;

            var x = accelerometer.reading.x;
            var y = accelerometer.reading.y;
            var z = accelerometer.reading.z;

            // Вычисление ускорения как изменения скорости за единицу времени
            accelerationX = (x - prevX) / deltaTime;
            accelerationY = (y - prevY) / deltaTime;
            accelerationZ = (z - prevZ) / deltaTime;

            // Вычисление скорости как интеграл ускорения по времени
            velocityX += accelerationX * deltaTime;
            velocityY += accelerationY * deltaTime;
            velocityZ += accelerationZ * deltaTime;

            let speedDiff = Math.abs(x + y + z - prevX - prevY - prevZ) / deltaTime;

            prevX = x;
            prevY = y;
            prevZ = z;

            xValue.text = x.toFixed(3);
            yValue.text = y.toFixed(3);
            zValue.text = z.toFixed(3);

            // Обновляем минимальные и максимальные значения
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);

            // Выводим минимальные и максимальные значения
            minXValue.text = minX.toFixed(3);
            minYValue.text = minY.toFixed(3);
            minZValue.text = minZ.toFixed(3);
            maxXValue.text = maxX.toFixed(3);
            maxYValue.text = maxY.toFixed(3);
            maxZValue.text = maxZ.toFixed(3);

            totalAcceleration = Math.sqrt(accelerationX * accelerationX + accelerationY * accelerationY + accelerationZ * accelerationZ);
            acceleration.text = totalAcceleration.toFixed(3) + "м/с²";
            totalVelocity = Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
            speed.text = totalVelocity.toFixed(3) + "м/с";

            // Определяем состояние устройства
            if (speedDiff > shakeThreshold) {
                deviceState.text = "Устройство встряхнули";
            } else if (speedDiff > 0) {
                deviceState.text = "Устройство в движении";
            } else {
                deviceState.text = "Устройство неподвижно";
            }

            console.log("totalVelocity " + totalVelocity);
        }
    }


    ScrollView {
        anchors.fill: parent

        Column {
            //spacing: 10
            width: parent.width

            Text {
                text: qsTr("Акселерометр\nРазработал Бондаренко")
                font.pixelSize: 20
                horizontalAlignment: Text.AlignHCenter
                anchors.horizontalCenter: parent.horizontalCenter
            }

            Row {
                spacing: 10
                anchors.horizontalCenter: parent.horizontalCenter

                Text { text: qsTr("X:") }
                Text { id: xValue }

                Text { text: qsTr("Y:") }
                Text { id: yValue }

                Text { text: qsTr("Z:") }
                Text { id: zValue }
            }

            Row {
                spacing: 10
                anchors.horizontalCenter: parent.horizontalCenter

                Text { text: qsTr("Min X:") }
                Text { id: minXValue }

                Text { text: qsTr("Min Y:") }
                Text { id: minYValue }

                Text { text: qsTr("Min Z:") }
                Text { id: minZValue }
            }

            Row {
                spacing: 10
                anchors.horizontalCenter: parent.horizontalCenter

                Text { text: qsTr("Max X:") }
                Text { id: maxXValue }

                Text { text: qsTr("Max Y:") }
                Text { id: maxYValue }

                Text { text: qsTr("Max Z:") }
                Text { id: maxZValue }
            }

            Text {
                id: deviceState
                text: qsTr("Встряхните устройство!")
                font.pixelSize: 18
                horizontalAlignment: Text.AlignHCenter
                anchors.horizontalCenter: parent.horizontalCenter
            }

            Row {
                spacing: 10
                anchors.horizontalCenter: parent.horizontalCenter

                Text {
                    text: qsTr("Скорость:")
                    font.pixelSize: 16
                }

                Text {
                    id: speed
                    font.pixelSize: 16
                }
            }

            Row {
                spacing: 10
                anchors.horizontalCenter: parent.horizontalCenter

                Text {
                    text: qsTr("Ускорение:")
                    font.pixelSize: 16
                }

                Text {
                    id: acceleration
                    font.pixelSize: 16
                }
            }

            Text {
                text: qsTr("Датчик GPS\nРазработал Бондаренко")
                font.pixelSize: 20
                horizontalAlignment: Text.AlignHCenter
                anchors.horizontalCenter: parent.horizontalCenter
            }

            Text {
                id: isGPSEnabled
                text: qsTr("GPS")
                font.pixelSize: 18
                horizontalAlignment: Text.AlignHCenter
                anchors.horizontalCenter: parent.horizontalCenter
            }

            Text {
                id: latitudeText
                text: qsTr("Широта:")
                font.pixelSize: 18
                horizontalAlignment: Text.AlignHCenter
                anchors.horizontalCenter: parent.horizontalCenter
            }

            Text {
                id: longitudeText
                text: qsTr("Долгота:")
                font.pixelSize: 18
                anchors.horizontalCenter: parent.horizontalCenter
            }

            Text {
                id: altitudeText
                text: qsTr("Высота:")
                font.pixelSize: 18
                horizontalAlignment: Text.AlignHCenter
                anchors.horizontalCenter: parent.horizontalCenter
            }

            Text {
                id: satelliteInfo
                text: qsTr("Спутники:")
                font.pixelSize: 16
                width: 360
                wrapMode: Text.WordWrap
                horizontalAlignment: Text.AlignHCenter
                anchors.horizontalCenter: parent.horizontalCenter
            }
        }
    }

    function updateCoordinates(latitude, longitude, altitude) {
        latitudeText.text = "Широта: " + latitude.toFixed(5) + "°";
        longitudeText.text = "Долгота: " + longitude.toFixed(5) + "°";
        altitudeText.text = "Высота: " + altitude.toFixed(2) + "м";
    }

    function setIsGPSEnabled(text) {
        isGPSEnabled.text = text;
    }

    function updateSatellites(satellites) {
        satelliteInfo.text = satellites;
    }


    // Таймер для записи координат каждые 5 минут
    Timer {
        id: timer
        interval: 300000 // 5 минут в миллисекундах
        repeat: true
        running: false // Не запускаем сразу, чтобы дать время на получение координат
        onTriggered: {
            // Пишем координаты в файл
            if (latitudeText.text !== "Широта: -" && longitudeText.text !== "Долгота: -") {
                var file = Qt.platform.openExternalFileUrl(Qt.platform.documentsLocation, "coordinates.txt");
                if (file) {
                    var text = latitudeText.text + ", " + longitudeText.text + ", " + altitudeText.text + "\n";
                    file.write(text);

                    // Отправляем оповещение в виде Intent
                    var intent = Qt.createQmlObject('import QtAndroid 1.0; Intent {}', mainWindow, "intent");
                    intent.action = "android.intent.action.VIEW";
                    intent.setData(android.net.Uri.parse("content://settings/system"));
                    QtAndroid.startActivity(intent, function() {
                        console.log("Координаты записаны в файл!");
                    });
                }
            }
        }
    }
}
