package org.firstinspires.ftc.teamcode.teleops;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.OpModeStorage;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp
public class sudoTeleOP26 extends OpMode {
    private Follower follower;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void start() {
        follower.setPose(OpModeStorage.autonomousEndPose);
        follower.update();
    }

    @Override
    public void loop() {
        double forward = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;
        follower.manual(forward, lateral, turn);
        follower.update();
    }
}
