package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class shooterSubsystem {
    private DcMotor intakeMotor;
    private DcMotor flywheelMotor;
    private DcMotor indexerMotor; // moves balls from intake to flywheel

    public void init(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        flywheelMotor = hardwareMap.get(DcMotor.class, "flywheelMotor");
        indexerMotor = hardwareMap.get(DcMotor.class, "indexerMotor");

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheelMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        indexerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setIntakePower(double power) {
        if (intakeMotor != null) {
            intakeMotor.setPower(power);
        }
    }

    public void setFlywheelPower(double power) {
        if (flywheelMotor != null) {
            flywheelMotor.setPower(power);
        }
    }

    public void setIndexerPower(double power) {
        if (indexerMotor != null) {
            indexerMotor.setPower(power);
        }
    }

    public void stopAll() {
        setIntakePower(0);
        setFlywheelPower(0);
        setIndexerPower(0);
    }
}
