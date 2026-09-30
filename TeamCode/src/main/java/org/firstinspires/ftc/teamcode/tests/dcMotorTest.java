package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class dcMotorTest extends LinearOpMode {
    private static final String[] MOTOR_NAMES = {
            "frontLeftMotor", "backLeftMotor", "frontRightMotor", "backRightMotor"
    };

    private static final double MAX_POWER = 1.0;
    private static final double INCREMENT = 0.05;

    private DcMotor[] motors;

    @Override
    public void runOpMode() {
        motors = new DcMotor[MOTOR_NAMES.length];
        for (int i = 0; i < MOTOR_NAMES.length; i++) {
            motors[i] = hardwareMap.get(DcMotor.class, MOTOR_NAMES[i]);

            motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        motors[2].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[3].setDirection(DcMotorSimple.Direction.REVERSE);

        int selected = 0;
        double power = 0.0;
        boolean yWasPressed = false;

        telemetry.addData("Status", "Initialized. Press Y to cycle motor (last = ALL).");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.y && !yWasPressed) {
                selected = (selected + 1) % (MOTOR_NAMES.length + 1);
                power = 0.0;
            }
            yWasPressed = gamepad1.y;

            if (gamepad1.dpad_up) {
                power = MAX_POWER;
            } else if (gamepad1.dpad_down) {
                power = -MAX_POWER;
            } else if (gamepad1.dpad_left || gamepad1.dpad_right) {
                power = 0.0;
            }

            if (gamepad1.right_bumper && power < MAX_POWER) {
                power += INCREMENT;
            } else if (gamepad1.left_bumper && power > -MAX_POWER) {
                power -= INCREMENT;
            }

            if (gamepad1.x) {
                for (int i = 0; i < motors.length; i++) {
                    if (selected == motors.length || selected == i) {
                        motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    }
                }
            }

            power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

            for (int i = 0; i < motors.length; i++) {
                motors[i].setPower(selected == motors.length || selected == i ? power : 0.0);
            }

            String selName = selected == motors.length ? "ALL" : MOTOR_NAMES[selected];
            telemetry.addData("Selected", "%s   Power: %.2f", selName, power);
            telemetry.addLine("Motor              Power  Encoder");
            for (int i = 0; i < motors.length; i++) {
                telemetry.addData("", "%-15s %5.2f  %6d",
                        MOTOR_NAMES[i], motors[i].getPower(), motors[i].getCurrentPosition());
            }
            telemetry.update();

            sleep(20);
        }

        for (DcMotor motor : motors) {
            motor.setPower(0);
        }
    }
}
