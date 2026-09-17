package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name = "sudoOP26 Panels OpMode", group = "Panels")
public class sudoOP26 extends OpMode {
    public static boolean enablePanelAddons = true;
    public static String statusMessage = "Running";

    @Override
    public void init() {
        telemetry.addData("Status", "Initialized with fullpanels support.");
        telemetry.update();
    }

    @Override
    public void loop() {
        // These values can change live while the robot is running via sliders or textboxes on the Panels dashboard
        telemetry.addData("Status", statusMessage);
        telemetry.addData("Addons Enabled", enablePanelAddons);
        
        telemetry.update();
    }

}

