/*
Drive the robot in a "box" pattern with right turns. This uses elapsed time
for both the turn and drive moves, so the TURN_TIME and DRIVE_TIME may need
to be adjusted to match the speed and agility of a particular robot. This was
tested with a direct-drive robot using NeveRest 40 motors and mecanum wheels
and makes a nearly perfect box within about 10 feet of space.

Required hardware:
  1. Four motors should be connected to motor controllers and named for their
     position on the robot: "mFL", "mFR", "mBL", "mBR", where "F" means front,
     "B" means back, "R" means right, and "L" means left.
*/

package com.robotechnix.examples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous
@SuppressWarnings({"unused"})
public class AutonomousElapsedTimeBoxPattern extends LinearOpMode {
    // The amount of time that it takes to turn 90°. This can be tuned until
    // it's as close as possible to a 90° turn.
    private static final double TURN_TIME = 1.0;

    // The amount of time to drive straight in between turns; this determines
    // the overall size of the box.
    private static final double DRIVE_TIME = 2.0;

    private static final double STOP_POWER = 0.0;
    private static final double TURN_POWER = 0.3;
    private static final double DRIVE_POWER = 0.5;

    private DcMotor mFL, mFR, mBL, mBR;

    private void doInitialization() {
        mFL = hardwareMap.dcMotor.get("mFL");
        mFL.setDirection(DcMotorSimple.Direction.FORWARD);
        mFR = hardwareMap.dcMotor.get("mFR");
        mFR.setDirection(DcMotorSimple.Direction.REVERSE);
        mBL = hardwareMap.dcMotor.get("mBL");
        mBL.setDirection(DcMotorSimple.Direction.FORWARD);
        mBR = hardwareMap.dcMotor.get("mBR");
        mBR.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void doStop() {
        // Stop movement.
        mFL.setPower(STOP_POWER);
        mBL.setPower(STOP_POWER);
        mFR.setPower(STOP_POWER);
        mBR.setPower(STOP_POWER);
    }

    private void doRightTurn() {
        ElapsedTime elapsedTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

        // Start a turn/spin to the right by powering the left wheels forwards
        // and the right wheels backwards.
        mFL.setPower(TURN_POWER);
        mBL.setPower(TURN_POWER);
        mFR.setPower(-TURN_POWER);
        mBR.setPower(-TURN_POWER);

        // Wait for TURN_TIME to elapse.
        while (!isStopRequested() && elapsedTime.time() < TURN_TIME) {
            // Do nothing.
        }

        // Stop movement.
        doStop();
    }

    private void doStraightDrive() {
        ElapsedTime elapsedTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

        // Start driving forwards by powering all wheels equally.
        mFL.setPower(DRIVE_POWER);
        mBL.setPower(DRIVE_POWER);
        mFR.setPower(DRIVE_POWER);
        mBR.setPower(DRIVE_POWER);

        // Wait for TURN_TIME to elapse.
        while (!isStopRequested() && elapsedTime.time() < DRIVE_TIME) {
            // Do nothing.
        }

        // Stop movement.
        doStop();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // Run our initialization code (in its own method to keep this one cleaner).
        doInitialization();

        // Wait for the driver to press the "Start" button.
        waitForStart();

        // 1. Drive straight ahead.
        doStraightDrive();

        // 2. Turn to the right 90°.
        doRightTurn();

        // 3. Drive straight ahead.
        doStraightDrive();

        // 4. Turn to the right 90° (now total 180°).
        doRightTurn();

        // 5. Drive straight ahead (parallel to move #1).
        doStraightDrive();

        // 6. Turn to the right 90° (now total 270°).
        doRightTurn();

        // 7. Drive straight ahead (parallel to move #3).
        doStraightDrive();

        // 8. Turn to the right 90° (now total 360° - hopefully).
        doRightTurn();

        // Make sure the robot is stopped.
        doStop();

        // Exit here, ending the program.
    }
}
