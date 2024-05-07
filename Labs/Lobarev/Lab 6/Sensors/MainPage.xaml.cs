using Microsoft.Maui.Devices.Sensors;
using System.Globalization;
using System.Text;
using System.Timers;
using Timer = System.Timers.Timer;


namespace Sensors;

public partial class MainPage : ContentPage
{
    public MainPage()
    {
        InitializeComponent();
        CultureInfo.CurrentCulture = CultureInfo.InvariantCulture;


        ToggleAccelerometer();
        ToggleBarometer();
        ToggleCompass();
        //ToggleShake();
        ToggleGyroscope();
        ToggleMagnetometer();
        ToggleOrientation();


        Func(AccelLabel, AccelToggle);
        Func(BarometerLabel, BarometerToggle);
        Func(CompassLabel, CompassToggle);
        Func(ShakeLabel, ShakeToggle);
        Func(GyroscopeLabel, GyroscopeToggle);
        Func(MagnetometerLabel, MagnetometerToggle);
        Func(OrientationLabel, OrientationToggle);












        void Func(Label label, Switch sw)
        {
            TapGestureRecognizer tapGestureRecognizer = new TapGestureRecognizer();
            tapGestureRecognizer.Tapped += (sender, e) => 
            {
                sw.IsToggled = !sw.IsToggled;
            };
            label.GestureRecognizers.Add(tapGestureRecognizer);
        };
    }

    private Timer Timer { get; set; } = null;

    public void ToggleAccelerometer()
    {
        if (Accelerometer.Default.IsSupported)
        {
            if (!Accelerometer.Default.IsMonitoring)
            {
                // Turn on accelerometer
                Accelerometer.Default.ReadingChanged += Accelerometer_ReadingChanged;
                Accelerometer.Default.Start(SensorSpeed.UI);
            }
            else
            {
                // Turn off accelerometer
                Accelerometer.Default.Stop();
                Accelerometer.Default.ReadingChanged -= Accelerometer_ReadingChanged;
            }
        }
        else
        {
            AccelToggle.IsToggled = false;
            AccelToggle.IsEnabled = false;
            AccelLabel.Text = AccelLabel.Text.Replace("Offline", "\nNot Available");
            AccelLabel.TextColor = Colors.Red;
        }
    }

    private void Accelerometer_ReadingChanged(object? sender, AccelerometerChangedEventArgs e)
    {
        // Update UI Label with accelerometer state
        var data = e.Reading.Acceleration;
        var str = $"X: {data.X:F3}\nY: {data.Y:F3}\nZ: {data.Z:F3}";

        AccelLabel.TextColor = Colors.Green;
        AccelLabel.Text = $"Acceleration: \n{str}";
    }

    public void ToggleBarometer()
    {
        if (Barometer.Default.IsSupported)
        {
            if (!Barometer.Default.IsMonitoring)
            {
                // Turn on barometer
                Barometer.Default.ReadingChanged += Barometer_ReadingChanged;
                Barometer.Default.Start(SensorSpeed.UI);
            }
            else
            {
                // Turn off barometer
                Barometer.Default.Stop();
                Barometer.Default.ReadingChanged -= Barometer_ReadingChanged;
            }
        }
        else
        {
            BarometerToggle.IsToggled = false;
            BarometerToggle.IsEnabled = false;
            BarometerLabel.Text = BarometerLabel.Text.Replace("Offline", "\nNot Available");
            BarometerLabel.TextColor = Colors.Red;
        }
    }

    private void Barometer_ReadingChanged(object? sender, BarometerChangedEventArgs e)
    {
        // Update UI Label with barometer state
        BarometerLabel.TextColor = Colors.Green;
        BarometerLabel.Text = $"Barometer: \n{e.Reading.PressureInHectopascals:F3} hPa";
    }

    private void ToggleCompass()
    {
        if (Compass.Default.IsSupported)
        {
            if (!Compass.Default.IsMonitoring)
            {
                // Turn on compass
                Compass.Default.ReadingChanged += Compass_ReadingChanged;
                Compass.Default.Start(SensorSpeed.UI, applyLowPassFilter: true);
            }
            else
            {
                // Turn off compass
                Compass.Default.Stop();
                Compass.Default.ReadingChanged -= Compass_ReadingChanged;
            }
        }
        else
        {
            CompassToggle.IsToggled = false;
            CompassToggle.IsEnabled = false;
            CompassLabel.Text = CompassLabel.Text.Replace("Offline", "\nNot Available");
            CompassLabel.TextColor = Colors.Red;
        }
    }

    private void Compass_ReadingChanged(object? sender, CompassChangedEventArgs e)
    {
        // Update UI Label with compass state
        CompassLabel.TextColor = Colors.Green;
        CompassLabel.Text = $"Compass: \nMagnetic North: \n{e.Reading.HeadingMagneticNorth:F5}";
    }


    private void ToggleShake()
    {
        if (Accelerometer.Default.IsSupported)
        {
            if (!Accelerometer.Default.IsMonitoring)
            {
                // Turn on accelerometer
                Accelerometer.Default.ShakeDetected += Accelerometer_ShakeDetected;
                Accelerometer.Default.Start(SensorSpeed.Game);
            }
            else
            {
                // Turn off accelerometer
                Accelerometer.Default.Stop();
                Accelerometer.Default.ShakeDetected -= Accelerometer_ShakeDetected;
            }
        }
        else
        {
            ShakeLabel.Text = "Shake Offline";

            ShakeToggle.IsToggled = false;
            ShakeToggle.IsEnabled = false;

            ShakeLabel.Text = ShakeLabel.Text.Replace("Offline", "\nNot Available");
            ShakeLabel.TextColor = Colors.Red;
        }
    }

    private void Accelerometer_ShakeDetected(object? sender, EventArgs e)
    {
        // Update UI Label with a "shaked detected" notice, in a randomized color
        int r = Random.Shared.Next(256);
        int g = Random.Shared.Next(256);
        int b = Random.Shared.Next(256);

        ShakeLabel.TextColor = new Color(r, g, b);
        ShakeLabel.Text = $"Shake detected";

        VertStack.BackgroundColor = new Color(256 - r, 256 - g, 256 - b);

        if (Timer is null)
        {
            Timer = new Timer(TimeSpan.FromSeconds(3))
            {
                AutoReset = false
            };
            Timer.Elapsed += Timer_Elapsed;
            Timer.Start();
        }
        else
        {
            Timer.Interval = 3000.0;
        }
    }

    private void Timer_Elapsed(object? sender, ElapsedEventArgs e)
    {
        VertStack.BackgroundColor = Colors.Transparent;
        Timer = null;
    }

    private void ToggleGyroscope()
    {
        if (Gyroscope.Default.IsSupported)
        {
            if (!Gyroscope.Default.IsMonitoring)
            {
                // Turn on gyroscope
                Gyroscope.Default.ReadingChanged += Gyroscope_ReadingChanged;
                Gyroscope.Default.Start(SensorSpeed.UI);
            }
            else
            {
                // Turn off gyroscope
                Gyroscope.Default.Stop();
                Gyroscope.Default.ReadingChanged -= Gyroscope_ReadingChanged;
            }
        }
        else
        {
            var label = GyroscopeLabel;
            var toggle = GyroscopeToggle;

            toggle.IsToggled = false;
            toggle.IsEnabled = false;

            label.Text = label.Text.Replace("Offline", "\nNot Available");
            label.TextColor = Colors.Red;
        }
    }

    private void Gyroscope_ReadingChanged(object? sender, GyroscopeChangedEventArgs e)
    {
        // Update UI Label with gyroscope state
        var data = e.Reading.AngularVelocity;
        var str = $"X: {data.X:F3}\nY: {data.Y:F3}\nZ: {data.Z:F3}";

        GyroscopeLabel.TextColor = Colors.Green;
        GyroscopeLabel.Text = $"Gyroscope: \n{str}";
    }

    private void ToggleMagnetometer()
    {
        if (Magnetometer.Default.IsSupported)
        {
            if (!Magnetometer.Default.IsMonitoring)
            {
                // Turn on magnetometer
                Magnetometer.Default.ReadingChanged += Magnetometer_ReadingChanged;
                Magnetometer.Default.Start(SensorSpeed.UI);
            }
            else
            {
                // Turn off magnetometer
                Magnetometer.Default.Stop();
                Magnetometer.Default.ReadingChanged -= Magnetometer_ReadingChanged;
            }
        }
        else
        {
            var label = MagnetometerLabel;
            var toggle = MagnetometerToggle;

            toggle.IsToggled = false;
            toggle.IsEnabled = false;

            label.Text = label.Text.Replace("Off", "\nNot Available");
            label.TextColor = Colors.Red;
        }
    }

    private void Magnetometer_ReadingChanged(object? sender, MagnetometerChangedEventArgs e)
    {
        // Update UI Label with magnetometer state
        var data = e.Reading.MagneticField;
        var str = $"X: {data.X:F3}\nY: {data.Y:F3}\nZ: {data.Z:F3}";

        MagnetometerLabel.TextColor = Colors.Green;
        MagnetometerLabel.Text = $"Magnetometer: \n{str}";
    }

    private void ToggleOrientation()
    {
        if (OrientationSensor.Default.IsSupported)
        {
            if (!OrientationSensor.Default.IsMonitoring)
            {
                // Turn on orientation
                OrientationSensor.Default.ReadingChanged += Orientation_ReadingChanged;
                OrientationSensor.Default.Start(SensorSpeed.UI);
            }
            else
            {
                // Turn off orientation
                OrientationSensor.Default.Stop();
                OrientationSensor.Default.ReadingChanged -= Orientation_ReadingChanged;
            }
        }
        else
        {
            var label = OrientationLabel;
            var toggle = OrientationToggle;

            toggle.IsToggled = false;
            toggle.IsEnabled = false;

            label.Text = label.Text.Replace("Offline", "\nNot Available");
            label.TextColor = Colors.Red;
        }
    }

    private void Orientation_ReadingChanged(object? sender, OrientationSensorChangedEventArgs e)
    {
        // Update UI Label with orientation state
        var data = e.Reading.Orientation;
        var str = $"X: {data.X:F3}\nY: {data.Y:F3}\nZ: {data.Z:F3}\nW: {data.W:F3}";

        OrientationLabel.TextColor = Colors.Green;
        OrientationLabel.Text = $"Orientation: \n{str}";
    }

    private void VibrateStartButton_Clicked(object? sender, EventArgs e)
    {
        int milliSecondsToVibrate = Random.Shared.Next(1000, 3000);
        TimeSpan vibrationLength = TimeSpan.FromMilliseconds(milliSecondsToVibrate);

        Vibration.Default.Vibrate(vibrationLength);
    }

    private void VibrateStopButton_Clicked(object? sender, EventArgs e) =>
        Vibration.Default.Cancel();














    private void AccelToggle_Toggled(object sender, ToggledEventArgs e)
    {
        if (AccelToggle.IsToggled)
        {
            ShakeToggle.IsToggled = false;
        }

        ToggleAccelerometer();
        AccelLabel.Text = "Accelerometer Off";
    }

    private void BarometerToggle_Toggled(object sender, ToggledEventArgs e)
    {
        ToggleBarometer();

        if(!BarometerToggle.IsToggled)
        {
            BarometerLabel.Text = "Barometer Off";
        }
    }

    private void CompassToggle_Toggled(object sender, ToggledEventArgs e)
    {
        ToggleCompass();

        if (!CompassToggle.IsToggled)
        {
            CompassLabel.Text = "Compass Off";
        }
    }

    private void ShakeToggle_Toggled(object sender, ToggledEventArgs e)
    {
        if (ShakeToggle.IsToggled)
        {
            AccelToggle.IsToggled = false;
            ShakeLabel.Text = "Shake not detected";
        }
        
        ToggleShake();

        if (!ShakeToggle.IsToggled)
        {
            ShakeLabel.Text = "Shake Off";
        }
    }

    private void GyroscopeToggle_Toggled(object sender, ToggledEventArgs e)
    {
        ToggleGyroscope();

        if (!GyroscopeToggle.IsToggled)
        {
            GyroscopeLabel.Text = "Gyroscope Off";
        }
    }

    private void MagnetometerToggle_Toggled(object sender, ToggledEventArgs e)
    {
        ToggleMagnetometer();

        if (!MagnetometerToggle.IsToggled)
        {
            MagnetometerLabel.Text = "Magnetometer Off";
        }
    }

    private void OrientationToggle_Toggled(object sender, ToggledEventArgs e)
    {
        ToggleOrientation();

        if (!OrientationToggle.IsToggled)
        {
            OrientationLabel.Text = "Orientation Off";
        }
    }
}
