/*
A color sensor will be used to steer the robot, where sensing "red" will turn
to the right, and seeing "blue" will turn to the left. Seeing equal amounts of
red and blue, or nothing at all will cause the robot to stop.

Required hardware:
  1. A color sensor should be connected to an I2C port and added to the robot
     configuration with the name "color".
  2. Four motors should be connected to motor controllers and named for their
     position on the robot: "mFL", "mFR", "mBL", "mBR", where "F" means front,
     "B" means back, "R" means right, and "L" means left.

This is a TeleOp OpMode so that it does not time out.
*/

package com.robotechnix.examples;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
@SuppressWarnings({"unused"})
public class ColorSensorSteering extends OpMode {
    private ColorSensor mColorSensor;
    private DcMotor mFL, mFR, mBL, mBR;

    @Override
    public void init() {
        mColorSensor = hardwareMap.colorSensor.get("color");

        mFL = hardwareMap.dcMotor.get("mFL");
        mFL.setDirection(DcMotorSimple.Direction.FORWARD);
        mFR = hardwareMap.dcMotor.get("mFR");
        mFR.setDirection(DcMotorSimple.Direction.REVERSE);
        mBL = hardwareMap.dcMotor.get("mBL");
        mBL.setDirection(DcMotorSimple.Direction.FORWARD);
        mBR = hardwareMap.dcMotor.get("mBR");
        mBR.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        int blue = mColorSensor.blue();
        int red = mColorSensor.red();

        if (blue == red || (blue < 5 && red < 5)) {
            // Both values are equal, or both are small. Stop.
            mFL.setPower(0.0);
            mFR.setPower(0.0);
            mBL.setPower(0.0);
            mBR.setPower(0.0);
        } else if (blue > red) {
            // Seeing more blue than red, drive left.
            mFL.setPower(0.0);
            mFR.setPower(0.3);
            mBL.setPower(0.0);
            mBR.setPower(0.3);
        } else if (red > blue) {
            // Seeing more red than blue, drive right.
            mFL.setPower(0.3);
            mFR.setPower(0.0);
            mBL.setPower(0.3);
            mBR.setPower(0.0);
        }
    }
}
