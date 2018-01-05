/*
The outputs from a color sensor will be read and then displayed on the driver
station phone using telemetry. This could be useful, for example, to check
what values the color sensor produces for different objects.

Required hardware:
  1. A color sensor should be connected to an I2C port and added to the robot
     configuration with the name "color".

This is a TeleOp OpMode so that it does not time out.
*/

package com.robotechnix.examples;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp
@SuppressWarnings({"unused"})
public class ColorSensorTelemetry extends OpMode {
    private ColorSensor mColorSensor;

    @Override
    public void init() {
        mColorSensor = hardwareMap.colorSensor.get("color");
    }

    @Override
    public void loop() {
        telemetry.addData("1. alpha", mColorSensor.alpha());
        telemetry.addData("2. red", mColorSensor.red());
        telemetry.addData("3. green", mColorSensor.green());
        telemetry.addData("4. blue", mColorSensor.blue());
        telemetry.addData("5. argb", mColorSensor.argb());
        telemetry.update();
    }
}
