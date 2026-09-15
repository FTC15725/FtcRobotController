package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo Test Indefinite", group = "Test")
public class ServoTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        
        /* 
         * NOTE: There are two main types of servos in FTC:
         * 1. Continuous Rotation (CR) Servos: They spin indefinitely like a motor.
         * 2. Standard Servos: They move to a precise position (0 to 1) and stop.
         *
         * Choose the option below that matches your servo type and configuration.
         */

        // ==========================================
        // OPTION 1: Continuous Rotation (CR) Servo
        // ==========================================
        CRServo testServo = hardwareMap.crservo.get("testServo");

        // ==========================================
        // OPTION 2: Standard Servo (Sweeping back and forth)
        // If you are using a standard servo, uncomment the line below and comment out Option 1.
        // ==========================================
        // Servo testServo = hardwareMap.servo.get("testServo");

        telemetry.addData("Status", "Initialized. Ready to start.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            
            // For Continuous Rotation Servo (Option 1):
            // Set power to 1.0 to spin forward indefinitely, -1.0 to spin backward, or 0.0 to stop.
            testServo.setPower(1.0);
            telemetry.addData("Status", "CR Servo spinning indefinitely at full power");
            
            // For Standard Servo (Option 2):
            // To make a standard servo "spin" indefinitely, we oscillate it between 0 and 1.
            // Uncomment the lines below if using a standard position servo.
            /*
            testServo.setPosition(0.0);
            telemetry.addData("Status", "Moving to 0.0");
            telemetry.update();
            sleep(2000);
            
            if (!opModeIsActive()) break;
            
            testServo.setPosition(1.0);
            telemetry.addData("Status", "Moving to 1.0");
            telemetry.update();
            sleep(2000);
            */

            telemetry.update();
        }
    }
}
