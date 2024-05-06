import QtQuick
import QtQuick.Controls
import QtQuick.Layouts
import QtQuick.Window

Window {
    width: 360
    height: 520
    visible: true
    title: qsTr("Lab25 Bondarenko")

    property int sliderValue: 50
    property real progressBarValue: 0.5

    // Свойства для отслеживания координат начала и конца жеста
    property real startX: 0
    property real startY: 0
    property real endX: 0
    property real endY: 0

    // Флаг, указывающий, идет ли сейчас жест
    property bool gestureInProgress: false
    property bool gestureStarted: false
    property var gesturePath: []

    // Страницы приложения (пока что просто их номера)
    property int currentPage: 1

    Rectangle {
        anchors.fill: parent

        StackView {
            id: stackView
            anchors.fill: parent
            initialItem: sliderComponent // Указываем начальный компонент
        }

        Component {
            id: sliderComponent

            Rectangle {
                id: sliderContainer

                Slider {
                    id: slider
                    width: parent.width * 0.8
                    anchors.centerIn: parent
                    anchors.horizontalCenter: parent.horizontalCenter

                    from: 0
                    to: 100
                    value: sliderValue

                    onValueChanged: {
                        sliderValue = slider.value
                    }
                }

                Text {
                    id: sliderText
                    anchors.top: slider.bottom
                    anchors.horizontalCenter: parent.horizontalCenter
                    text: `Слайдер ` + sliderValue + `\nРазработал Бондаренко`
                    font.pixelSize: 14
                    horizontalAlignment: Text.AlignHCenter
                }
            }
        }

        Component {
            id: progressBarComponent

            Rectangle {
                id: progressBarContainer

                ProgressBar {
                    id: progressBar
                    width: parent.width * 0.8
                    anchors.centerIn: parent
                    anchors.horizontalCenter: parent.horizontalCenter

                    value: progressBarValue

                    onValueChanged: {
                        progressBarValue = progressBar.value
                    }
                }

                Text {
                    id: progressBarText
                    anchors.top: progressBar.bottom
                    anchors.horizontalCenter: parent.horizontalCenter
                    anchors.topMargin: 10
                    text: `Прогресс-бар ` + (progressBarValue * 100).toFixed(2) + ` %\nРазработал Бондаренко`
                    font.pixelSize: 14
                    horizontalAlignment: Text.AlignHCenter
                }
            }
        }

        MouseArea {
            id: gestureArea
            anchors.fill: parent

            // Обработчик начала жеста
            onPressed: {
                startX = mouseX
                startY = mouseY
                gestureInProgress = true
                gestureStarted = true
                gesturePath = []
                gesturePath.push({x: startX, y: startY})
            }

            // Обработчик движения мыши
            onPositionChanged: {
                if (gestureInProgress) {
                    endX = mouseX
                    endY = mouseY
                    gesturePath.push({x: mouseX, y:mouseY})
                }
            }

            // Обработчик окончания жеста
            onReleased: {
                if (gestureInProgress) {
                    gestureInProgress = false
                    gestureStarted = false
                    evaluateGesture()
                }
            }

            // Функция для оценки и выполнения жеста
            function evaluateGesture() {
                var gesture = ""
                for (var i = 1; i < gesturePath.length; ++i) {
                    var dx = gesturePath[i].x - gesturePath[i - 1].x
                    var dy = gesturePath[i].y - gesturePath[i - 1].y

                    if (dx > 0) {
                        gesture += ">"
                    } else if (dx < 0) {
                        gesture += "<"
                    }
                }
                if (gesture[0] === '<' && gesture[gesture.length - 1] === '>') {
                    // Обнаружен жест "<"
                    previousPage()
                } else if (gesture[0] === '>' && gesture[gesture.length - 1] === '<') {
                    // Обнаружен жест ">"
                    nextPage()
                }
            }

            // Функция для перехода на предыдущую страницу
            function previousPage() {
                // Убедитесь, что ваш логический блок учитывает ограничения (например, количество страниц)
                if (currentPage === 2) {
                    currentPage = 1; // Переключение обратно на страницу 1
                    console.log("Navigating to page:", currentPage)
                    stackView.replace(sliderComponent); // Используйте replace для замены содержимого стека
                } else {
                    console.log("Already on the first page or invalid page number")
                }
            }

            // Функция для перехода на следующую страницу
            function nextPage() {
                // Убедитесь, что ваш логический блок учитывает ограничения (например, количество страниц)
                if (currentPage === 1) {
                    currentPage = 2; // Переключение на страницу 2
                    console.log("Navigating to page:", currentPage)
                    stackView.replace(progressBarComponent); // Используйте replace для замены содержимого стека
                } else {
                    console.log("Already on the last page or invalid page number")
                }
            }
        }
    }

    PinchHandler {
        id: pinchHandler

        minimumScale: 0.1
        maximumScale: 5

        property real initialSliderValue
        property real initialProgressBarValue

        onActiveChanged: {
            if (active) {
                // Сохраняем текущее значение слайдера при начале жеста "щипок"
                pinchHandler.initialSliderValue = sliderValue;
                pinchHandler.initialProgressBarValue = progressBarValue;
            }
        }

        onScaleChanged: {

            // Проверяем, что начальное значение слайдера не равно нулю перед изменением значения
            if (pinchHandler.initialSliderValue !== 0) {
                // Изменяем значение слайдера в соответствии с масштабированием жеста "щипок"
                sliderValue = Math.max(Math.min(pinchHandler.initialSliderValue * scale, 100), 0);
                progressBarValue = Math.max(Math.min(pinchHandler.initialProgressBarValue * scale, 1), 0);
            }
            else {
                sliderValue = 1
                progressBarValue = 0.01
            }
        }
    }
}
