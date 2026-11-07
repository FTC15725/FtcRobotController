package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.subsystems.shooterSubsystem;

@TeleOp
public class MecanumTeleOp extends LinearOpMode {
    private final shooterSubsystem shooter = new shooterSubsystem();

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        shooter.init(hardwareMap);

        waitForStart();

        if (isStopRequested()) return;

        boolean aLastState = false;
        boolean flywheelRunning = false;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (y + x + rx) / denominator;
            double backLeftPower = (y - x + rx) / denominator;
            double frontRightPower = (y - x - rx) / denominator;
            double backRightPower = (y + x - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            // Shooter / Intake controls (gamepad2)
            boolean aCurrentState = gamepad2.a;
            if (aCurrentState && !aLastState) {
                flywheelRunning = !flywheelRunning;
            }
            aLastState = aCurrentState;

            shooter.setFlywheelPower(flywheelRunning ? 1 : 0.0);
            shooter.setIndexerPower(gamepad2.right_trigger);
            shooter.setIntakePower(gamepad2.right_bumper ? 1.0 : 0.0);
        }
    }
}