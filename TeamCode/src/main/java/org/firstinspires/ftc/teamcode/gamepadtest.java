package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class gamepadtest extends LinearOpMode {

    private Servo testServo;

    // Define target servo positions (range: 0.0 to 1.0)
    private static final double POSITION_MIN = 0.0;
    private static final double POSITION_MAX = 1.0;
    private static final double POSITION_CENTER = 0.5;

    // Increment step size for smooth incremental control
    private static final double INCREMENT = 0.01;

    @Override
    public void runOpMode() {
        // Hardware Mapping
        testServo = hardwareMap.get(Servo.class, "testServo");

        // Set initial position to center
        double currentPosition = POSITION_CENTER;
        testServo.setPosition(currentPosition);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Preset controls using D-pad buttons
            if (gamepad1.dpad_up) {
                currentPosition = POSITION_MAX;
            } else if (gamepad1.dpad_down) {
                currentPosition = POSITION_MIN;
            } else if (gamepad1.dpad_left || gamepad1.dpad_right) {
                currentPosition = POSITION_CENTER;
            }

            // Incremental control using bumpers (Hold to move back/forth smoothly)
            if (gamepad1.right_bumper && currentPosition < POSITION_MAX) {
                currentPosition += INCREMENT;
            } else if (gamepad1.left_bumper && currentPosition > POSITION_MIN) {
                currentPosition -= INCREMENT;
            }

            // Write final position to servo
            testServo.setPosition(currentPosition);

            // Output telemetry to Driver Station
            telemetry.addData("Servo Target Position", "%.2f", currentPosition);
            telemetry.addData("Actual Servo Position", "%.2f", testServo.getPosition());
            telemetry.update();

            // Short sleep to prevent rapid over-stepping during bumper holds
            sleep(20);
        }
    }
}