package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.*;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.*;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
public class sudoOP26 extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 90);
    private final Pose shoot1 = poseFactory.of(57.485, 19.6649, 90);
    private final Pose flower1 = poseFactory.of(9.2699, 47.6927, 180);
    private final Pose shoot2 = poseFactory.of(57.7564, 105.7024, 270);
    private final Pose flower2 = poseFactory.of(47.1499, 133.1904, 90);
    private final Pose shoot3 = poseFactory.of(58.0872, 105.7891, 270);
    private final Pose gotoParkingStop = poseFactory.of(9.2984, 106.2736, 270);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, shoot1()),
                follow(follower, flower1()),
                follow(follower, shoot2()),
                follow(follower, flower2()),
                follow(follower, shoot3()),
                follow(follower, gotoParkingStop())
        );
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

        waitForStart();
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path shoot1() {
        return line(start, shoot1).linear(start, shoot1);
    }

    public Path flower1() {
        return line(shoot1, flower1).linear(shoot1, flower1);
    }

    public Path shoot2() {
        return line(flower1, shoot2).linear(flower1, shoot2);
    }

    public Path flower2() {
        return line(shoot2, flower2).linear(shoot2, flower2);
    }

    public Path shoot3() {
        return line(flower2, shoot3).linear(flower2, shoot3);
    }

    public Path gotoParkingStop() {
        return line(shoot3, gotoParkingStop).linear(shoot3, gotoParkingStop);
    }
}