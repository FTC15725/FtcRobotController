package org.firstinspires.ftc.teamcode.teleops;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.subsystems.shooterSubsystem;

@TeleOp
public class sudoTeleOP26 extends OpMode {
    private Follower follower;
    private shooterSubsystem shooter = new shooterSubsystem();

    private boolean aLastState = false;
    private boolean flywheelRunning = false;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        shooter.init(hardwareMap);
    }

    @Override
    public void start() {
        if (follower != null && OpModeStorage.autonomousEndPose != null) {
            follower.setPose(OpModeStorage.autonomousEndPose);
            follower.update();
        }
    }

    @Override
    public void loop() {
        // Drivetrain control (gamepad1)
        double forward = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;
        if (follower != null) {
            follower.manual(forward, lateral, turn);
            follower.update();
        }

        // Shooter / Intake controls (gamepad2)
        // A button toggles flywheel motor (uses PIDF)
        boolean aCurrentState = gamepad2.a;
        if (aCurrentState && !aLastState) {
            flywheelRunning = !flywheelRunning;
        }
        aLastState = aCurrentState;

        shooter.setFlywheelPower(flywheelRunning ? 0.8 : 0.0);

        // R2 (right_trigger) / L2 (left_trigger) does the funnel / indexer (LT reversed RT)
        double indexerPower = gamepad2.right_trigger - gamepad2.left_trigger;
        shooter.setIndexerPower(indexerPower);

        // RB (right_bumper) / LB (left_bumper) does the intake (LB reversed RB)
        double intakePower = gamepad2.right_bumper ? 1.0 : (gamepad2.left_bumper ? -1.0 : 0.0);
        shooter.setIntakePower(intakePower);
    }
}
