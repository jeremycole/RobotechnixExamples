/*
Drive the robot back and forth until running out of time to demonstrate how
to use a RuntimeException to allow for immediate and safe stopping of the robot
when the "Stop" button is pressed (or the 30 second autonomous mode timer
expires).

See the AutonomousElapsedTimeBoxPattern example for more explanation of the
mechanics of Autonomous. This example focuses primarily on the new exception
concept introduced here.

The entire work of the LinearOpMode is inside of a try/catch block so that an
exception thrown from any of the called methods will transfer control to the
catch block below, which can safely handle it. A new StopImmediatelyException
is defined to specifically signal that a "Stop" was requested (as opposed to
other exceptions). Any code implementing a wait of any sort - for a timer, an
encoder, etc., should call shouldKeepRunning() during each step of its wait,
which will automatically throw StopImmediatelyException if the robot should
stop.

Note that once StopImmediatelyException has been caught, an early "return"
causes runOpMode() (and anything it has called) to exit. This means that your
autonomous code (doing actual robot movements) does not have to continually
keep checking if it should progress to the next step.

Required hardware:
  1. Four motors should be connected to motor controllers and named for their
     position on the robot: "mFL", "mFR", "mBL", "mBR", where "F" means front,
     "B" means back, "R" means right, and "L" means left.
*/

package com.robotechnix.examples;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Locale;

@Autonomous
@SuppressWarnings({"unused", "WeakerAccess"})
public class AutonomousStopImmediately extends LinearOpMode {
    // A "log tag" to include in log messages here. It is helpful if this is
    // unique enough to find easily find in the logs without having to filter
    // through many unrelated log entries. "FTCnnnnn" where nnnnn is the team
    // number should be unique enough!
    final private static String LOG_TAG = "FTC99999";

    // An exception to throw to indicate that "Stop" was pressed (or fired
    // automatically due to timer expiration). The robot should stop
    // immediately to avoid penalty points or crashing. This is a subclass of
    // RuntimeException so that it can be thrown by methods without needing to
    // declare it with 'throws' everywhere that shouldKeepRunning() could be
    // used.
    public class StopImmediatelyException extends RuntimeException {
        public StopImmediatelyException() { super(); }
    }

    // If stop was requested, throw a StopImmediatelyException which will be
    // caught by runOpMode to stop the robot immediately. Check this anywhere
    // that you would have checked opModeIsActive()!
    private boolean shouldKeepRunning() {
        if(!opModeIsActive())
            throw new StopImmediatelyException();
        return true;
    }

    // The runOpMode() required by the LinearOpMode class. In order to keep
    // things as easy as possible to understand, just call a few other methods
    // implemented below: doInitialization, doAutonomous, and doStopEverything.
    @Override
    public void runOpMode() throws InterruptedException {
        try {
            // Initialize the robot.
            doInitialization();

            // Wait for the start button to be pressed on the Driver Station.
            waitForStart();

            // The waitForStart() method also returns when "Stop" is pressed,
            // so check if the OpMode should be active before continuing.
            if (!opModeIsActive())
                return;

            // Run the method containing all of the real autonomous moves.
            doAutonomous();

            // Stop everything after exit. (Should never be reached because the
            // OpMode will be canceled by the autonomous timer due to the below
            // doAutonomous() method intentionally taking too long.)
            doStopEverything();
        } catch (Throwable t) {
            // This exception was expected due to timer expiration or "Stop"
            // button being pressed. Log it, stop the robot, and return from
            // runOpMode().
            if (t instanceof StopImmediatelyException) {
                Log.i(LOG_TAG, "StopImmediatelyException exception caught!");
                doStopEverything();
                return;
            }

            // Unexpected exception (such as a NullPointerException). Log it,
            // and then re-throw a RuntimeException to let the program crash.
            // Without knowing what the exception is, trying to stop the robot
            // here could just make things worse.
            Log.e(LOG_TAG, "Unexpected exception caught!", t);

            // This is already an instance of a RuntimeException, so just
            // rethrow it.
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }

            // This was some other Throwable, so wrap it in a RuntimeException
            // to force it to crash the program.
            throw new RuntimeException(t);
        }
    }

    // Beyond this point would be customized for the specific robot design and
    // autonomous strategy desired.

    private static final double STOP_POWER = 0.0;
    private static final double DRIVE_POWER = 0.2;

    private DcMotor mFL, mFR, mBL, mBR;

    // Initialize the robot.
    private void doInitialization() {
        Log.i(LOG_TAG, "doInitialization()");

        mFL = hardwareMap.dcMotor.get("mFL");
        mFL.setDirection(DcMotorSimple.Direction.FORWARD);
        mFR = hardwareMap.dcMotor.get("mFR");
        mFR.setDirection(DcMotorSimple.Direction.REVERSE);
        mBL = hardwareMap.dcMotor.get("mBL");
        mBL.setDirection(DcMotorSimple.Direction.FORWARD);
        mBR = hardwareMap.dcMotor.get("mBR");
        mBR.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    // Do anything necessary to stop the robot safely. This could include
    // stopping drive motors, repositioning servos, etc.
    private void doStopEverything() {
        Log.i(LOG_TAG, "doStopEverything()");

        doStopDriving();
    }

    // Stop movement of the drive motors.
    private void doStopDriving() {
        Log.i(LOG_TAG, "doStopDriving()");

        mFL.setPower(STOP_POWER);
        mBL.setPower(STOP_POWER);
        mFR.setPower(STOP_POWER);
        mBR.setPower(STOP_POWER);
    }

    // Drive straight (forward or backward depending on the sign of the drive
    // power) at a given power level, for a given time.
    private void doStraightDrive(double drive_power, double drive_time) {
        Log.i(LOG_TAG, String.format(Locale.US, "doStraightDrive(%.1f, %.1f)", drive_power, drive_time));

        ElapsedTime elapsedTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);

        // Start driving forwards by powering all wheels equally.
        mFL.setPower(drive_power);
        mBL.setPower(drive_power);
        mFR.setPower(drive_power);
        mBR.setPower(drive_power);

        // Wait for drive_time to elapse.
        while (shouldKeepRunning() && elapsedTime.time() < drive_time) {
            // Do nothing else, just wait.
        }

        // Stop movement.
        doStopDriving();
    }

    // The actual autonomous instructions that you want your robot to follow.
    // This is just a relatively simple and safe example driving back and forth
    // and "accidentally" running over time.
    private void doAutonomous() {
        Log.i(LOG_TAG, "doAutonomous()");

        // Drive forwards and backwards a few times to simulate an autonomous.
        // Note that this *intentionally* adds up to more than 30 seconds so
        // that the Autonomous OpMode timer will be forced to cancel the OpMode.
        for (int i=1; i <= 35; i++) {
            Log.i(LOG_TAG, "doAutonomous(): Loop " + i);
            doStraightDrive(DRIVE_POWER, 0.5);
            doStraightDrive(-DRIVE_POWER, 0.5);
        }

        // This should not be reached if everything is working well, because
        // the timer will have expired by now.
        Log.i(LOG_TAG, "doAutonomous(): Completed! (Whaaat?!)");
    }
}
