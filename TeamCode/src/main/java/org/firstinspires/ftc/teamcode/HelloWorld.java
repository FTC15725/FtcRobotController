package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class HelloWorld extends OpMode {

    @Override
    public void init() {
        telemetry.addData("Hello", "World!");
    }

    @Override
    public void loop() {
        for (int i = 0; i < 5; i++) {
            telemetry.addData("Test: ", i);
        }
    }
}
